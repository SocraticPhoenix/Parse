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


import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.parse.parser.PatternContext;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternRestrictions;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class provides powerful methods to parse a string, both at a basic level and with specifications provided by a
 * {@link ParserData} object. Furthermore, this class is capable of real-time transformation of escape codes to their
 * representative characters. It should be noted that the "current character" of the stream is the character which will
 * be processed next, and returned by the {@link CharacterStream#next()} method. The stream's index will always point to
 * the "current character"
 */
public class CharacterStream {
    private char[] stream;
    private int index;
    private String content;
    private PatternContext context;

    /**
     * Creates a new CharacterStream which streams the given Objects, converted to a string with {@link
     * Strings#join(Object...)}
     *
     * @param toStream The objects to stream
     */
    public CharacterStream(Object... toStream) {
        this(Strings.join(toStream).toCharArray());
    }

    /**
     * Creates a new CharacterStream which streams the given characters. The stream will call copy the array to prevent
     * the local copy from being modifiable
     *
     * @param toStream The characters to stream
     */
    public CharacterStream(char[] toStream) {
        this.stream = toStream.clone();
        this.index = 0;
        this.content = new String(this.stream);
        this.context = new PatternContext();
    }

    /**
     * Streams the entire string, returning the resulting string. This can be used to fully apply any transformation
     * specified by the given {@link ParserData}, such as escape codes
     *
     * @param s    The string to stream
     * @param data The transformations to apply
     *
     * @return The transformed string
     */
    public static String complete(String s, ParserData data) {
        return new CharacterStream(s).remaining(data);
    }

    /**
     * Jumps the stream to the specified index. If the index is out of bounds, this method will fail silently, leaving
     * the index at its current position
     *
     * @param i The new index
     */
    public void jumpTo(int i) {
        if (i >= 0 && i < this.stream.length) {
            this.index = i;
        }
    }

    /**
     * Returns the next {@code i} characters, while not counting the given {@code ignore} characters. Characters given
     * in {@code ignore} are still returned in the resulting string, but not counted towards {@code i}. For example, if
     * the next 5 characters in the stream were {@code ababa}, and this method was given the arguments {@code 3 and
     * ['b']}, the return would be {@code ababa}, because that contains three characters not ignored by the count. If
     * this method were to extend out of bounds, it fails silently and returns the result as it has been calculated so
     * far
     *
     * @param i      The number of not-ignored characters to stream
     * @param ignore The characters to ignore while counting to {@code i}
     *
     * @return A string containing at least {@code i} characters, {@code i} of which are not in {@code ignore}
     */
    public String next(int i, char... ignore) {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < i && this.hasNext(); j++) {
            char c = this.next().get();
            builder.append(c);
            if (Items.contains(c, ignore)) {
                j--;
            }
        }
        return builder.toString();
    }

    /**
     * Returns the next {@code i} characters. If this method were to extend out of bounds, it fails silently and returns
     * the result as it has been calculated so far
     *
     * @param i The number of characters to stream
     *
     * @return A string containing {@code i} characters
     */
    public String next(int i) {
        StringBuilder builder = new StringBuilder();
        for (int j = 0; j < i && this.hasNext(); j++) {
            builder.append(this.next().get());
        }
        return builder.toString();
    }

    /**
     * @return True if there are characters before the index, false otherwise
     */
    public boolean hasPrevious() {
        return this.index > 0;
    }

    /**
     * @return A present optional containing the previous character if it exists, or an empty optional otherwise
     */
    public Optional<Character> peekPrevious() {
        if (this.hasPrevious()) {
            return Optional.of(this.stream[this.index - 1]);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the previous character while shifting the index backwards. If there are no previous characters, the index
     * is left unchanged and an empty optional is returned
     *
     * @return A present optional containing the previous character if it exists, or an empty optional otherwise
     */
    public Optional<Character> previous() {
        if (this.hasPrevious()) {
            this.index--;
            return Optional.of(this.stream[this.index]);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Shifts the index backwards while the previous character is within {@code array}. This is functionally equivalent
     * to:
     * <pre>
     *     backWhile(z -&#62; Items.contains(z, array))
     * </pre>
     *
     * @param array The characters to match
     *
     * @see CharacterStream#backWhile(Predicate)
     */
    public void backWhile(char... array) {
        this.backWhile(z -> Items.contains(z, array));
    }

    /**
     * Shifts the index backwards so long as the predicate returns true for the previous character. Specifically, the
     * current character after the execution of this method will be the leftmost end of the sequence of characters in
     * the range [0, index] which match {@code condition}. For example, if the content of the stream was {@code
     * baabbbt}, and the index was at {@code t}, and the condition matched only the {@code 'b'} character, this method
     * would shift the index to 3, which is the {@code b} directly after {@code aa}
     *
     * @param condition The condition to use while shifting backwards
     */
    public void backWhile(Predicate<Character> condition) {
        boolean found = false;
        while (this.hasPrevious() && condition.test(this.previous().get())) {
            found = true;
        }
        if (!found) {
            this.next();
        }
    }

    /**
     * Shifts the index forwards so long as the predicate returns false for the current character. Specifically, the
     * current character after the execution of this method will be the next occurrence of a character which the
     * predicate matches. Furthermore, this method returns a string containing the characters from the current index,
     * and up to (but not including) the first occurrence of a character matched by the predicate. If this method were
     * to extend out of bounds, it will fail silently and return the result as it has been calculated so far
     *
     * @param condition The condition to use while shifting forwards
     *
     * @return A string containing characters from the current index to the first match of {@code condition}
     */
    public String nextUntil(Predicate<Character> condition) {
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char z = this.next().get();
            if (condition.test(z)) {
                this.back();
                break;
            } else {
                builder.append(z);
            }
        }
        return builder.toString();
    }

    /**
     * Shifts the index forwards so long as the predicate returns false for the current character, and {@link
     * ParserData#shouldConsider()} returns true. Specifically, the current character after the execution of this method
     * will be the next occurrence of a character which the {@code data} defines as considerable, and the predicate
     * matches. Furthermore, this method returns a string containing the character from the current index, and up to
     * (but not including) the first occurrence of character for which the predicate matches and {@code data} considers.
     * If this method were to extend out of bounds, it will fail silently and return the result as it has been
     * calculated so far. This method does not call {@link ParserData#reset()}
     *
     * @param condition The condition to use while shifting forwards
     * @param data      The data to use to determine whether or not a character should be considered
     *
     * @return A string containing characters from the current index to the first match of {@code condition} which is
     * considered by {@code data}
     *
     * @see CharacterStream#nextUntil(Predicate)
     */
    public String nextUntil(Predicate<Character> condition, ParserData data) {
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char z = this.next().get();
            String s = data.consider(z);
            if (data.shouldConsider() && condition.test(z)) {
                this.back();
                break;
            } else {
                builder.append(s);
            }
        }
        return builder.append(data.subTrailing()).toString();
    }

    /**
     * Shifts the index forwards so long as the restriction does not have a match at the current index, and {@link
     * ParserData#shouldConsider()} returns true. Specifically, the current character after the execution of this method
     * will be the next occurrence of a character which the {@code data} defines as considerable, and the first
     * character of a sequence matched by {@code condition}. Furthermore, this method returns a string containing the
     * character from the current index, and up to (but not including) the first occurrence of character for which the
     * restriction matches and {@code data} considers. If this method were to extend out of bounds, it will fail
     * silently and return the result as it has been calculated so far. This method does not call {@link
     * ParserData#reset()}
     *
     * @param condition The condition to use while shifting forwards
     * @param data      The data to use to determine whether or not a character should be considered
     *
     * @return A string containing characters from the current index to the first match of {@code condition} which is
     * considered by {@code data}
     *
     * @see CharacterStream#nextUntil(Predicate)
     */
    public String nextUntil(PatternRestriction condition, ParserData data) {
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char z = this.next().get();
            String s = data.consider(z);
            if (data.shouldConsider() && condition.match(this.content, this.index, this.context).isSuccesful()) {
                this.back();
                break;
            } else {
                builder.append(s);
            }
        }
        return builder.append(data.subTrailing()).toString();
    }

    /**
     * Shifts the index forwards so long as the {@link ParserData#shouldConsider()} returns false. This method is useful
     * for fully resolving brackets or quoted strings. Furthermore, this method will return a string containing the
     * characters from the current index, and up to (but not including) the first occurrence of a character the {@code
     * data} considers. If this method were to extend out of bounds, it will fail silently and return the result as it
     * has been calculated so far. This method does not call {@link ParserData#reset()}
     *
     * @param data The data to use while shifting forwards
     *
     * @return A string containing characters from the current index to the first character considered by {@code data}
     */
    public String nextUntil(ParserData data) {
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char z = this.next().get();
            String s = data.consider(z);
            builder.append(s);
            if (data.shouldConsider()) {
                break;
            }
        }
        return builder.append(data.subTrailing()).toString();
    }


    /**
     * Shifts the index forwards while the current character is not in {@code array}. This is functionally equivalent
     * to:
     * <pre>
     *     nextUntil(z -&#62; Items.contains(z, array))
     * </pre>
     *
     * @param array The characters to match
     *
     * @return A string of characters from the current index to the first occurrence of a character in {@code array}
     *
     * @see CharacterStream#nextUntil(Predicate)
     */
    public String nextUntil(char... array) {
        return this.nextUntil(z -> Items.contains(z, array));
    }

    /**
     * Accumulates a string result so long as that result is matched by {@code condition}. More specifically, this
     * method shifts the index forwards as long as the next character, concatenated to the result, is matched by {@code
     * condition}.
     *
     * @param condition The condition to match
     *
     * @return The longest string of characters for which {@code condition} returns true, or an empty string if {@code
     * condition} does not match any characters
     */
    public String nextWhile(Predicate<String> condition) {
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char c = this.next().get();
            if (condition.test(builder.toString() + c)) {
                builder.append(c);
            } else {
                this.back();
                break;
            }
        }
        return builder.toString();
    }

    /**
     * Accumulates a string result so long as that result is matched by {@code condition}. More specifically, this
     * method shifts the index forwards as long as the next character, concatenated to the result, is matched by {@code
     * condition}.
     *
     * @param condition The condition to match
     *
     * @return The longest string of characters for which {@code condition} returns true, or an empty string if {@code
     * condition} does not match any characters
     */
    public String nextWhile(PatternRestriction condition) {
        condition = PatternRestrictions.completed(condition);
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char c = this.next().get();
            if (condition.match(builder.toString() + c).isSuccesful()) {
                builder.append(c);
            } else {
                this.back();
                break;
            }
        }
        return builder.toString();
    }

    /**
     * Accumulates a string result so long as that result is matched by {@code condition}. More specifically, this
     * method shifts the index forwards as long as the next character, concatenated to the result, is matched by {@code
     * condition}. {@code condition} is only applied when {@link ParserData#shouldConsider()} returns true. This method
     * does not call {@link ParserData#reset()}
     *
     * @param condition The condition to match
     * @param data      The data to use while shifting forwards
     *
     * @return The longest string of characters for which {@code condition} returns true, or an empty string if {@code
     * condition} does not match any characters
     */
    public String nextWhile(Predicate<String> condition, ParserData data) {
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char c = this.next().get();
            String s = data.consider(c);
            if (data.shouldConsider() && condition.test(builder.toString() + c)) {
                this.back();
                break;
            } else {
                builder.append(s);
            }
        }
        return builder.append(data.subTrailing()).toString();
    }

    /**
     * Accumulates a string result so long as that result is matched by {@code condition}. More specifically, this
     * method shifts the index forwards as long as the next character, concatenated to the result, is matched by {@code
     * condition}. {@code condition} is only applied when {@link ParserData#shouldConsider()} returns true. This method
     * does not call {@link ParserData#reset()}
     *
     * @param condition The condition to match
     * @param data      The data to use while shifting forwards
     *
     * @return The longest string of characters for which {@code condition} returns true, or an empty string if {@code
     * condition} does not match any characters
     */
    public String nextWhile(PatternRestriction condition, ParserData data) {
        condition = PatternRestrictions.completed(condition);
        StringBuilder builder = new StringBuilder();
        while (this.hasNext()) {
            char c = this.next().get();
            String s = data.consider(c);
            if (data.shouldConsider() && condition.match(builder.toString() + c).isSuccesful()) {
                this.back();
                break;
            } else {
                builder.append(s);
            }
        }
        return builder.append(data.subTrailing()).toString();
    }

    /**
     * Moves forward one character if the current character is in {@code array}. This is functionally equivalent to:
     * <pre>
     *     consume(z -&#62; Items.contains(z, array))
     * </pre>
     *
     * @param array The array to match while moving forwards
     *
     * @see CharacterStream#consume(Predicate)
     */
    public void consume(char... array) {
        this.consume(z -> Items.contains(z, array));
    }

    /**
     * Moves forward one character if the current character matches {@code condition}
     *
     * @param condition The condition to use while moving forwards
     */
    public void consume(Predicate<Character> condition) {
        if (this.hasNext() && !condition.test(this.next().get())) {
            this.back();
        }
    }

    /**
     * Moves forward while the current character is in {@code array}. This is functionally equivalent to:
     * <pre>
     *     consumeAll(z -&#62; Items.contains(z, array))
     * </pre>
     *
     * @param array The array to match while moving forwards
     */
    public void consumeAll(char... array) {
        this.consumeAll(z -> Items.contains(z, array));
    }

    /**
     * Moves forward while the current character matches {@code condition}
     *
     * @param condition The condition to use while moving forwards
     */
    public void consumeAll(Predicate<Character> condition) {
        while (this.isNext(condition)) {
            this.next();
        }
    }

    /**
     * @return The complete sequence of characters this stream is streaming
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Moves back {@code i} characters. If this method were to extend out of bounds, it fails silently and leaves the
     * index at {@code 0}
     *
     * @param i The amount of characters to move back
     */
    public void back(int i) {
        for (int j = 0; j < i; j++) {
            this.back();
        }
    }

    /**
     * Moves back 1 character. If this method were to extend out of bounds, it fails silently and leaves the index at
     * its current value
     */
    public void back() {
        this.index = index > 0 ? index - 1 : index;
    }

    /**
     * Returns true if the current character is in {@code array}. This is functional equivalent to:
     * <pre>
     *     isNext(z -&#62; Items.contains(z, array))
     * </pre>
     *
     * @param array The array to match
     *
     * @return True if the current character is in {@code array}, false otherwise
     *
     * @see CharacterStream#isNext(Predicate)
     */
    public boolean isNext(char... array) {
        return this.isNext(z -> Items.contains(z, array));
    }

    /**
     * Returns true if the current character is matched by {@code condition}
     *
     * @param condition The condition to match with
     *
     * @return True if the current character is matched by {@code condition}, false otherwise
     */
    public boolean isNext(Predicate<Character> condition) {
        if (this.hasNext()) {
            char z = this.peek().get();
            return condition.test(z);
        }
        return false;
    }

    /**
     * @return True if the stream has a current character, false otherwise
     */
    public boolean hasNext() {
        return this.index < this.stream.length;
    }

    /**
     * @return The current character if it exists, an empty optional otherwise
     */
    public Optional<Character> peek() {
        Optional<Character> next = this.next();
        if (next.isPresent()) {
            this.back();
        }
        return next;
    }

    /**
     * Returns the current character if it exists, and moves the index forwards 1
     *
     * @return The current character if it exists, an empty optional otherwise
     */
    public Optional<Character> next() {
        if (this.hasNext()) {
            char val = this.stream[this.index];
            this.index++;
            return Optional.of(val);
        } else {
            return Optional.empty();
        }
    }

    /**
     * @return The current index
     */
    public int index() {
        return this.index;
    }

    /**
     * @return The remaining characters in the string
     */
    public String remaining() {
        return this.nextUntil(c -> false);
    }

    /**
     * Processes the remaining characters in the string with the given {@code data}, and returns the result
     *
     * @param data The data to use
     *
     * @return The remaining characters in the string, as transformed by the {@code data}
     */
    public String remaining(ParserData data) {
        return this.nextUntil(c -> false, data);
    }


}
