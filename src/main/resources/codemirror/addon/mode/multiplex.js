/*
 * #%L
 * multiplex.js - Shift - 2013
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
CodeMirror.multiplexingMode = function(outer /*, others */) {
  // Others should be {open, close, mode [, delimStyle]} objects
  var others = Array.prototype.slice.call(arguments, 1);
  var n_others = others.length;

  function indexOf(string, pattern, from) {
    if (typeof pattern == "string") return string.indexOf(pattern, from);
    var m = pattern.exec(from ? string.slice(from) : string);
    return m ? m.index + from : -1;
  }

  return {
    startState: function() {
      return {
        outer: CodeMirror.startState(outer),
        innerActive: null,
        inner: null
      };
    },

    copyState: function(state) {
      return {
        outer: CodeMirror.copyState(outer, state.outer),
        innerActive: state.innerActive,
        inner: state.innerActive && CodeMirror.copyState(state.innerActive.mode, state.inner)
      };
    },

    token: function(stream, state) {
      if (!state.innerActive) {
        var cutOff = Infinity, oldContent = stream.string;
        for (var i = 0; i < n_others; ++i) {
          var other = others[i];
          var found = indexOf(oldContent, other.open, stream.pos);
          if (found == stream.pos) {
            stream.match(other.open);
            state.innerActive = other;
            state.inner = CodeMirror.startState(other.mode, outer.indent ? outer.indent(state.outer, "") : 0);
            return other.delimStyle;
          } else if (found != -1 && found < cutOff) {
            cutOff = found;
          }
        }
        if (cutOff != Infinity) stream.string = oldContent.slice(0, cutOff);
        var outerToken = outer.token(stream, state.outer);
        if (cutOff != Infinity) stream.string = oldContent;
        return outerToken;
      } else {
        var curInner = state.innerActive, oldContent = stream.string;
        var found = indexOf(oldContent, curInner.close, stream.pos);
        if (found == stream.pos) {
          stream.match(curInner.close);
          state.innerActive = state.inner = null;
          return curInner.delimStyle;
        }
        if (found > -1) stream.string = oldContent.slice(0, found);
        var innerToken = curInner.mode.token(stream, state.inner);
        if (found > -1) stream.string = oldContent;
        var cur = stream.current(), found = cur.indexOf(curInner.close);
        if (found > -1) stream.backUp(cur.length - found);
        return innerToken;
      }
    },

    indent: function(state, textAfter) {
      var mode = state.innerActive ? state.innerActive.mode : outer;
      if (!mode.indent) return CodeMirror.Pass;
      return mode.indent(state.innerActive ? state.inner : state.outer, textAfter);
    },

    blankLine: function(state) {
      var mode = state.innerActive ? state.innerActive.mode : outer;
      if (mode.blankLine) {
        mode.blankLine(state.innerActive ? state.inner : state.outer);
      }
      if (!state.innerActive) {
        for (var i = 0; i < n_others; ++i) {
          var other = others[i];
          if (other.open === "\n") {
            state.innerActive = other;
            state.inner = CodeMirror.startState(other.mode, mode.indent ? mode.indent(state.outer, "") : 0);
          }
        }
      } else if (state.innerActive.close === "\n") {
        state.innerActive = state.inner = null;
      }
    },

    electricChars: outer.electricChars,

    innerMode: function(state) {
      return state.inner ? {state: state.inner, mode: state.innerActive.mode} : {state: state.outer, mode: outer};
    }
  };
};
