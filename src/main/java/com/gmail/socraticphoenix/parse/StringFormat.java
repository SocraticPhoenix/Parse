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
import com.gmail.socraticphoenix.collect.coupling.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringFormat {
    private List<Switch<String, String>> elements;

    public StringFormat(List<Switch<String, String>> elements) {
        this.elements = elements;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static StringFormat fromString(String string) {
        StringFormat.Builder builder = StringFormat.builder();
        CharacterStream stream = new CharacterStream(string);

        StringBuilder current = new StringBuilder();
        boolean escaped = false;
        boolean readingKey = false;
        while (stream.hasNext()) {
            char c = stream.next().get();
            if (c == '\\' && !escaped) {
                escaped = true;
                current.append(c);
            } else if (c == '$' && !escaped && !readingKey && stream.hasNext() && stream.peek().get() == '{') {
                stream.next();
                readingKey = true;
                escaped = false;
                builder.literal(current.toString());
                current = new StringBuilder();
            } else if (c == '}' && !escaped && readingKey) {
                readingKey = false;
                escaped = false;
                builder.variable(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
                escaped = false;
            }
        }

        if (!readingKey) {
            builder.literal(current.toString());
        } else {
            builder.variable(current.toString());
        }

        return builder.build();
    }

    public List<Switch<String, String>> getElements() {
        return Items.looseClone(this.elements, ArrayList::new);
    }

    public Filler filler() {
        return new Filler(this);
    }

    public String fill(Map<String, String> values) {
        StringBuilder builder = new StringBuilder();
        this.elements.forEach(element -> {
            if (element.getA().isPresent()) {
                builder.append(element.getA().get());
            } else if (element.getB().isPresent()) {
                String name = element.getB().get();
                if (values.containsKey(name)) {
                    builder.append(values.get(name));
                } else {
                    builder.append("${").append(name).append("}");
                }
            } else {
                builder.append("<undefined>");
            }
        });
        return builder.toString();
    }

    public static class Filler {
        private Map<String, String> variables;
        private StringFormat format;

        public Filler(StringFormat format) {
            this.format = format;
            this.variables = new HashMap<>();
        }

        public Filler var(String name, String value) {
            this.variables.put(name, value);
            return this;
        }

        public Filler var(String name, int[] value) {
            return this.var(name, Arrays.toString(value));
        }

        public Filler var(String name, byte[] value) {
            return this.var(name, Arrays.toString(value));
        }

        public Filler var(String name, long[] value) {
            return this.var(name, Arrays.toString(value));
        }

        public Filler var(String name, short[] value) {
            return this.var(name, Arrays.toString(value));
        }

        public Filler var(String name, float[] value) {
            return this.var(name, Arrays.toString(value));
        }

        public Filler var(String name, double[] value) {
            return this.var(name, Arrays.toString(value));
        }

        public Filler var(String name, boolean[] value) {
            return this.var(name, Arrays.toString(value));
        }

        public Filler var(String name, Object[] value) {
            return this.var(name, Arrays.deepToString(value));
        }

        public Filler var(String name, Object value) {
            return this.var(name, String.valueOf(value));
        }

        public String fill() {
            return this.format.fill(this.variables);
        }

    }

    public static class Builder {
        private List<Switch<String, String>> elements;

        public Builder() {
            this.elements = new ArrayList<>();
        }

        public Builder literal(String literal) {
            this.elements.add(Switch.ofA(literal));
            return this;
        }

        public Builder variable(String name) {
            this.elements.add(Switch.ofB(name));
            return this;
        }

        public StringFormat build() {
            return new StringFormat(this.elements);
        }
    }

}
