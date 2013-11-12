package com.backelite.shift.gui.preview;

/*
 * #%L
 * RemoteHTMLPreviewController.java - Shift - 2013
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
import com.backelite.shift.util.FileUtils;
import com.backelite.shift.util.NetworkUtils;
import com.backelite.shift.workspace.HTTPWorkspaceProxyServer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.LoggerFactory;

/**
 * Remote HTML preview.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class RemoteHTMLPreviewController extends AbstractPreviewController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RemoteHTMLPreviewController.class);
    private static final String REMOTE_CONTROL_SCRIPT_NAME = "remote-control.js";
    private static final String REMOTE_CONTROL_WEB_SOCKET_CONTEXT = "/remote-control";
    private static final String WORKSPACE_CONTEXT = "/workspace";
    private Server server;
    /**
     * Server port.
     */
    private int port = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);

        // Start server
        startServer();

    }

    @Override
    public void setParentStage(Stage parentStage) {
        super.setParentStage(parentStage);

        // Register stage close listener to stop server
        this.getParentStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
                stopServer();
            }
        });
    }

    private void startServer() {

        // Start Workspace HTTP server (if not done yet)
        HTTPWorkspaceProxyServer workspaceServer = ApplicationContext.getHTTPWorkspaceProxyServer();
        workspaceServer.start();


        // Find free port to start the server on ...
        port = NetworkUtils.findAvailablePort("localhost", workspaceServer.getPort() + 1, workspaceServer.getPort() + 1001);

        try {

            HandlerCollection handlers = new HandlerCollection();

            // Websocket
            WebSocketHandler webSocketHandler = new WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.register(RemoteHTMLPreviewWebSocket.class);
                }
            };
            ContextHandler webSocketContext = new ContextHandler();
            webSocketContext.setContextPath(REMOTE_CONTROL_WEB_SOCKET_CONTEXT);
            webSocketContext.setHandler(webSocketHandler);
            handlers.addHandler(webSocketContext);

            // Proxy
            ContextHandler workspaceContext = new ContextHandler();
            workspaceContext.setContextPath(WORKSPACE_CONTEXT);
            workspaceContext.setHandler(new ProxyHTTPHandler());
            handlers.addHandler(workspaceContext);
            
            // Root (force redirect to current document)
            ContextHandler rootContext = new ContextHandler();
            rootContext.setContextPath("/");
            rootContext.setHandler(new AbstractHandler() {

                public void handle(String tagret, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                    response.sendRedirect(String.format("%s%s", WORKSPACE_CONTEXT, document.getWorkspacePath()));
                }
            });
            handlers.addHandler(rootContext);

            server = new Server(port);
            server.setHandler(handlers);
            server.start();

            log.debug("Starting remote HTTP preview server");
        } catch (Exception ex) {
            log.error("Failed to start remote HTTP preview server", ex);
        }
    }

    private void stopServer() {
        if (server != null) {
            try {
                server.stop();
                server = null;
            } catch (Exception ex) {
                log.error("Failed to stop remote HTTP preview server", ex);
            }
        }
    }

    @Override
    protected void refresh() {
        
        String url = String.format("http://%s:%d%s%s", NetworkUtils.getHostIPAddress(), port, WORKSPACE_CONTEXT, document.getWorkspacePath());
        RemoteHTMLPreviewWebSocket.broadcastRefresh(url);
    }

    /**
     * Tries to inject <script> into HTML <head>
     *
     * @param html HTML content
     * @return HTML content with <script> of the remote control script inside
     */
    private String injectRemoteControlScript(String html) {

        String tag = String.format("<script>var REMOTE_PORT = %d;\nvar REMOTE_ADDRESS = '%s';</script>\n<script src=\"%s/%s\"></script>", port, NetworkUtils.getHostIPAddress(), WORKSPACE_CONTEXT, REMOTE_CONTROL_SCRIPT_NAME);

        // If <head> found : add script there
        if (html.toLowerCase().contains("<head>")) {
            return html.replaceFirst("<head>", String.format("<head>\n%s", tag)).replaceFirst("<HEAD>", String.format("<HEAD>\n%s", tag));

            // If <html> founf (but no <head>) : build <head> with script
        } else if (html.toLowerCase().contains("<html>")) {
            return html.replaceFirst("<html>", String.format("<html>\n<head>%s</head>", tag)).replaceFirst("<HTML>", String.format("<HTML>\n<head>%s</head>", tag));
        }

        // No HTML tag found : add script at the begining
        return String.format("%s%s", tag, html);

    }

    /**
     * Server handler. Proxify workspace server requests and inject remote
     * control JS on HTML files.
     */
    private class ProxyHTTPHandler extends AbstractHandler {

        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
 
            // If root context requested : render current document anyway
            if (target.equals("/")) {
                response.sendRedirect(String.format("%s%s", WORKSPACE_CONTEXT, document.getWorkspacePath()));
                return;
            }
            
            
            baseRequest.setHandled(true);

            // Remote control file requested
            if (target.equals(String.format("/%s", REMOTE_CONTROL_SCRIPT_NAME))) {
                String script = FileUtils.getFileContentAsStringFromClasspathResource(String.format("/%s", REMOTE_CONTROL_SCRIPT_NAME));
                response.setContentType("application/javascript");
                response.getOutputStream().write(script.getBytes());
                response.getOutputStream().flush();

                // Otherwise : proxy request
            } else {

                HttpClient client = new HttpClient();
                HttpMethod method = new GetMethod(String.format("http://localhost:%s%s", ApplicationContext.getHTTPWorkspaceProxyServer().getPort(), target.replaceFirst(WORKSPACE_CONTEXT, "")));
                int statusCode = client.executeMethod(method);

                // Set staus
                response.setStatus(statusCode);

                // Copy headers
                Header[] headers = method.getResponseHeaders();
                for (Header header : headers) {
                    response.setHeader(header.getName(), header.getValue());
                }

                // If content is HTML : try to inject remote-control.js
                Header contentTypeHeader = method.getResponseHeader("Content-Type");
                if (contentTypeHeader != null && contentTypeHeader.getValue().contains("html")) {
                    String body = new String(method.getResponseBody(), "UTF-8");
                    response.getOutputStream().write(injectRemoteControlScript(body).getBytes("UTF-8"));
                } else {
                    response.getOutputStream().write(method.getResponseBody());
                }

                response.getOutputStream().flush();

                method.releaseConnection();

            }

        }
    }
}
