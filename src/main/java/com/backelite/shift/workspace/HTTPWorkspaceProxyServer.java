package com.backelite.shift.workspace;

/*
 * #%L
 * HTTPWorkspaceProxyServer.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import com.backelite.shift.ApplicationContext;
import com.backelite.shift.workspace.artifact.Artifact;
import com.backelite.shift.workspace.artifact.Document;
import com.backelite.shift.util.NetworkUtils;
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
