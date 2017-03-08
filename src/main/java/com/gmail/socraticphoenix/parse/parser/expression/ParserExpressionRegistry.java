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
package com.gmail.socraticphoenix.parse.parser.expression;

import com.gmail.socraticphoenix.parse.parser.expression.methods.AndParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.CompletedParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.LazyParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.ListParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.LiteralParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.OptionalParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.OrParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.RegexParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.RepeatingOrNoneParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.RepeatingParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.SequenceParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.SetAndUseParserMethod;
import com.gmail.socraticphoenix.parse.parser.expression.methods.SetParserMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParserExpressionRegistry {
    private static Map<String, ParserExpressionMethod> methods;

    static  {
        ParserExpressionRegistry.methods = new HashMap<>();

        register(new AndParserMethod(), "and", "&&");
        register(new CompletedParserMethod(), "completed", "co");
        register(new LazyParserMethod(), "var", "va");
        register(new LiteralParserMethod(), "literal", "li");
        register(new OptionalParserMethod(), "optional", "op");
        register(new OrParserMethod(), "or", "||");
        register(new RegexParserMethod(), "regex", "rx");
        register(new RepeatingOrNoneParserMethod(), "repeatingOrNone", "rn");
        register(new RepeatingParserMethod(), "repeating", "re");
        register(new SequenceParserMethod(), "sequence", "se");
        register(new SetAndUseParserMethod(), "setAndUse", "su");
        register(new SetParserMethod(), "set", "st");
        register(new ListParserMethod(), "list", "lt");
    }

    public static void register(ParserExpressionMethod method, String... names) {
        for(String name : names) {
            if(!ParserExpressionRegistry.methods.containsKey(name)) {
                ParserExpressionRegistry.methods.put(name, method);
            } else {
                throw new IllegalArgumentException("A parser method is already registered under the name: " + name);
            }
        }
    }

    public static Optional<ParserExpressionMethod> get(String name) {
        return Optional.ofNullable(ParserExpressionRegistry.methods.get(name));
    }

}
