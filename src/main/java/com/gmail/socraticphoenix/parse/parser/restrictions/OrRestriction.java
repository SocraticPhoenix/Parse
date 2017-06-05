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
package com.gmail.socraticphoenix.parse.parser.restrictions;

import com.gmail.socraticphoenix.parse.parser.PatternResult;
import com.gmail.socraticphoenix.parse.parser.PatternContext;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;

import java.util.ArrayList;
import java.util.List;

public class OrRestriction implements PatternRestriction {
    private PatternRestriction[] restrictions;

    public OrRestriction(PatternRestriction... restrictions) {
        this.restrictions = restrictions.clone();
    }

    @Override
    public PatternResult match(String string, int start, PatternContext context) {
        List<PatternResult> failed = new ArrayList<>();

        PatternResult longest = null;
        for (PatternRestriction restriction : this.restrictions) {
            PatternResult result = restriction.match(string, start, context);
            if (result.isSuccesful() && (longest == null || result.getEnd() > longest.getEnd())) {
                longest = result;
            } else {
                failed.add(result);
            }
        }
        return longest == null ? new PatternResult(start, PatternResult.Type.SYNTAX_ERROR, "All tests failed", failed, false) : longest;
    }

}
