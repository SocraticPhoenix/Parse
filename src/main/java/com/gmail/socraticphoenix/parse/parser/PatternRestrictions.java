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

import com.gmail.socraticphoenix.parse.parser.restrictions.AndRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.CompletedRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.LazyVariableRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.LengthRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.LiteralRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.RepeatingNonGreedyRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.RepeatingOrNoneNonGreedyRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.OptionalRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.OrRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.PredicateRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.RegexRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.RepeatingOrNoneRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.RepeatingRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.SequenceRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.SetAndUseRestriction;
import com.gmail.socraticphoenix.parse.parser.restrictions.SetRestriction;

import java.util.Collection;
import java.util.function.BiFunction;

public interface PatternRestrictions {

    static PatternRestriction length(int len) {
        return new LengthRestriction(len);
    }

    static PatternRestriction literal(String literal) {
        return new LiteralRestriction(literal);
    }

    static PatternRestriction sequence(PatternRestriction... restrictions) {
        return new SequenceRestriction(restrictions);
    }

    static PatternRestriction sequence(Collection<PatternRestriction> restrictions) {
        return PatternRestrictions.sequence(restrictions.toArray(new PatternRestriction[restrictions.size()]));
    }

    static PatternRestriction and(PatternRestriction... restrictions) {
        return new AndRestriction(restrictions);
    }

    static PatternRestriction and(Collection<PatternRestriction> restrictions) {
        return PatternRestrictions.and(restrictions.toArray(new PatternRestriction[restrictions.size()]));
    }

    static PatternRestriction longest(PatternRestriction... restrictions) {
        return new OrRestriction(true, restrictions);
    }

    static PatternRestriction or(PatternRestriction... restrictions) {
        return new OrRestriction(false, restrictions);
    }

    static PatternRestriction or(Collection<PatternRestriction> restrictions) {
        return PatternRestrictions.or(restrictions.toArray(new PatternRestriction[restrictions.size()]));
    }

    static PatternRestriction oneOf(String... strings) {
        PatternRestriction[] restrictions = new PatternRestriction[strings.length];
        for (int i = 0; i < restrictions.length; i++) {
            restrictions[i] = PatternRestrictions.literal(strings[i]);
        }
        return PatternRestrictions.or(restrictions);
    }

    static PatternRestriction repeating(PatternRestriction restriction) {
        return new RepeatingRestriction(restriction);
    }

    static PatternRestriction repeatingOrNone(PatternRestriction restriction) {
        return new RepeatingOrNoneRestriction(restriction);
    }

    static PatternRestriction repeatingOrNoneNonGreedy(PatternRestriction repeat, PatternRestriction next) {
        return new RepeatingOrNoneNonGreedyRestriction(repeat, next);
    }

    static PatternRestriction repeatingNonGreedy(PatternRestriction repeat, PatternRestriction next) {
        return new RepeatingNonGreedyRestriction(repeat, next);
    }

    static PatternRestriction optional(PatternRestriction restriction) {
        return new OptionalRestriction(restriction);
    }

    static PatternRestriction lazy(String name) {
        return new LazyVariableRestriction(name);
    }

    static PatternRestriction regex(String regex) {
        return new RegexRestriction(regex);
    }

    static PatternRestriction predicate(PatternRestriction generalMatch, BiFunction<String, Integer, PatternResult> predicate) {
        return new PredicateRestriction(generalMatch, predicate);
    }

    static PatternRestriction set(String name, PatternRestriction restriction) {
        return new SetRestriction(name, restriction);
    }

    static PatternRestriction list(PatternRestriction element, PatternRestriction separator) {
        return PatternRestrictions.sequence(element, PatternRestrictions.repeatingOrNone(PatternRestrictions.sequence(separator, element)));
    }

    static PatternRestriction setAndUse(String name, PatternRestriction restriction) {
        return new SetAndUseRestriction(name, restriction);
    }

    static PatternRestriction completed(PatternRestriction restriction) {
        return new CompletedRestriction(restriction);
    }

}
