/*
 * #%L
 * mark-selection.js - Shift - 2013
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
// Because sometimes you need to mark the selected *text*.
//
// Adds an option 'styleSelectedText' which, when enabled, gives
// selected text the CSS class "CodeMirror-selectedtext".

(function() {
  "use strict";

  CodeMirror.defineOption("styleSelectedText", false, function(cm, val, old) {
    var prev = old && old != CodeMirror.Init;
    if (val && !prev) {
      updateSelectedText(cm);
      cm.on("cursorActivity", updateSelectedText);
    } else if (!val && prev) {
      cm.off("cursorActivity", updateSelectedText);
      clearSelectedText(cm);
      delete cm._selectionMark;
    }
  });

  function clearSelectedText(cm) {
    if (cm._selectionMark) cm._selectionMark.clear();
  }

  function updateSelectedText(cm) {
    clearSelectedText(cm);

    if (cm.somethingSelected())
      cm._selectionMark = cm.markText(cm.getCursor("start"), cm.getCursor("end"),
                                      {className: "CodeMirror-selectedtext"});
    else
      cm._selectionMark = null;
  }
})();
