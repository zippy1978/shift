/*
 * #%L
 * xml-hint.js - Shift - 2013
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
(function() {

    CodeMirror.xmlHints = [];

    CodeMirror.xmlHint = function(cm) {

        var cursor = cm.getCursor();

        if (cursor.ch > 0) {

            var text = cm.getRange(CodeMirror.Pos(0, 0), cursor);
            var typed = '';
            var simbol = '';
            for(var i = text.length - 1; i >= 0; i--) {
                if(text[i] == ' ' || text[i] == '<') {
                    simbol = text[i];
                    break;
                }
                else {
                    typed = text[i] + typed;
                }
            }

            text = text.slice(0, text.length - typed.length);

            var path = getActiveElement(text) + simbol;
            var hints = CodeMirror.xmlHints[path];

            if(typeof hints === 'undefined')
                hints = [''];
            else {
                hints = hints.slice(0);
                for (var i = hints.length - 1; i >= 0; i--) {
                    if(hints[i].indexOf(typed) != 0)
                        hints.splice(i, 1);
                }
            }

            return {
                list: hints,
                from: CodeMirror.Pos(cursor.line, cursor.ch - typed.length),
                to: cursor
            };
        }
    };

    var getActiveElement = function(text) {

        var element = '';

        if(text.length >= 0) {

            var regex = new RegExp('<([^!?][^\\s/>]*)[\\s\\S]*?>', 'g');

            var matches = [];
            var match;
            while ((match = regex.exec(text)) != null) {
                matches.push({
                    tag: match[1],
                    selfclose: (match[0].slice(match[0].length - 2) === '/>')
                });
            }

            for (var i = matches.length - 1, skip = 0; i >= 0; i--) {

                var item = matches[i];

                if (item.tag[0] == '/')
                {
                    skip++;
                }
                else if (item.selfclose == false)
                {
                    if (skip > 0)
                    {
                        skip--;
                    }
                    else
                    {
                        element = '<' + item.tag + '>' + element;
                    }
                }
            }

            element += getOpenTag(text);
        }

        return element;
    };

    var getOpenTag = function(text) {

        var open = text.lastIndexOf('<');
        var close = text.lastIndexOf('>');

        if (close < open)
        {
            text = text.slice(open);

            if(text != '<') {

                var space = text.indexOf(' ');
                if(space < 0)
                    space = text.indexOf('\t');
                if(space < 0)
                    space = text.indexOf('\n');

                if (space < 0)
                    space = text.length;

                return text.slice(0, space);
            }
        }

        return '';
    };

})();
