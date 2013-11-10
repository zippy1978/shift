/*
 * Shift remote control script.
 */

// Namespaces
window.Shift = {};
Shift.remoteControl = {};

Shift.remoteControl = {
            
    start: function() {

        ws = new WebSocket("ws://" + REMOTE_ADDRESS + ":" + REMOTE_PORT + "/remote-control/");
        ws.onopen = function(event) {
            
        };
        ws.onmessage = function(event) {
            window.location.href = event.data;
        };
        ws.onclose = function(event) {
            
        };
    }
};


// Start remote control
Shift.remoteControl.start();