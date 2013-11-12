package com.backelite.shift.gui.preview;

/*
 * #%L
 * RemoteHTMLPreviewWebSocket.java - Shift - 2013
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
import java.lang.String;
import java.util.HashMap;
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
    private static final String MESSAGE_TYPE_COMMAND = "COMMAND";
    private static final String COMMAND_ACTION = "REFRESH";
    protected Session session;
    protected BrowserInfo browserInfo;

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

                }

            } else {
                log.error("Unrecognized message received");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }


    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        
        this.session = session;
        BROADCAST.add(this);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        
        BROADCAST.remove(this);
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
            command.setName(COMMAND_ACTION);
            command.getParameters().put("url", url);
            String jsonMessage = OBJECT_MAPPER.writeValueAsString(command);

            for (RemoteHTMLPreviewWebSocket sock : BROADCAST) {
                sock.session.getRemote().sendStringByFuture(jsonMessage);
            }
            
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
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
