package org.shiftedit.workspace;

/*
 * #%L
 * HTTPWorkspaceProxyServer.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import org.shiftedit.util.NetworkUtils;
import org.shiftedit.workspace.artifact.Artifact;
import org.shiftedit.workspace.artifact.Document;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedded HTTP server able to server workspace resources.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class HTTPWorkspaceProxyServer {

    private static final Logger log = LoggerFactory.getLogger(HTTPWorkspaceProxyServer.class);
    private Workspace workspace;
    private Server server;
    /**
     * Server port : starts at 9000 until a free port is found.
     */
    private int port = 9000;

    public HTTPWorkspaceProxyServer(Workspace workspace) {
        this.workspace = workspace;
    }

    public synchronized void start() {

        if (server == null) {
            try {
                // Get available port
                port = NetworkUtils.findAvailablePort("localhost", getPort(), getPort() + 1000);

                // Start server
                server = new Server(getPort());
                server.setHandler(new HTTPWorkspaceHandler());
                server.start();

                log.debug("Starting HTTP server");


            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }

    }

    public synchronized void stop() {

        // Send stop command to the running server (if running)
        if (server != null) {

            log.debug("Stopping HTTP server");
            try {
                server.stop();
                server = null;
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Workspace HTTP handler.
     */
    private class HTTPWorkspaceHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


            log.debug(String.format("Handling %s", request.getPathInfo()));
            baseRequest.setHandled(true);



            // Search for artifact matching path
            Artifact artifact = workspace.findArtifactByWorkspacePath(request.getPathInfo().replaceAll("\\+", " "));

            if (artifact != null) {
                // Only render documents.
                if (artifact instanceof Document) {

                    Document document = (Document) artifact;

                    synchronized (document) {

                        // Memorize opening state of the document : if it was closed before, it must be closed after processing
                        boolean wasOpened = document.isOpened();

                        document.open();

                        // Guess mime
                        String mime = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(document.getContent()));
                        if (mime == null) {
                            if (document.getName().toLowerCase().endsWith(".js")) {
                                mime = "text/javascript";
                            } else if (document.getName().toLowerCase().endsWith(".css")) {
                                mime = "text/css";
                            } else if (document.getName().toLowerCase().endsWith(".html")) {
                                mime = "text/html";
                            }
                        }

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType(String.format("%s;/charset=utf-8", mime));
                        response.getOutputStream().write(document.getContent());
                        response.getOutputStream().flush();
                        log.debug(String.format("Artifact %s found (mime is %s), sending data...", request.getPathInfo(), mime));

                        if (!wasOpened) {
                            document.close();
                        }

                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                // Not found...
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }


        }
    }
}
