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
                window.location.href = command.parameters.url;
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