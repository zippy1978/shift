/*
 * Author: Constantin Jucovschi (c.jucovschi@jacobs-university.de)
 * Licence: MIT
 */

CodeMirror.defineMode("stex", function() {
    "use strict";

    function pushCommand(state, command) {
        state.cmdState.push(command);
    }

    function peekCommand(state) {
        if (state.cmdState.length > 0) {
            return state.cmdState[state.cmdState.length - 1];
        } else {
            return null;
        }
    }

    function popCommand(state) {
        var plug = state.cmdState.pop();
        if (plug) {
            plug.closeBracket();
        }
    }

    // returns the non-default plugin closest to the end of the list
    function getMostPowerful(state) {
        var context = state.cmdState;
        for (var i = context.length - 1; i >= 0; i--) {
            var plug = context[i];
            if (plug.name == "DEFAULT") {
                continue;
            }
            return plug;
        }
        return { styleIdentifier: function() { return null; } };
    }

    function addPluginPattern(pluginName, cmdStyle, styles) {
        return function () {
            this.name = pluginName;
            this.bracketNo = 0;
            this.style = cmdStyle;
            this.styles = styles;
            this.argument = null;   // \begin and \end have arguments that follow. These are stored in the plugin

            this.styleIdentifier = function() {
                return this.styles[this.bracketNo - 1] || null;
            };
            this.openBracket = function() {
                this.bracketNo++;
                return "bracket";
            };
            this.closeBracket = function() {};
        };
    }

    var plugins = {};

    plugins["importmodule"] = addPluginPattern("importmodule", "tag", ["string", "builtin"]);
    plugins["documentclass"] = addPluginPattern("documentclass", "tag", ["", "atom"]);
    plugins["usepackage"] = addPluginPattern("usepackage", "tag", ["atom"]);

/*
 * #%L
 * stex.js - Shift - 2013
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
    plugins["begin"] = addPluginPattern("begin", "tag", ["atom"]);
    plugins["end"] = addPluginPattern("end", "tag", ["atom"]);

    plugins["DEFAULT"] = function () {
        this.name = "DEFAULT";
        this.style = "tag";

        this.styleIdentifier = this.openBracket = this.closeBracket = function() {};
    };

    function setState(state, f) {
        state.f = f;
    }

    // called when in a normal (no environment) context
    function normal(source, state) {
        var plug;
        // Do we look like '\command' ?  If so, attempt to apply the plugin 'command'
        if (source.match(/^\\[a-zA-Z@]+/)) {
            var cmdName = source.current().slice(1);
            plug = plugins[cmdName] || plugins["DEFAULT"];
            plug = new plug();
            pushCommand(state, plug);
            setState(state, beginParams);
            return plug.style;
        }

        // escape characters
        if (source.match(/^\\[$&%#{}_]/)) {
          return "tag";
        }

        // white space control characters
        if (source.match(/^\\[,;!\/]/)) {
          return "tag";
        }

        // find if we're starting various math modes
        if (source.match("\\[")) {
            setState(state, function(source, state){ return inMathMode(source, state, "\\]"); });
            return "keyword";
        }
        if (source.match("$$")) {
            setState(state, function(source, state){ return inMathMode(source, state, "$$"); });
            return "keyword";
        }
        if (source.match("$")) {
            setState(state, function(source, state){ return inMathMode(source, state, "$"); });
            return "keyword";
        }

        var ch = source.next();
        if (ch == "%") {
            // special case: % at end of its own line; stay in same state
            if (!source.eol()) {
              setState(state, inCComment);
            }
            return "comment";
        }
        else if (ch == '}' || ch == ']') {
            plug = peekCommand(state);
            if (plug) {
                plug.closeBracket(ch);
                setState(state, beginParams);
            } else {
                return "error";
            }
            return "bracket";
        } else if (ch == '{' || ch == '[') {
            plug = plugins["DEFAULT"];
            plug = new plug();
            pushCommand(state, plug);
            return "bracket";
        }
        else if (/\d/.test(ch)) {
            source.eatWhile(/[\w.%]/);
            return "atom";
        }
        else {
            source.eatWhile(/[\w\-_]/);
            plug = getMostPowerful(state);
            if (plug.name == 'begin') {
                plug.argument = source.current();
            }
            return plug.styleIdentifier();
        }
    }

    function inCComment(source, state) {
        source.skipToEnd();
        setState(state, normal);
        return "comment";
    }

    function inMathMode(source, state, endModeSeq) {
        if (source.eatSpace()) {
            return null;
        }
        if (source.match(endModeSeq)) {
            setState(state, normal);
            return "keyword";
        }
        if (source.match(/^\\[a-zA-Z@]+/)) {
            return "tag";
        }
        if (source.match(/^[a-zA-Z]+/)) {
            return "variable-2";
        }
        // escape characters
        if (source.match(/^\\[$&%#{}_]/)) {
          return "tag";
        }
        // white space control characters
        if (source.match(/^\\[,;!\/]/)) {
          return "tag";
        }
        // special math-mode characters
        if (source.match(/^[\^_&]/)) {
          return "tag";
        }
        // non-special characters
        if (source.match(/^[+\-<>|=,\/@!*:;'"`~#?]/)) {
            return null;
        }
        if (source.match(/^(\d+\.\d*|\d*\.\d+|\d+)/)) {
          return "number";
        }
        var ch = source.next();
        if (ch == "{" || ch == "}" || ch == "[" || ch == "]" || ch == "(" || ch == ")") {
            return "bracket";
        }

        // eat comments here, because inCComment returns us to normal state!
        if (ch == "%") {
            if (!source.eol()) {
                source.skipToEnd();
            }
            return "comment";
        }
        return "error";
    }

    function beginParams(source, state) {
        var ch = source.peek(), lastPlug;
        if (ch == '{' || ch == '[') {
            lastPlug = peekCommand(state);
            lastPlug.openBracket(ch);
            source.eat(ch);
            setState(state, normal);
            return "bracket";
        }
        if (/[ \t\r]/.test(ch)) {
            source.eat(ch);
            return null;
        }
        setState(state, normal);
        popCommand(state);

        return normal(source, state);
    }

    return {
        startState: function() {
            return {
                cmdState: [],
                f: normal
            };
        },
        copyState: function(s) {
            return {
                cmdState: s.cmdState.slice(),
                f: s.f
            };
        },
        token: function(stream, state) {
            return state.f(stream, state);
        }
    };
});

CodeMirror.defineMIME("text/x-stex", "stex");
CodeMirror.defineMIME("text/x-latex", "stex");
