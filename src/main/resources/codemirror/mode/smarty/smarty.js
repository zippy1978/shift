/*
 * #%L
 * smarty.js - Shift - 2013
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
CodeMirror.defineMode("smarty", function(config) {
  var keyFuncs = ["debug", "extends", "function", "include", "literal"];
  var last;
  var regs = {
    operatorChars: /[+\-*&%=<>!?]/,
    validIdentifier: /[a-zA-Z0-9\_]/,
    stringChar: /[\'\"]/
  };
  var leftDelim = (typeof config.mode.leftDelimiter != 'undefined') ? config.mode.leftDelimiter : "{";
  var rightDelim = (typeof config.mode.rightDelimiter != 'undefined') ? config.mode.rightDelimiter : "}";
  function ret(style, lst) { last = lst; return style; }


  function tokenizer(stream, state) {
    function chain(parser) {
      state.tokenize = parser;
      return parser(stream, state);
    }

    if (stream.match(leftDelim, true)) {
      if (stream.eat("*")) {
        return chain(inBlock("comment", "*" + rightDelim));
      }
      else {
        state.tokenize = inSmarty;
        return "tag";
      }
    }
    else {
      // I'd like to do an eatWhile() here, but I can't get it to eat only up to the rightDelim string/char
      stream.next();
      return null;
    }
  }

  function inSmarty(stream, state) {
    if (stream.match(rightDelim, true)) {
      state.tokenize = tokenizer;
      return ret("tag", null);
    }

    var ch = stream.next();
    if (ch == "$") {
      stream.eatWhile(regs.validIdentifier);
      return ret("variable-2", "variable");
    }
    else if (ch == ".") {
      return ret("operator", "property");
    }
    else if (regs.stringChar.test(ch)) {
      state.tokenize = inAttribute(ch);
      return ret("string", "string");
    }
    else if (regs.operatorChars.test(ch)) {
      stream.eatWhile(regs.operatorChars);
      return ret("operator", "operator");
    }
    else if (ch == "[" || ch == "]") {
      return ret("bracket", "bracket");
    }
    else if (/\d/.test(ch)) {
      stream.eatWhile(/\d/);
      return ret("number", "number");
    }
    else {
      if (state.last == "variable") {
        if (ch == "@") {
          stream.eatWhile(regs.validIdentifier);
          return ret("property", "property");
        }
        else if (ch == "|") {
          stream.eatWhile(regs.validIdentifier);
          return ret("qualifier", "modifier");
        }
      }
      else if (state.last == "whitespace") {
        stream.eatWhile(regs.validIdentifier);
        return ret("attribute", "modifier");
      }
      else if (state.last == "property") {
        stream.eatWhile(regs.validIdentifier);
        return ret("property", null);
      }
      else if (/\s/.test(ch)) {
        last = "whitespace";
        return null;
      }

      var str = "";
      if (ch != "/") {
        str += ch;
      }
      var c = "";
      while ((c = stream.eat(regs.validIdentifier))) {
        str += c;
      }
      var i, j;
      for (i=0, j=keyFuncs.length; i<j; i++) {
        if (keyFuncs[i] == str) {
          return ret("keyword", "keyword");
        }
      }
      if (/\s/.test(ch)) {
        return null;
      }
      return ret("tag", "tag");
    }
  }

  function inAttribute(quote) {
    return function(stream, state) {
      while (!stream.eol()) {
        if (stream.next() == quote) {
          state.tokenize = inSmarty;
          break;
        }
      }
      return "string";
    };
  }

  function inBlock(style, terminator) {
    return function(stream, state) {
      while (!stream.eol()) {
        if (stream.match(terminator)) {
          state.tokenize = tokenizer;
          break;
        }
        stream.next();
      }
      return style;
    };
  }

  return {
    startState: function() {
      return { tokenize: tokenizer, mode: "smarty", last: null };
    },
    token: function(stream, state) {
      var style = state.tokenize(stream, state);
      state.last = last;
      return style;
    },
    electricChars: ""
  };
});

CodeMirror.defineMIME("text/x-smarty", "smarty");
