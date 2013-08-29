/*
 * #%L
 * meta.js - Shift - 2013
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
CodeMirror.modeInfo = [
  {name: 'APL', mime: 'text/apl', mode: 'apl'},
  {name: 'Asterisk', mime: 'text/x-asterisk', mode: 'asterisk'},
  {name: 'C', mime: 'text/x-csrc', mode: 'clike'},
  {name: 'C++', mime: 'text/x-c++src', mode: 'clike'},
  {name: 'Java', mime: 'text/x-java', mode: 'clike'},
  {name: 'C#', mime: 'text/x-csharp', mode: 'clike'},
  {name: 'Scala', mime: 'text/x-scala', mode: 'clike'},
  {name: 'Clojure', mime: 'text/x-clojure', mode: 'clojure'},
  {name: 'CoffeeScript', mime: 'text/x-coffeescript', mode: 'coffeescript'},
  {name: 'Common Lisp', mime: 'text/x-common-lisp', mode: 'commonlisp'},
  {name: 'CSS', mime: 'text/css', mode: 'css'},
  {name: 'D', mime: 'text/x-d', mode: 'd'},
  {name: 'diff', mime: 'text/x-diff', mode: 'diff'},
  {name: 'ECL', mime: 'text/x-ecl', mode: 'ecl'},
  {name: 'Erlang', mime: 'text/x-erlang', mode: 'erlang'},
  {name: 'Gas', mime: 'text/x-gas', mode: 'gas'},
  {name: 'GitHub Flavored Markdown', mode: 'gfm'},
  {name: 'GO', mime: 'text/x-go', mode: 'go'},
  {name: 'Groovy', mime: 'text/x-groovy', mode: 'groovy'},
  {name: 'Haskell', mime: 'text/x-haskell', mode: 'haskell'},
  {name: 'Haxe', mime: 'text/x-haxe', mode: 'haxe'},
  {name: 'ASP.NET', mime: 'application/x-aspx', mode: 'htmlembedded'},
  {name: 'Embedded Javascript', mime: 'application/x-ejs', mode: 'htmlembedded'},
  {name: 'JavaServer Pages', mime: 'application/x-jsp', mode: 'htmlembedded'},
  {name: 'HTML', mime: 'text/html', mode: 'htmlmixed'},
  {name: 'HTTP', mime: 'message/http', mode: 'http'},
  {name: 'JavaScript', mime: 'text/javascript', mode: 'javascript'},
  {name: 'JSON', mime: 'application/json', mode: 'javascript'},
  {name: 'TypeScript', mime: 'application/typescript', mode: 'javascript'},
  {name: 'Jinja2', mime: 'jinja2', mode: 'jinja2'},
  {name: 'LESS', mime: 'text/x-less', mode: 'less'},
  {name: 'LiveScript', mime: 'text/x-livescript', mode: 'livescript'},
  {name: 'Lua', mime: 'text/x-lua', mode: 'lua'},
  {name: 'Markdown (GitHub-flavour)', mime: 'text/x-markdown', mode: 'markdown'},
  {name: 'mIRC', mime: 'text/mirc', mode: 'mirc'},
  {name: 'NTriples', mime: 'text/n-triples', mode: 'ntriples'},
  {name: 'OCaml', mime: 'text/x-ocaml', mode: 'ocaml'},
  {name: 'Pascal', mime: 'text/x-pascal', mode: 'pascal'},
  {name: 'Perl', mime: 'text/x-perl', mode: 'perl'},
  {name: 'PHP', mime: 'text/x-php', mode: 'php'},
  {name: 'PHP(HTML)', mime: 'application/x-httpd-php', mode: 'php'},
  {name: 'Pig', mime: 'text/x-pig', mode: 'pig'},
  {name: 'Plain Text', mime: 'text/plain', mode: 'null'},
  {name: 'Properties files', mime: 'text/x-properties', mode: 'clike'},
  {name: 'Python', mime: 'text/x-python', mode: 'python'},
  {name: 'R', mime: 'text/x-rsrc', mode: 'r'},
  {name: 'reStructuredText', mime: 'text/x-rst', mode: 'rst'},
  {name: 'Ruby', mime: 'text/x-ruby', mode: 'ruby'},
  {name: 'Rust', mime: 'text/x-rustsrc', mode: 'rust'},
  {name: 'Sass', mime: 'text/x-sass', mode: 'sass'},
  {name: 'Scheme', mime: 'text/x-scheme', mode: 'scheme'},
  {name: 'SCSS', mime: 'text/x-scss', mode: 'css'},
  {name: 'Shell', mime: 'text/x-sh', mode: 'shell'},
  {name: 'Sieve', mime: 'application/sieve', mode: 'sieve'},
  {name: 'Smalltalk', mime: 'text/x-stsrc', mode: 'smalltalk'},
  {name: 'Smarty', mime: 'text/x-smarty', mode: 'smarty'},
  {name: 'SPARQL', mime: 'application/x-sparql-query', mode: 'sparql'},
  {name: 'SQL', mime: 'text/x-sql', mode: 'sql'},
  {name: 'MariaDB', mime: 'text/x-mariadb', mode: 'sql'},
  {name: 'sTeX', mime: 'text/x-stex', mode: 'stex'},
  {name: 'LaTeX', mime: 'text/x-latex', mode: 'stex'},
  {name: 'Tcl', mime: 'text/x-tcl', mode: 'tcl'},
  {name: 'TiddlyWiki ', mime: 'text/x-tiddlywiki', mode: 'tiddlywiki'},
  {name: 'Tiki wiki', mime: 'text/tiki', mode: 'tiki'},
  {name: 'VB.NET', mime: 'text/x-vb', mode: 'vb'},
  {name: 'VBScript', mime: 'text/vbscript', mode: 'vbscript'},
  {name: 'Velocity', mime: 'text/velocity', mode: 'velocity'},
  {name: 'Verilog', mime: 'text/x-verilog', mode: 'verilog'},
  {name: 'XML', mime: 'application/xml', mode: 'xml'},
  {name: 'HTML', mime: 'text/html', mode: 'xml'},
  {name: 'XQuery', mime: 'application/xquery', mode: 'xquery'},
  {name: 'YAML', mime: 'text/x-yaml', mode: 'yaml'},
  {name: 'Z80', mime: 'text/x-z80', mode: 'z80'}
];
