package com.backelite.shift.util;

/*
 * #%L
 * NetworkUtils.java - shift - 2013
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class NetworkUtils {

    /**
     * Find an available port within a port range on a host
     *
     * @param hostname Host name
     * @param startPort Port number starting the range
     * @param endPort Port number ending the range
     * @return An available port number, or 0 if none is available.
     */
    public static synchronized int findAvailablePort(String hostname,
            int startPort, int endPort) {

        for (int port = startPort; port < (endPort + 1); port++) {

            try {
                Socket socket = new Socket(InetAddress.getByName(hostname),
                        port);
                // port not available
                socket.close();
            } catch (IOException e) {
                // port available
                return port;
            }
        }

        return 0;
    }

    /**
     * Wait until a port is listening on a given host. An exception is thrown
     * if the timeout is excedeed.
     *
     * @param hostname Host name
     * @param port Port number
     * @param timeout Timeout in seconds
     * @throws IOException If a connection error occurs or if the timeout is
     * exceeded
     */
    public static void waitUntilPortListening(String hostname, int port,
            int timeout) throws IOException {

        int i = 0;
        while (i < timeout) {

            // try to get connection
            try {
                Socket socket = new Socket(InetAddress.getByName(hostname),
                        port);
                // connection OK: exit
                socket.close();
                return;
            } catch (IOException e) {
                // nothing
            }

            i++;

            // wait for 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // nothing
            }
        }

        throw new IOException("Timeout waiting for port " + port + " to listen");
    }
}
