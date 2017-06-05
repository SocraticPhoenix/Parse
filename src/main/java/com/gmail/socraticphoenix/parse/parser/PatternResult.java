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
package com.gmail.socraticphoenix.parse.parser;

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.parse.Strings;

import java.util.List;

public class PatternResult {
    private int end;
    private Type type;
    private String message;
    private List<PatternResult> subResults;

    private boolean debug;

    public PatternResult(int end, Type type, String message, List<PatternResult> subResults, boolean debug) {
        this.end = end;
        this.type = type;
        this.message = message;
        this.subResults = subResults;
        this.debug = debug;
    }

    public static PatternResult succesful(int end) {
        return new PatternResult(end, Type.SUCCESS, "Matched correctly", Items.buildList(), false);
    }

    public static PatternResult parseError(String message, int end) {
        return new PatternResult(end, Type.PARSE_ERROR, message, Items.buildList(), false);
    }

    public static PatternResult composed(String error, int end, List<PatternResult> results) {
        Type type = results.stream().anyMatch(p -> !p.isSuccesful() && !p.isDebug()) ? Type.ERROR : Type.SUCCESS;
        return new PatternResult(end, type, type.isSuccesful() ? "Matched correctly" : error, results, false);
    }

    public PatternResult asDebug() {
        return new PatternResult(this.end, this.type, this.message, this.subResults, true);
    }

    public String buildMessage() {
        return this.buildMessage(0, false);
    }

    public String buildMessage(int indent, boolean debug) {
        StringBuilder builder = new StringBuilder();
        String ls = System.lineSeparator();
        String i1 = Strings.indent(indent);
        builder.append(i1).append("[").append(this.end).append("] ").append(this.type).append(": ").append(this.type.isSuccesful() ? "Succesful" : "Unsuccessful").append(": ").append(this.message).append(this.subResults.stream().filter(r -> (!r.isDebug() || debug)).findFirst().isPresent() ? ". Caused by:" : "").append(ls);
        this.subResults.stream().filter(r -> (!r.isDebug() || debug)).forEach(r -> {
            builder.append(r.buildMessage(indent + 1, debug));
        });
        return builder.toString();
    }

    public int getEnd() {
        return this.end;
    }

    public Type getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public List<PatternResult> getSubResults() {
        return this.subResults;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public boolean isSuccesful() {
        return this.getType().isSuccesful();
    }

    public enum Type {
        ERROR(false),
        PARSE_ERROR(false),
        SYNTAX_ERROR(false),
        UNKNOWN_ERROR(false),
        WARNING(true),
        SUCCESS(true);

        private final boolean succesful;

        Type(boolean succesful) {
            this.succesful = succesful;
        }

        public boolean isSuccesful() {
            return this.succesful;
        }
    }
}
