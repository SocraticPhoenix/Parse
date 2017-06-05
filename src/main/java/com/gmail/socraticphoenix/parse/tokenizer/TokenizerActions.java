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
package com.gmail.socraticphoenix.parse.tokenizer;

import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.tokenizer.action.ConsumeAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.LazyVariableAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.LiteralAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.OptionalAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.RepeatingAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.RepeatingNonGreedyAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.RepeatingOrNoneAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.RepeatingOrNoneNonGreedyAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.SequenceAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.SetAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.SetAndUseAction;
import com.gmail.socraticphoenix.parse.tokenizer.action.WrapAction;

public interface TokenizerActions {

    static TokenizerAction wrap(String name, TokenizerAction action) {
        return new WrapAction(name, action);
    }

    static TokenizerAction wrap(String name, PatternRestriction restriction) {
        return new WrapAction(name, TokenizerActions.literal(restriction));
    }

    static TokenizerAction[] wrapAll(String name, TokenizerAction... actions) {
        TokenizerAction[] wrapped = new TokenizerAction[actions.length];
        for (int i = 0; i < wrapped.length; i++) {
            wrapped[i] = TokenizerActions.wrap(name, actions[i]);
        }
        return wrapped;
    }

    static TokenizerAction literal(PatternRestriction restriction) {
        return new LiteralAction(restriction);
    }

    static TokenizerAction lazy(String name) {
        return new LazyVariableAction(name);
    }

    static TokenizerAction set(String name, TokenizerAction action) {
        return new SetAction(name, action);
    }

    static TokenizerAction setAndUse(String name, TokenizerAction action) {
        return new SetAndUseAction(name, action);
    }

    static TokenizerAction sequence(TokenizerAction... actions) {
        return new SequenceAction(actions);
    }

    static TokenizerAction sequence(String name, TokenizerAction... actions) {
        return new SequenceAction(TokenizerActions.wrapAll(name, actions));
    }

    static TokenizerAction consume(TokenizerAction action) {
        return new ConsumeAction(action);
    }

    static TokenizerAction consume(PatternRestriction restriction) {
        return new ConsumeAction(TokenizerActions.literal(restriction));
    }

    static TokenizerAction list(TokenizerAction element, TokenizerAction separator) {
        return TokenizerActions.sequence(element, TokenizerActions.repeatingOrNone(TokenizerActions.sequence(separator, element)));
    }

    static TokenizerAction optional(TokenizerAction action) {
        return new OptionalAction(action);
    }

    static TokenizerAction repeating(TokenizerAction action) {
        return new RepeatingAction(action);
    }

    static TokenizerAction repeatingOrNone(TokenizerAction action) {
        return new RepeatingOrNoneAction(action);
    }

    static TokenizerAction repeatingOrNoneNonGreedy(TokenizerAction repeat, PatternRestriction next) {
        return new RepeatingOrNoneNonGreedyAction(repeat, next);
    }

    static TokenizerAction repeatingNonGreedy(TokenizerAction repeat, PatternRestriction next) {
        return new RepeatingNonGreedyAction(repeat, next);
    }

}
