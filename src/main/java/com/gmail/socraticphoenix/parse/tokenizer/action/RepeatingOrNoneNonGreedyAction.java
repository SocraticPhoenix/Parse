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
package com.gmail.socraticphoenix.parse.tokenizer.action;

import com.gmail.socraticphoenix.collect.coupling.Pair;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternResult;
import com.gmail.socraticphoenix.parse.token.TokenParameters;
import com.gmail.socraticphoenix.parse.tokenizer.TokenizerAction;
import com.gmail.socraticphoenix.parse.tokenizer.TokenizerContext;

import java.util.ArrayList;
import java.util.List;

public class RepeatingOrNoneNonGreedyAction implements TokenizerAction {
    private TokenizerAction nonGreedy;
    private PatternRestriction next;

    public RepeatingOrNoneNonGreedyAction(TokenizerAction nonGreedy, PatternRestriction next) {
        this.nonGreedy = nonGreedy;
        this.next = next;
    }


    @Override
    public Pair<List<TokenParameters.Element>, PatternResult> tokenize(String string, int start, TokenizerContext context) {
        List<TokenParameters.Element> elements = new ArrayList<>();
        while (!(this.next.match(string, start, context.getPatternContext()).isSuccesful())) {
            Pair<List<TokenParameters.Element>, PatternResult> result = this.nonGreedy.tokenize(string, start, context);
            start = result.getB().getEnd();
            if (!result.getB().isSuccesful()) {
                return Pair.of(elements, result.getB());
            } else {
                elements.addAll(result.getA());
            }
        }

        return Pair.of(elements, PatternResult.succesful(start));    }

}
