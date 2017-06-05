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

import com.gmail.socraticphoenix.collect.coupling.Pair;
import com.gmail.socraticphoenix.parse.parser.PatternRestriction;
import com.gmail.socraticphoenix.parse.parser.PatternResult;
import com.gmail.socraticphoenix.parse.parser.expression.ParserExpressionReader;
import com.gmail.socraticphoenix.parse.token.Token;
import com.gmail.socraticphoenix.parse.tokenizer.TokenizerAction;

import static com.gmail.socraticphoenix.parse.parser.PatternRestrictions.*;
import static com.gmail.socraticphoenix.parse.tokenizer.TokenizerActions.*;

public class QuickTest {

    public static void main(String[] args) {
        String test = "[14567874567867890;2353254356334;213513535;351783.41235346771;-4234313.2531531;253153;1613616413614615432]";

        PatternRestriction arrayParsed = ParserExpressionReader.read("{completed:{sequence:[,{list:{sequence:{optional:-},{repeating:{or:0,1,2,3,4,5,6,7,8,9}},{optional:{sequence:.,{repeating:{or:0,1,2,3,4,5,6,7,8,9}}}}},{sequence:{rn: },;,{rn: }}},]}}");

        PatternRestriction number = sequence(optional(literal("-")),
                repeating(oneOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")),
                optional(sequence(literal("."), repeating(oneOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")))));
        PatternRestriction separator = sequence(repeatingOrNone(literal(" ")), literal(";"), repeatingOrNone(literal(" ")));
        PatternRestriction array = completed(sequence(literal("["), list(number, separator), literal("]")));
        System.out.println(array.match(test).buildMessage());
        System.out.println(arrayParsed.match(test).buildMessage());

        TokenizerAction numberAction = wrap("number", number);
        TokenizerAction arrayAction = sequence(consume(literal("[")), list(numberAction, consume(separator)), consume(literal("]")));
        Pair<Token, PatternResult> result = arrayAction.tokenize(test, "array");
        System.out.println(result.getA().write());
    }

}
