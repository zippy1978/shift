/*
 * #%L
 * test.js - Shift - 2013
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
(function() {
  var mode = CodeMirror.getMode({tabSize: 4}, "gfm");
  function MT(name) { test.mode(name, mode, Array.prototype.slice.call(arguments, 1)); }

  MT("emInWordAsterisk",
     "foo[em *bar*]hello");

  MT("emInWordUnderscore",
     "foo_bar_hello");

  MT("emStrongUnderscore",
     "[strong __][em&strong _foo__][em _] bar");

  MT("fencedCodeBlocks",
     "[comment ```]",
     "[comment foo]",
     "",
     "[comment ```]",
     "bar");

  MT("fencedCodeBlockModeSwitching",
     "[comment ```javascript]",
     "[variable foo]",
     "",
     "[comment ```]",
     "bar");

  MT("taskListAsterisk",
     "[variable-2 * []] foo]", // Invalid; must have space or x between []
     "[variable-2 * [ ]]bar]", // Invalid; must have space after ]
     "[variable-2 * [x]]hello]", // Invalid; must have space after ]
     "[variable-2 * ][meta [ ]]][variable-2  [world]]]", // Valid; tests reference style links
     "    [variable-3 * ][property [x]]][variable-3  foo]"); // Valid; can be nested

  MT("taskListPlus",
     "[variable-2 + []] foo]", // Invalid; must have space or x between []
     "[variable-2 + [ ]]bar]", // Invalid; must have space after ]
     "[variable-2 + [x]]hello]", // Invalid; must have space after ]
     "[variable-2 + ][meta [ ]]][variable-2  [world]]]", // Valid; tests reference style links
     "    [variable-3 + ][property [x]]][variable-3  foo]"); // Valid; can be nested

  MT("taskListDash",
     "[variable-2 - []] foo]", // Invalid; must have space or x between []
     "[variable-2 - [ ]]bar]", // Invalid; must have space after ]
     "[variable-2 - [x]]hello]", // Invalid; must have space after ]
     "[variable-2 - ][meta [ ]]][variable-2  [world]]]", // Valid; tests reference style links
     "    [variable-3 - ][property [x]]][variable-3  foo]"); // Valid; can be nested

  MT("taskListNumber",
     "[variable-2 1. []] foo]", // Invalid; must have space or x between []
     "[variable-2 2. [ ]]bar]", // Invalid; must have space after ]
     "[variable-2 3. [x]]hello]", // Invalid; must have space after ]
     "[variable-2 4. ][meta [ ]]][variable-2  [world]]]", // Valid; tests reference style links
     "    [variable-3 1. ][property [x]]][variable-3  foo]"); // Valid; can be nested

  MT("SHA",
     "foo [link be6a8cc1c1ecfe9489fb51e4869af15a13fc2cd2] bar");

  MT("shortSHA",
     "foo [link be6a8cc] bar");

  MT("tooShortSHA",
     "foo be6a8c bar");

  MT("longSHA",
     "foo be6a8cc1c1ecfe9489fb51e4869af15a13fc2cd22 bar");

  MT("badSHA",
     "foo be6a8cc1c1ecfe9489fb51e4869af15a13fc2cg2 bar");

  MT("userSHA",
     "foo [link bar@be6a8cc1c1ecfe9489fb51e4869af15a13fc2cd2] hello");

  MT("userProjectSHA",
     "foo [link bar/hello@be6a8cc1c1ecfe9489fb51e4869af15a13fc2cd2] world");

  MT("num",
     "foo [link #1] bar");

  MT("badNum",
     "foo #1bar hello");

  MT("userNum",
     "foo [link bar#1] hello");

  MT("userProjectNum",
     "foo [link bar/hello#1] world");

  MT("vanillaLink",
     "foo [link http://www.example.com/] bar");

  MT("vanillaLinkPunctuation",
     "foo [link http://www.example.com/]. bar");

  MT("vanillaLinkExtension",
     "foo [link http://www.example.com/index.html] bar");

  MT("notALink",
     "[comment ```css]",
     "[tag foo] {[property color][operator :][keyword black];}",
     "[comment ```][link http://www.example.com/]");

  MT("notALink",
     "[comment ``foo `bar` http://www.example.com/``] hello");

  MT("notALink",
     "[comment `foo]",
     "[link http://www.example.com/]",
     "[comment `foo]",
     "",
     "[link http://www.example.com/]");
})();
