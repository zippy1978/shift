package org.shiftedit.gui.preview.html;

/*
 * #%L
 * RemoteHTMLPreviewController.java - Shift - 2013
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

import org.shiftedit.ApplicationContext;
import org.shiftedit.gui.preview.AbstractPreviewController;
import org.shiftedit.util.FileUtils;
import org.shiftedit.util.MemoryUtils;
import org.shiftedit.util.NetworkUtils;
import org.shiftedit.workspace.HTTPWorkspaceProxyServer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
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
public class RemoteHTMLPreviewController extends AbstractPreviewController implements RemoteHTMLPreviewWebSocket.RemoteHTMLPreviewWebSocketListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RemoteHTMLPreviewController.class);
    private static final String REMOTE_CONTROL_SCRIPT_NAME = "remote-control.js";
    private static final String REMOTE_CONTROL_WEB_SOCKET_CONTEXT = "/remote-control";
    private static final String WORKSPACE_CONTEXT = "/workspace";
    
    @FXML
    private Label instructionsLabel;
    
    @FXML
    private Hyperlink urlLink;
    
    @FXML
    private TableView connectionTable;
    @FXML
    private ToggleButton trackActiveFileToggleButton;
    
    private final ObservableList<RemoteHTMLPreviewWebSocket> tableModel = FXCollections.observableArrayList();
    
    /**
     * Indicate if remote preview is already running.
     * Only one remote preview is allowed at the time.
     */
    private static boolean started = false;
    
    private Server server;
    /**
     * Server port.
     */
    private int port = 0;
    
    private EventHandler<ActionEvent> urlLinkActionEventHandler;
    private ChangeListener<Boolean> trackActiveFileChangeListener;
    private EventHandler<MouseEvent> tableCellMouseEventHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        if (started) {
            displayInfoDialog(getResourceBundle().getString("builtin.plugin.preview.remote_html.title"), getResourceBundle().getString("builtin.plugin.preview.remote_html.already_running.text"));
            Platform.runLater(() -> {
                close();
            });
            
            
        } else {
            
            // Start server
            startServer();
            
            // URL click
            urlLinkActionEventHandler = (ActionEvent t) -> {
                ApplicationContext.getHostServices().showDocument(urlLink.getText());
            };
            urlLink.setOnAction(new WeakEventHandler<>(urlLinkActionEventHandler));
            
            // Table view setup
            this.setupConnectionTable();
            
            // Bind tracking button state
            trackActiveFileChangeListener = (ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
                setActiveDocumentTrackingEnabled(t1);
            };
            trackActiveFileToggleButton.selectedProperty().addListener(new WeakChangeListener<>(trackActiveFileChangeListener));
            trackActiveFileToggleButton.setSelected(true);
        }
        

        
        // Later ...
        Platform.runLater(() -> {
            getStage().setTitle(getResourceBundle().getString("builtin.plugin.preview.remote_html.title"));
        });
    }
    
    
    private void setupConnectionTable() {
        
        // Cell click handler
        tableCellMouseEventHandler = (MouseEvent t) -> {
            TableCell c = (TableCell) t.getSource();
            int index = c.getIndex();
            
            // Send ping request on double click
            if (t.getClickCount() == 2) {
                tableModel.get(index).ping();
            }
        };
        
        // Cell factory
        Callback<TableColumn, TableCell> cellFactory =
                (TableColumn p) -> {
                    TextFieldTableCell cell = new TextFieldTableCell();
                    cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new WeakEventHandler<>(tableCellMouseEventHandler));
                    return cell;
        };
        
        
        // Remote address
        TableColumn remoteAddressCol = new TableColumn(getResourceBundle().getString("builtin.plugin.preview.remote_html.remote_address"));
        remoteAddressCol.setMinWidth(100);
        remoteAddressCol.setCellValueFactory(new PropertyValueFactory<>("remoteAddress"));
        remoteAddressCol.setCellFactory(cellFactory);
        connectionTable.getColumns().add(remoteAddressCol);
        
        // User agent
        TableColumn userAgentCol = new TableColumn(getResourceBundle().getString("builtin.plugin.preview.remote_html.user_agent"));
        userAgentCol.setMinWidth(200);
        userAgentCol.setCellValueFactory(new PropertyValueFactory<>("userAgent"));
        userAgentCol.setCellFactory(cellFactory);
        connectionTable.getColumns().add(userAgentCol);
        
        // Rendering time
        TableColumn renderingTimeCol = new TableColumn(getResourceBundle().getString("builtin.plugin.preview.remote_html.rendering_time"));
        renderingTimeCol.setMinWidth(200);
        renderingTimeCol.setCellValueFactory(new PropertyValueFactory<>("renderingTime"));
        renderingTimeCol.setCellFactory(cellFactory);
        connectionTable.getColumns().add(renderingTimeCol);
        
        connectionTable.setPlaceholder(new Label(getResourceBundle().getString("builtin.plugin.preview.remote_html.no_connection")));
        
        connectionTable.setItems(tableModel);
        
    }

    @Override
    public void close() {
        super.close();
        stopServer();
        
        // Table clean up (if cell factory is not removed = memory leak)
        MemoryUtils.cleanUpTableView(connectionTable);
        
    }
   
    private void startServer() {
        
        started = true;
        
        // Listen to client browser connections
        RemoteHTMLPreviewWebSocket.setListener(this);

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

                @Override
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
        
        log.debug("Stopping remote HTTP preview server");
        
        // Clear listener
        RemoteHTMLPreviewWebSocket.setListener(null);
        
        started = false;
        
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
        
        String url = String.format("http://%s:%d", NetworkUtils.getHostIPAddress(), port);
        RemoteHTMLPreviewWebSocket.broadcastRefresh(url);
        
        urlLink.setText(url);
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

        @Override
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
                HttpMethod method = new GetMethod(String.format("http://localhost:%s%s", ApplicationContext.getHTTPWorkspaceProxyServer().getPort(), target.replaceFirst(WORKSPACE_CONTEXT, "").replaceAll(" ", "+")));
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

    @Override
    public void onConnectionAdded(RemoteHTMLPreviewWebSocket connection) {
        
        log.debug("Adding connection");
        tableModel.add(connection);
    }

    @Override
    public void onConnectionRemoved(RemoteHTMLPreviewWebSocket connection) {
        
        log.debug("Removing connection");
        tableModel.remove(connection);
    }

    @Override
    public void onConnectionDataUpdated(RemoteHTMLPreviewWebSocket connection) {
        
        ObservableList<RemoteHTMLPreviewWebSocket> newModel = FXCollections.observableArrayList(tableModel);
        
        // Refresh table
        // This refresh is ugly (flickering, but it seems there is no better support for that at the moment)
        synchronized(tableModel) {
            tableModel.removeAll(tableModel);
            tableModel.addAll(newModel);        
        }
        
        
        
    }

    
    
    
    
}
