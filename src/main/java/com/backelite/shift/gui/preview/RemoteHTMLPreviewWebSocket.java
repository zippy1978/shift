package com.backelite.shift.gui.preview;

/*
 * #%L
 * RemoteHTMLPreviewWebSocket.java - Shift - 2013
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.LoggerFactory;

/**
 * WebSocket server used for remote control communication.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
@WebSocket
public class RemoteHTMLPreviewWebSocket {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RemoteHTMLPreviewWebSocket.class);
    private static final ConcurrentLinkedQueue<RemoteHTMLPreviewWebSocket> BROADCAST = new ConcurrentLinkedQueue<RemoteHTMLPreviewWebSocket>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String MESSAGE_TYPE_BROWSER_INFO = "BROWSER_INFO";
    private static final String MESSAGE_TYPE_SESSION_INFO = "SESSION_INFO";
    private static final String MESSAGE_TYPE_RENDERING_TIME = "RENDERING_TIME";
    private static final String MESSAGE_TYPE_COMMAND = "COMMAND";
    private static final String COMMAND_REFRESH = "REFRESH";
    private static final String COMMAND_PING = "PING";
    protected Session session;
    protected BrowserInfo browserInfo;
    protected MonitoringInfo monitoringInfo = new MonitoringInfo();
    /**
     * Static listener.
     */
    private static WeakReference<RemoteHTMLPreviewWebSocketListener> listener = new WeakReference<>(null);

    public interface RemoteHTMLPreviewWebSocketListener {

        public void onConnectionAdded(RemoteHTMLPreviewWebSocket connection);

        public void onConnectionRemoved(RemoteHTMLPreviewWebSocket connection);

        public void onConnectionDataUpdated(RemoteHTMLPreviewWebSocket connection);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {

        String messageType = this.getMessageType(message);

        try {

            if (messageType != null) {

                // Browser info
                if (MESSAGE_TYPE_BROWSER_INFO.equals(messageType)) {

                    // Store browser info
                    browserInfo = OBJECT_MAPPER.readValue(message, BrowserInfo.class);

                    // Send back session info if not provided in browser info
                    if (browserInfo.getSessionInfo() == null) {
                        SessionInfo sessionInfo = new SessionInfo();
                        sessionInfo.setSessionId(session.hashCode());
                        browserInfo.setSessionInfo(sessionInfo);
                        String jsonMessage = OBJECT_MAPPER.writeValueAsString(sessionInfo);
                        session.getRemote().sendStringByFuture(jsonMessage);
                    }

                    log.debug(String.format("New browser connected %s", browserInfo.getUserAgent()));

                    // Rendering time
                } else if (MESSAGE_TYPE_RENDERING_TIME.equals(messageType)) {

                    Map<String, Object> messageMap = OBJECT_MAPPER.readValue(message, Map.class);

                    this.monitoringInfo.setRenderingTime((Integer) messageMap.get("value"));

                } else {
                    log.error("Unrecognized message received");
                }

                // Notify
                if (listener.get() != null) {
                    listener.get().onConnectionDataUpdated(this);
                }

            }


        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }


    }

    @OnWebSocketConnect
    public void onConnect(Session session) {

        this.session = session;
        BROADCAST.add(this);

        // Notify
        if (listener.get() != null) {
            listener.get().onConnectionAdded(this);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {

        BROADCAST.remove(this);

        // Notify
        if (listener.get() != null) {
            listener.get().onConnectionRemoved(this);
        }
    }

    /**
     * Return message type of received message
     *
     * @param message Received message
     * @return Message type
     */
    private String getMessageType(String message) {

        try {
            HashMap<String, String> parsedMessage = OBJECT_MAPPER.readValue(message, HashMap.class);
            return parsedMessage.get("type");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Broadcast refresh command to every connected browser.
     */
    public static void broadcastRefresh(String url) {

        try {
            Command command = new Command();
            command.setName(COMMAND_REFRESH);
            command.getParameters().put("url", url);
            String jsonMessage = OBJECT_MAPPER.writeValueAsString(command);

            for (RemoteHTMLPreviewWebSocket sock : BROADCAST) {
                sock.session.getRemote().sendStringByFuture(jsonMessage);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static void setListener(RemoteHTMLPreviewWebSocketListener uniqueListener) {
        listener = new WeakReference<>(uniqueListener);
    }

    public static List<RemoteHTMLPreviewWebSocket> getConnections() {
        return new ArrayList<>(BROADCAST);
    }

    /**
     * Request ping to remote browser.
     */
    public void ping() {

        try {
            Command command = new Command();
            command.setName(COMMAND_PING);
            String jsonMessage = OBJECT_MAPPER.writeValueAsString(command);

            this.session.getRemote().sendStringByFuture(jsonMessage);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

    }

    public String getUserAgent() {
        if (this.browserInfo != null) {
            return this.browserInfo.getUserAgent();
        } else {
            return "";
        }
    }

    public String getRemoteAddress() {
        return this.session.getRemoteAddress().getHostString();
    }

    public int getRenderingTime() {
        return this.monitoringInfo.getRenderingTime();
    }

    /**
     * Client browser info.
     */
    public static class BrowserInfo {

        private String userAgent;
        private String type = MESSAGE_TYPE_BROWSER_INFO;
        private SessionInfo sessionInfo;

        /**
         * @return the userAgent
         */
        public String getUserAgent() {
            return userAgent;
        }

        /**
         * @param userAgent the userAgent to set
         */
        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the sessionInfo
         */
        public SessionInfo getSessionInfo() {
            return sessionInfo;
        }

        /**
         * @param sessionInfo the sessionInfo to set
         */
        public void setSessionInfo(SessionInfo sessionInfo) {
            this.sessionInfo = sessionInfo;
        }
    }

    /**
     * Websocket session info.
     */
    public static class SessionInfo {

        private int sessionId;
        private String type = MESSAGE_TYPE_SESSION_INFO;

        /**
         * @return the sessionId
         */
        public int getSessionId() {
            return sessionId;
        }

        /**
         * @param sessionId the sessionId to set
         */
        public void setSessionId(int sessionId) {
            this.sessionId = sessionId;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * Holds monitoring information.
     */
    public static class MonitoringInfo {

        /**
         * Page rendering time in ms.
         */
        private int renderingTime;

        /**
         * @return the renderingTime
         */
        public int getRenderingTime() {
            return renderingTime;
        }

        /**
         * @param renderingTime the renderingTime to set
         */
        public void setRenderingTime(int renderingTime) {
            this.renderingTime = renderingTime;
        }
    }

    /**
     * Command object. Used to send remote command to browser.
     */
    public static class Command {

        private String name;
        private Map<String, String> parameters = new HashMap<String, String>();
        private String type = MESSAGE_TYPE_COMMAND;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the parameters
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * @param parameters the parameters to set
         */
        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(String type) {
            this.type = type;
        }
    }
}
