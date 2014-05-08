/*
 * #%L
 * xml-fold.js - Shift - 2013
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
CodeMirror.tagRangeFinder = (function() {
  var nameStartChar = "A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD";
  var nameChar = nameStartChar + "\-\:\.0-9\\u00B7\\u0300-\\u036F\\u203F-\\u2040";
  var xmlTagStart = new RegExp("<(/?)([" + nameStartChar + "][" + nameChar + "]*)", "g");

  return function(cm, start) {
    var line = start.line, ch = start.ch, lineText = cm.getLine(line);

    function nextLine() {
      if (line >= cm.lastLine()) return;
      ch = 0;
      lineText = cm.getLine(++line);
      return true;
    }
    function toTagEnd() {
      for (;;) {
        var gt = lineText.indexOf(">", ch);
        if (gt == -1) { if (nextLine()) continue; else return; }
        var lastSlash = lineText.lastIndexOf("/", gt);
        var selfClose = lastSlash > -1 && /^\s*$/.test(lineText.slice(lastSlash + 1, gt));
        ch = gt + 1;
        return selfClose ? "selfClose" : "regular";
      }
    }
    function toNextTag() {
      for (;;) {
        xmlTagStart.lastIndex = ch;
        var found = xmlTagStart.exec(lineText);
        if (!found) { if (nextLine()) continue; else return; }
        ch = found.index + found[0].length;
        return found;
      }
    }

    var stack = [], startCh;
    for (;;) {
      var openTag = toNextTag(), end;
      if (!openTag || line != start.line || !(end = toTagEnd())) return;
      if (!openTag[1] && end != "selfClose") {
        stack.push(openTag[2]);
        startCh = ch;
        break;
      }
    }

    for (;;) {
      var next = toNextTag(), end, tagLine = line, tagCh = ch - (next ? next[0].length : 0);
      if (!next || !(end = toTagEnd())) return;
      if (end == "selfClose") continue;
      if (next[1]) { // closing tag
        for (var i = stack.length - 1; i >= 0; --i) if (stack[i] == next[2]) {
          stack.length = i;
          break;
        }
        if (!stack.length) return {
          from: CodeMirror.Pos(start.line, startCh),
          to: CodeMirror.Pos(tagLine, tagCh)
        };
      } else { // opening tag
        stack.push(next[2]);
      }
    }
  };
})();
