/*
 * #%L
 * match-highlighter.js - Shift - 2013
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
// Highlighting text that matches the selection
//
// Defines an option highlightSelectionMatches, which, when enabled,
// will style strings that match the selection throughout the
// document.
//
// The option can be set to true to simply enable it, or to a
// {minChars, style} object to explicitly configure it. minChars is
// the minimum amount of characters that should be selected for the
// behavior to occur, and style is the token style to apply to the
// matches. This will be prefixed by "cm-" to create an actual CSS
// class name.

(function() {
  var DEFAULT_MIN_CHARS = 2;
  var DEFAULT_TOKEN_STYLE = "matchhighlight";

  function State(options) {
    this.minChars = typeof options == "object" && options.minChars || DEFAULT_MIN_CHARS;
    this.style = typeof options == "object" && options.style || DEFAULT_TOKEN_STYLE;
    this.overlay = null;
  }

  CodeMirror.defineOption("highlightSelectionMatches", false, function(cm, val, old) {
    var prev = old && old != CodeMirror.Init;
    if (val && !prev) {
      cm._matchHighlightState = new State(val);
      cm.on("cursorActivity", highlightMatches);
    } else if (!val && prev) {
      var over = cm._matchHighlightState.overlay;
      if (over) cm.removeOverlay(over);
      cm._matchHighlightState = null;
      cm.off("cursorActivity", highlightMatches);
    }
  });

  function highlightMatches(cm) {
    cm.operation(function() {
      var state = cm._matchHighlightState;
      if (state.overlay) {
        cm.removeOverlay(state.overlay);
        state.overlay = null;
      }

      if (!cm.somethingSelected()) return;
      var selection = cm.getSelection().replace(/^\s+|\s+$/g, "");
      if (selection.length < state.minChars) return;

      cm.addOverlay(state.overlay = makeOverlay(selection, state.style));
    });
  }

  function makeOverlay(query, style) {
    return {token: function(stream) {
      if (stream.match(query)) return style;
      stream.next();
      stream.skipTo(query.charAt(0)) || stream.skipToEnd();
    }};
  }
})();
