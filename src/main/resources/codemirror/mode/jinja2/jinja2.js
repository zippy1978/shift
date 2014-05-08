/*
 * #%L
 * jinja2.js - Shift - 2013
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
CodeMirror.defineMode("jinja2", function() {
    var keywords = ["block", "endblock", "for", "endfor", "in", "true", "false",
                    "loop", "none", "self", "super", "if", "as", "not", "and",
                    "else", "import", "with", "without", "context"];
    keywords = new RegExp("^((" + keywords.join(")|(") + "))\\b");

    function tokenBase (stream, state) {
        var ch = stream.next();
        if (ch == "{") {
            if (ch = stream.eat(/\{|%|#/)) {
                stream.eat("-");
                state.tokenize = inTag(ch);
                return "tag";
            }
        }
    }
    function inTag (close) {
        if (close == "{") {
            close = "}";
        }
        return function (stream, state) {
            var ch = stream.next();
            if ((ch == close || (ch == "-" && stream.eat(close)))
                && stream.eat("}")) {
                state.tokenize = tokenBase;
                return "tag";
            }
            if (stream.match(keywords)) {
                return "keyword";
            }
            return close == "#" ? "comment" : "string";
        };
    }
    return {
        startState: function () {
            return {tokenize: tokenBase};
        },
        token: function (stream, state) {
            return state.tokenize(stream, state);
        }
    };
});
