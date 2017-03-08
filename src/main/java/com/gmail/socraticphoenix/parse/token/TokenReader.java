/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 socraticphoenix@gmail.com
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
 *
 * @author Socratic_Phoenix (socraticphoenix@gmail.com)
 */
package com.gmail.socraticphoenix.parse.token;

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.parse.CharacterStream;
import com.gmail.socraticphoenix.parse.ParserData;
import com.gmail.socraticphoenix.parse.Strings;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TokenReader {
    private static ParserData parserData = Strings.javaEscapeFormat()
            .brackets('(', ')')
            .quote('"');
    private CharacterStream stream;
    private Predicate<String> tokenName;
    private Predicate<String> tokenValue;

    public TokenReader(String tokenString, Predicate<String> tokenName, Predicate<String> tokenValue) throws TokenizerException {
        this.stream = new CharacterStream(tokenString);
        this.tokenName = tokenName;
        this.tokenValue = tokenValue;
    }

    public TokenReader(String tokenString) throws TokenizerException {
        this(tokenString, s -> true, s1 -> true);
    }

    public Token nextToken() throws TokenizerException {
        String name = this.nextTokenName();
        this.stream.consumeAll(' ');
        String param = this.nextTokenParameter();
        Token token = new Token(name);
        List<String> params = Items.buildList(param);
        while (this.stream.hasNext()) {
            String nextParam = this.nextTokenParameter();
            params.add(nextParam);
        }
        this.stream.back();

        for (String s : params) {
            if (!s.equals("") && !s.equals("null")) {
                if (s.startsWith(Token.TOKEN_PREFIX)) {
                    TokenReader tokenReader = new TokenReader(s, this.tokenName, this.tokenValue);
                    token.addElement(tokenReader.nextToken());
                } else {
                    token.addElement(Strings.deEscape(Strings.cutLast(s.substring(1))));
                }
            }
        }

        return token;
    }

    public boolean hasNext() {
        return this.stream.hasNext();
    }

    public String nextTokenName() throws TokenizerException {
        this.stream.consumeAll(' ');
        String value = this.stream.nextUntil('(');
        this.stream.consumeAll(' ');
        if (!value.startsWith(Token.TOKEN_PREFIX)) {
            throw new TokenizerException("Token \"" + value + "\" does not start with token prefix \"" + Token.TOKEN_PREFIX + "\"");
        }

        return value.replaceFirst(Pattern.quote(Token.TOKEN_PREFIX), "");
    }

    public String nextTokenParameter() {
        return this.stream.nextUntil(c -> c == ',', TokenReader.parserData.reset());
    }


}
