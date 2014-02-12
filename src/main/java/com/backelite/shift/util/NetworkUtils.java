package com.backelite.shift.util;

/*
 * #%L
 * NetworkUtils.java - shift - 2013
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    /**
     * Return the current machine IP address.
     * If not found or error : returns 127.0.0.1
     * @return 
     */
    public static String getHostIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return "127.0.0.1";
        }
    }
}
