/*
 * #%L
 * indent-fold.js - Shift - 2013
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
CodeMirror.indentRangeFinder = function(cm, start) {
  var tabSize = cm.getOption("tabSize"), firstLine = cm.getLine(start.line);
  var myIndent = CodeMirror.countColumn(firstLine, null, tabSize);
  for (var i = start.line + 1, end = cm.lineCount(); i < end; ++i) {
    var curLine = cm.getLine(i);
    if (CodeMirror.countColumn(curLine, null, tabSize) < myIndent &&
        CodeMirror.countColumn(cm.getLine(i-1), null, tabSize) > myIndent)
      return {from: CodeMirror.Pos(start.line, firstLine.length),
              to: CodeMirror.Pos(i, curLine.length)};
  }
};
