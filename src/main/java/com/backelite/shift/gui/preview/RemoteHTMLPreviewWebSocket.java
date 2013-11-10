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
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * WebSocket server used for remote control communication.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
@WebSocket
public class RemoteHTMLPreviewWebSocket {

    private static final ConcurrentLinkedQueue<RemoteHTMLPreviewWebSocket> BROADCAST = new ConcurrentLinkedQueue<RemoteHTMLPreviewWebSocket>();
    
    protected Session session;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
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
     * Broadcast text to every connected session.
     * @param text Test message
     */
    public static void broadcastTest(String text) {
        
        for (RemoteHTMLPreviewWebSocket sock : BROADCAST)
        {
            sock.session.getRemote().sendStringByFuture(text);
        }
    }
}
