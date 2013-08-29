/*
 * #%L
 * vbscript.js - Shift - 2013
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
CodeMirror.defineMode("vbscript", function() {
  var regexVBScriptKeyword = /^(?:Call|Case|CDate|Clear|CInt|CLng|Const|CStr|Description|Dim|Do|Each|Else|ElseIf|End|Err|Error|Exit|False|For|Function|If|LCase|Loop|LTrim|Next|Nothing|Now|Number|On|Preserve|Quit|ReDim|Resume|RTrim|Select|Set|Sub|Then|To|Trim|True|UBound|UCase|Until|VbCr|VbCrLf|VbLf|VbTab)$/im;

  return {
    token: function(stream) {
      if (stream.eatSpace()) return null;
      var ch = stream.next();
      if (ch == "'") {
        stream.skipToEnd();
        return "comment";
      }
      if (ch == '"') {
        stream.skipTo('"');
        return "string";
      }

      if (/\w/.test(ch)) {
        stream.eatWhile(/\w/);
        if (regexVBScriptKeyword.test(stream.current())) return "keyword";
      }
      return null;
    }
  };
});

CodeMirror.defineMIME("text/vbscript", "vbscript");
