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

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ParseResult {
    private String message;
    private List<ParseResult> sub;
    private boolean debug;
    private boolean succesful;

    public ParseResult(String message, List<ParseResult> sub, boolean succesful, boolean debug) {
        this.message = message;
        this.sub = sub;
        this.debug = debug;
        this.succesful = succesful;
    }

    public static ParseResult succesful(String message) {
        return new ParseResult(message, new ArrayList<>(), true, false);
    }

    public static ParseResult unSuccesful(String message) {
        return new ParseResult(message, new ArrayList<>(), false, false);
    }

    public static ParseResult unSuccesful(String message, ParseResult... sub) {
        return new ParseResult(message, Items.buildList(sub), false, false);
    }

    public static ParseResult unSuccesful(String message, List<ParseResult> results) {
        return new ParseResult(message, results, false, false);
    }

    public static ParseResult debug(String message, ParseResult... sub) {
        return new ParseResult(message, Items.buildList(sub), false, true);
    }

    private void buildMessage(boolean withDebug, int indent, StringBuilder builder) {
        String ind = Strings.indent(indent);
        builder.append(ind).append("[").append(this.succesful ? "SUCCESFUL" : "UNSUCCESSFUL").append("] ").append(this.message).append(System.lineSeparator());
        this.sub.stream().filter(result -> !result.isDebug() || withDebug).forEach(result -> {
            result.buildMessage(withDebug, indent + 1, builder);
        });
    }

    public String buildMessage(boolean withDebug) {
        StringBuilder builder = new StringBuilder();
        this.buildMessage(withDebug, 0, builder);
        return builder.toString();
    }

    public String buildMessage() {
        return this.buildMessage(false);
    }

    public String getMessage() {
        return this.message;
    }

    public List<ParseResult> getNodes() {
        return Items.looseClone(this.sub);
    }

    public boolean isDebug() {
        return this.debug;
    }

    public boolean isSuccesful() {
        return this.succesful;
    }

    public ParseResult withDebug(boolean debug) {
        return new ParseResult(this.message, this.sub, this.succesful, debug);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String message;
        private List<ParseResult> sub;
        private boolean debug;
        private boolean succesful;

        public Builder() {
            this.message = "no additional information";
            this.sub = new ArrayList<>();
            this.debug = false;
            this.succesful = false;
        }

        public String getMessage() {
            return this.message;
        }

        public boolean isDebug() {
            return this.debug;
        }

        public boolean isSuccesful() {
            return this.succesful;
        }

        public Builder modify(UnaryOperator<ParseResult> nodeTransformer) {
            this.sub.replaceAll(nodeTransformer);
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder node(ParseResult node) {
            this.sub.add(node);
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder succesful(boolean succesful) {
            this.succesful = succesful;
            return this;
        }

        public ParseResult build() {
            return new ParseResult(this.message, this.sub, this.succesful, this.debug);
        }

        public Builder reset() {
            this.message = "no additional information";
            this.sub = new ArrayList<>();
            this.debug = false;
            this.succesful = false;
            return this;
        }

    }

}
