/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.parse;

import com.gmail.socraticphoenix.collect.coupling.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserData {
    private Map<Character, Integer> leftBrackets;
    private Map<Character, Integer> rightBrackets;
    private List<Pair<Character, Character>> brackets;

    private Map<Character, String> escapes;
    private char escapeChar;
    private boolean escaped;
    private boolean prevEscaped;

    private char unicodeEscapeChar;
    private boolean unicodeEscaped;
    private char ua;
    private char ub;
    private char uc;
    private char ud;

    private Map<Character, Boolean> quotes;

    public ParserData() {
        this.leftBrackets = new HashMap<>();
        this.rightBrackets = new HashMap<>();
        this.brackets = new ArrayList<>();
        this.escapes = new HashMap<>();
        this.escapeChar = '\0';
        this.escaped = false;
        this.quotes = new HashMap<>();
        this.unicodeEscapeChar = 'u';
        this.ua = '\0';
        this.ub = '\0';
        this.uc = '\0';
        this.ud = '\0';
    }

    public ParserData reset() {
        ParserData data = new ParserData();
        data.unicodeEscapeChar = this.unicodeEscapeChar;
        this.brackets.forEach(p -> data.brackets(p.getA(), p.getB()));
        data.escapeChar(this.escapeChar);
        this.escapes.entrySet().forEach(e -> data.escape(e.getKey(), e.getValue()));
        this.quotes.keySet().forEach(data::quote);
        return data;
    }

    public Map<Character, Integer> getLeftBrackets() {
        return this.leftBrackets;
    }

    public Map<Character, Integer> getRightBrackets() {
        return this.rightBrackets;
    }

    public List<Pair<Character, Character>> getBrackets() {
        return this.brackets;
    }

    public Map<Character, String> getEscapes() {
        return this.escapes;
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public char getUnicodeEscapeChar() {
        return this.unicodeEscapeChar;
    }

    public boolean isEscaped() {
        return this.escaped;
    }

    public boolean isPrevEscaped() {
        return this.prevEscaped;
    }

    public Map<Character, Boolean> getQuotes() {
        return this.quotes;
    }

    public ParserData brackets(char left, char right) {
        this.leftBrackets.put(left, 0);
        this.rightBrackets.put(right, 0);
        this.brackets.add(Pair.of(left, right));
        return this;
    }

    public ParserData unicodeEscapeChar(char escapeChar) {
        this.unicodeEscapeChar = escapeChar;
        return this;
    }

    public ParserData escapeChar(char escapeChar) {
        this.escapeChar = escapeChar;
        return this;
    }

    public ParserData escape(char syntaxChar) {
        return this.escape(syntaxChar, String.valueOf(syntaxChar));
    }

    public ParserData escape(char code, String literal) {
        this.escapes.put(code, literal);
        return this;
    }

    public ParserData quote(char quote) {
        this.quotes.put(quote, false);
        return this;
    }

    public boolean isInQuotes() {
        return this.quotes.values().stream().filter(b -> b).findFirst().isPresent();
    }

    public boolean isInBrackets() {
        for (Pair<Character, Character> pair : this.brackets) {
            if (this.leftBrackets.get(pair.getA()) - this.rightBrackets.get(pair.getB()) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isSignificant(char c) {
        return this.quotes.containsKey(c) || this.leftBrackets.containsKey(c) || this.rightBrackets.containsKey(c) || this.escapeChar == c || this.escaped;
    }

    public boolean shouldConsider() {
        return !this.isInQuotes() && !this.isInBrackets() && !this.escaped && !this.prevEscaped;
    }

    public String subTrailing() {
        return Strings.cutLast(this.trailing());
    }

    public String trailing() {
        StringBuilder builder = new StringBuilder();
        if(this.unicodeEscaped) {
            builder.append("\\u");
            if(this.ua != '\0') {
                builder.append(this.ua);
            }
            if(this.ub != '\0') {
                builder.append(this.ub);
            }
            if(this.uc != '\0') {
                builder.append(this.uc);
            }
            if(this.ub != '\0') {
                builder.append(this.ub);
            }
        } else if (this.escaped) {
            builder.append("\\");
        }

        return builder.toString();
    }

    public String consider(char c) {
        if (this.unicodeEscaped) {
            if(this.ua == '\0') {
                this.ua = c;
            } else if(this.ub == '\0') {
                this.ub = c;
            } else if(this.uc == '\0') {
                this.uc = c;
            } else if(this.ud == '\0') {
                this.ud = c;
                String val = new String(new char[]{this.ua, this.ub, this.uc, this.ud});
                this.ua = '\0';
                this.ub = '\0';
                this.uc = '\0';
                this.ud = '\0';
                this.unicodeEscaped = false;

                try {
                    return String.valueOf((char) Integer.parseInt(val, 16));
                } catch (NumberFormatException e) {
                    return "\\u" + val;
                }
            }

            return "";
        } else if (this.escaped) {
            this.escaped = false;
            this.prevEscaped = true;
            if (c == this.unicodeEscapeChar) {
                this.unicodeEscaped = true;
                return "";
            } else {
                String s = this.escapes.containsKey(c) ? this.escapes.get(c) : this.escapeChar + "" + c;
                return s;
            }
        } else {
            this.prevEscaped = false;
            if (c == this.escapeChar) {
                this.escaped = true;
                return "";
            } else if (!this.isInQuotes()) {
                if (this.leftBrackets.containsKey(c)) {
                    this.leftBrackets.put(c, this.leftBrackets.get(c) + 1);
                } else if (this.rightBrackets.containsKey(c)) {
                    this.rightBrackets.put(c, this.rightBrackets.get(c) + 1);
                }
            } else if (this.quotes.containsKey(c)) {
                this.quotes.put(c, !this.quotes.get(c));
            }
            return String.valueOf(c);
        }
    }

}
