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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * This class provides convenience methods for manipulating strings
 */
public class Strings {
    private static final ParserData javaFormat;

    static {
        javaFormat = new ParserData();
        Strings.javaFormat.escapeChar('\\')
                .unicodeEscapeChar('u')
                .escape('0', "\0")
                .escape('b', "\b")
                .escape('t', "\t")
                .escape('n', "\n")
                .escape('f', "\f")
                .escape('r', "\r")
                .escape('"')
                .escape('\'')
                .escape('\\');
    }

    public static ParserData javaEscapeFormat() {
        return Strings.javaFormat.reset();
    }

    public static boolean isBalanced(String s, ParserData data) {
        List<Pair<Character, Character>> brackets = data.getBrackets();
        Map<Character, Character> leftToRight = new HashMap<>();
        Map<Character, Character> rightToLeft = new HashMap<>();
        brackets.forEach(p -> {
            leftToRight.put(p.getA(), p.getB());
            rightToLeft.put(p.getB(), p.getA());
        });

        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (leftToRight.containsKey(c)) {
                stack.push(c);
            } else if (rightToLeft.containsKey(c)) {
                char target = rightToLeft.get(c);
                if (stack.empty() || stack.pop() != target) {
                    return false;
                }
            }
        }
        return stack.empty();
    }

    /**
     * Creates an indent of level {@code times}
     *
     * @param times The indent level
     *
     * @return Four spaces, repeated {@code times} times
     */
    public static String indent(int times) {
        return Strings.repeat("    ", times);
    }

    /**
     * Repeats the given argument {@code times} times. If {@code s} is null, an empty string is returned
     *
     * @param s     The string to repeat
     * @param times The amount of times to repeat it
     *
     * @return The repeated string
     */
    public static String repeat(String s, int times) {
        if (s == null) {
            return "";
        }
        String result = "";
        for (int i = 0; i < times; i++) {
            result += s;
        }
        return result;
    }

    /**
     * Escapes literal characters in a string to their appropriate counterparts, following java conventions for escape
     * codes. More notably, the values are replaced like so:
     * <table border="1" summary="">
     * <tr>
     * <td>Literal Character: </td>
     * <td>Replacement: </td>
     * </tr>
     * <tr>
     * <td>null (ASCII 0, \0)</td>
     * <td>\0</td>
     * </tr>
     * <tr>
     * <td>backspace (ASCII 8, \b)</td>
     * <td>\b</td>
     * </tr>
     * <tr>
     * <td>tab (ASCII 9, \t)</td>
     * <td>\t</td>
     * </tr>
     * <tr>
     * <td>newline (ASCII 10, \n)</td>
     * <td>\n</td>
     * </tr>
     * <tr>
     * <td>formfeed (ASCII 12, \f)</td>
     * <td>\f</td>
     * </tr>
     * <tr>
     * <td>carriage return (ASCII 13, \r)</td>
     * <td>\r</td>
     * </tr>
     * <tr>
     * <td>double quote (ASCII 34, ")</td>
     * <td>\"</td>
     * </tr>
     * <tr>
     * <td>single quote (ASCII 39, ')</td>
     * <td>\'</td>
     * </tr>
     * <tr>
     * <td>backslash (ASCII 92, \)</td>
     * <td>\\</td>
     * </tr>
     * </table>
     *
     * @param string The string to escape. If null, an empty string is returned
     *
     * @return The escaped string
     */
    public static String escape(String string) {
        return Strings.escape(string, Strings.javaFormat);
    }

    public static String escape(String string, ParserData data) {
        if (string == null) {
            return "";
        }

        Map<Character, String> escapes = data.getEscapes();
        char esc = data.getEscapeChar();
        if(escapes.entrySet().stream().filter(c -> c.getKey() == esc && c.getValue().equals(String.valueOf(esc))).findFirst().isPresent()) {
            string = string.replace(String.valueOf(esc), esc + "" + esc);
        }
        for (Map.Entry<Character, String> entry : escapes.entrySet()) {
            if (entry.getKey() != esc || !entry.getValue().equals(String.valueOf(esc))) {
                String re = esc + "" + entry.getKey();
                string = string.replace(entry.getValue(), re);
            }
        }

        return string;
    }

    /**
     * De-escapes escape codes in a string to their appropriate counterparts, following java conventions for escape
     * codes. More notably, the values are replaced like so:
     * <table border="1" summary="">
     * <tr>
     * <td>Escape Code: </td>
     * <td>Replacement: </td>
     * </tr>
     * <tr>
     * <td>\0</td>
     * <td>null (ASCII 0, \0)</td>
     * </tr>
     * <tr>
     * <td>\b</td>
     * <td>backspace (ASCII 8, \b)</td>
     * </tr>
     * <tr>
     * <td>\t</td>
     * <td>tab (ASCII 9, \t)</td>
     * </tr>
     * <tr>
     * <td>\n</td>
     * <td>newline (ASCII 10, \n)</td>
     * </tr>
     * <tr>
     * <td>\f</td>
     * <td>formfeed (ASCII 12, \f)</td>
     * </tr>
     * <tr>
     * <td>\r</td>
     * <td>carriage return (ASCII 13, \r)</td>
     * </tr>
     * <tr>
     * <td>\"</td>
     * <td>double quote (ASCII 34, ")</td>
     * </tr>
     * <tr>
     * <td>\'</td>
     * <td>single quote (ASCII 39, ')</td>
     * </tr>
     * <tr>
     * <td>\\</td>
     * <td>backslash (ASCII 92, \)</td>
     * </tr>
     * <tr>
     * <td>&#92;u####</td>
     * <td>unicode character</td>
     * </tr>
     * </table>
     * No error is thrown on an invalid escape code, and if one is present, the escape character will be interpreted
     * literally
     *
     * @param string The string to de-escape. If null, an empty string is returned
     *
     * @return The de-escaped string
     */
    public static String deEscape(String string) {
        return Strings.deEscape(string, Strings.javaFormat);
    }

    public static String deEscape(String string, ParserData data) {
        if(string == null) {
            return "";
        }

        return CharacterStream.complete(string, data.reset());
    }

    /**
     * Removes {@code count} characters from the end of the string. If {@code string.length() < count}, an empty string
     * is returned
     *
     * @param string The string to cut. If null, an empty string is returned
     * @param count  The number of characters to cut
     *
     * @return The cut string
     *
     * @see Strings#cutLast(String)
     */
    public static String cutLast(String string, int count) {
        if (string == null) {
            return "";
        }

        if (string.length() < count) {
            return "";
        } else {
            for (int i = 0; i < count; i++) {
                string = Strings.cutLast(string);
            }
            return string;
        }
    }

    /**
     * Removes {@code count} characters from the start of the string. If {@code string.length() < count}, an empty
     * string is returned
     *
     * @param string The string to cut. If null, an empty string is returned
     * @param count  The number of characters to cut
     *
     * @return The cut string
     *
     * @see Strings#cutFirst(String)
     */
    public static String cutFirst(String string, int count) {
        if (string == null) {
            return "";
        }

        if (string.length() < count) {
            return "";
        } else {
            for (int i = 0; i < count; i++) {
                string = Strings.cutFirst(string);
            }
            return string;
        }
    }

    /**
     * Removes the last character of a string and returns it. If the string is of length 0, an empty string is returned
     *
     * @param string The string to cut. If null, an empty string is returned
     *
     * @return The cut string
     */
    public static String cutLast(String string) {
        if (string == null) {
            return "";
        }

        if (string.length() == 0) {
            return string;
        } else {
            return string.substring(0, string.length() - 1);
        }
    }

    /**
     * Removes the first character of a string and returns it. If the string is of length 0, an empty string is returned
     *
     * @param string The string to cut. If null, an empty string is returned
     *
     * @return The cut string
     */
    public static String cutFirst(String string) {
        if (string == null) {
            return "";
        }

        if (string.length() == 0) {
            return string;
        } else {
            return string.substring(1, string.length());
        }
    }

    public static String join(Object... message) {
        StringBuilder builder = new StringBuilder();
        for (Object object : message) {
            builder.append(object);
        }
        return builder.toString();
    }

    public static String glue(String glue, Object... message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length; i++) {
            builder.append(message[i]);
            if (i < message.length - 1) {
                builder.append(glue);
            }
        }
        return builder.toString();
    }
}
