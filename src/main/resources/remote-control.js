/*
 * #%L
 * remote-control.js - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Shift
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
/*
 * Shift remote control script.
 */

// Namespaces
window.Shift = {};
Shift.remoteControl = {};

Shift.remoteControl = {
            
    start: function() {
        
        this._startMonitor();
        
        caller = this;

        ws = new WebSocket("ws://" + REMOTE_ADDRESS + ":" + REMOTE_PORT + "/remote-control/");
        ws.onopen = function(event) {
            
            // Retrieve browser info from session storage
            var browserInfo = caller._loadBrowserInfo();
            
            // If not found : build new one and store it
            if (!browserInfo) {
                browserInfo = {
                    type: 'BROWSER_INFO',
                    userAgent: navigator.userAgent
                };
                caller._storeBrowserInfo(browserInfo);
            }
            
            // Send browser info
            ws.send(JSON.stringify(browserInfo));
            
        };
        ws.onmessage = function(event) {
            
            var message = JSON.parse(event.data);
            switch (message.type) {
                case 'SESSION_INFO':
                    caller._handleSessionInfo(message);
                    break;
                case 'COMMAND':
                    caller._handleCommand(message);
                    break;
            }
            
        };
        ws.onclose = function(event) {
            
        };
    },
            
    _startMonitor: function() {

        var startTime = new Date().getTime();

        window.onload = function() {
            
            // Compute and send rendering time
            endTime = new Date().getTime();
            renderingTime = endTime - startTime;
            
            // Result is sent after a delay to prevent 
            // sending data while socket is not opened yet
            setTimeout(function() {
                ws.send(JSON.stringify({
                  type: 'RENDERING_TIME',
                  value: renderingTime
                }));
            }, 300);
            
        };

    },
            
    _handleSessionInfo: function(sessionInfo) {

        // Retrieve browser info from session storage
        var browserInfo = this._loadBrowserInfo();
        
        // Add session info
        if (browserInfo) {
            browserInfo.sessionInfo = sessionInfo;
        }
        
        // Store new browser info
        this._storeBrowserInfo(browserInfo);
        
    },
    
    _handleCommand: function(command) {
        
        // Handle commands
        switch(command.name) {
            case 'REFRESH':
                window.location.replace(command.parameters.url);
                break;
            case 'PING' :
                alert("Hello, is it me you're looking for ?");
                break;
        }
    },
            
    _storeBrowserInfo: function(browserInfo) {

        if (window.sessionStorage) {
            sessionStorage.setItem('browserInfo', JSON.stringify(browserInfo));
        }
    },
            
    _loadBrowserInfo : function() {

        var browserInfo = null;
        if (window.sessionStorage) {
            var browserInfoString = sessionStorage.getItem('browserInfo');
            if (browserInfoString) {
                browserInfo = JSON.parse(browserInfoString);
            }
        }
        
        return browserInfo;
    }
};


// Start remote control
Shift.remoteControl.start();