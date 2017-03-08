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

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.parse.CharacterStream;
import com.gmail.socraticphoenix.parse.ParserData;
import com.gmail.socraticphoenix.parse.Strings;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternRestrictions;
import com.gmail.socraticphoenix.parse.parser.expression.methods.LiteralParserMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParserExpressionReader {
    private static LiteralParserMethod literalParserMethod = new LiteralParserMethod();

    private CharacterStream stream;
    private ParserData mainData;
    private ParserData literalData;
    private ParserData argData;


    public ParserExpressionReader(String exp) {
        this.stream = new CharacterStream(exp);
        this.mainData = new ParserData();
        this.mainData
                .brackets('{', '}')
                .escapeChar('\\')
                .escape('{');
        this.literalData = new ParserData();
        this.literalData
                .escapeChar('\\')
                .escape('{');
        this.argData = new ParserData();
        this.argData.brackets('{', '}')
                .escapeChar('\\')
                .escape(',', "\\,");
    }

    public static PatternRestriction read(String content) {
        ParserExpressionReader reader = new ParserExpressionReader(content);
        return reader.remainingSequence();
    }

    public boolean hasNext() {
        return this.stream.hasNext();
    }

    public PatternRestriction next() {
        if (this.stream.isNext('{')) {
            String s = this.stream.nextUntil(this.mainData.reset());
            String cut = Strings.cutFirst(Strings.cutLast(s));
            String[] pieces = cut.split(":", 2);
            String name;
            String args;
            if (pieces.length == 2) {
                name = pieces[0];
                args = pieces[1];
            } else {
                name = cut;
                args = "";
            }
            Optional<ParserExpressionMethod> method = ParserExpressionRegistry.get(name);
            if (method.isPresent()) {
                List<String> arguments = this.parseArguments(args);
                if (method.get().accepts(arguments)) {
                    return method.get().accept(arguments);
                }
            }
            return ParserExpressionReader.literalParserMethod.accept(Items.buildList(s));
        } else {
            String s = this.stream.nextUntil(c -> c == '{', this.literalData.reset());
            return ParserExpressionReader.literalParserMethod.accept(Items.buildList(s));
        }
    }

    public PatternRestriction remainingSequence() {
        List<PatternRestriction> restrictions = new ArrayList<>();
        while (this.hasNext()) {
            restrictions.add(this.next());
        }
        return restrictions.size() == 1 ? restrictions.get(0) : PatternRestrictions.sequence(restrictions);
    }

    private List<String> parseArguments(String s) {
        CharacterStream sub = new CharacterStream(s);
        List<String> vals = new ArrayList<>();
        while (sub.hasNext()) {
            vals.add(sub.nextUntil(c -> c == ',', this.argData.reset()));
            sub.consume(',');
        }
        return vals;
    }

}
