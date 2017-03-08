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

import com.gmail.socraticphoenix.parse.Strings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Token implements Cloneable {
    public static final String TOKEN_PREFIX = "@";

    private String name;
    private TokenParameters parameters;

    public Token(String name) {
        if(!name.equals(Strings.escape(name))) {
            throw new IllegalArgumentException("Tokens cannot have names that require escaping (\"".concat(name).concat("\" was not equal to \"".concat(Strings.escape(name)).concat("\")")));
        }

        this.name = name;
        this.parameters = new TokenParameters();
    }

    public Optional<Token> first(String name) {
        Optional<TokenParameters.Element> elementOptional = this.parameters.stream().filter(element -> element.getToken().isPresent() && element.getToken().get().getName().equals(name)).findFirst();
        if(elementOptional.isPresent()) {
            return elementOptional.get().getToken();
        } else {
            return Optional.empty();
        }
    }

    public List<Token> all(String name) {
        return this.parameters.stream().filter(element -> element.getToken().isPresent() && element.getToken().get().getName().equals(name)).map(element -> element.getToken().get()).collect(Collectors.toList());
    }

    public Token addElement(String element) {
        this.parameters.add(new TokenParameters.Element(element));
        return this;
    }

    public Token addElement(Token token) {
        this.parameters.add(new TokenParameters.Element(token));
        return this;
    }

    public Token addElement(TokenParameters.Element element) {
        this.parameters.add(element);
        return this;
    }

    public Token clone() {
        Token token = new Token(this.name);
        for(TokenParameters.Element element : this.parameters) {
            if(element.getString().isPresent()) {
                token.addElement(element.getString().get());
            } else {
                token.addElement(element.getToken().get().clone());
            }
        }
        return token;
    }

    public String getName() {
        return this.name;
    }

    public TokenParameters getParameters() {
        return this.parameters;
    }

    public String writePretty() {
        return this.writePretty(0);
    }

    public String writePretty(int i) {
        String indent = Strings.indent(i);
        String indent2 = Strings.indent(i + 1);
        String ls = System.lineSeparator();
        StringBuilder builder = new StringBuilder();
        builder.append(indent).append(TOKEN_PREFIX).append(this.name).append("(").append(ls);
        for (int j = 0; j < this.parameters.size(); j++) {
            TokenParameters.Element element = this.parameters.get(j);
            if(element.getString().isPresent()) {
                builder.append(indent2).append(element.getString().get());
            } else {
                builder.append(element.getToken().get().writePretty(i + 1));
            }
            if(j < this.parameters.size() - 1) {
                builder.append(",").append(ls);
            }

        }
        return builder.append(ls).append(indent).append(")").toString();
    }

    public String write() {
        StringBuilder builder = new StringBuilder();
        builder.append(Token.TOKEN_PREFIX).append(this.name).append("(").append(this.parameters.write()).append(")");
        return builder.toString();
    }

    public static Token parse(String tokenString) throws TokenizerException {
        TokenReader tokenReader = new TokenReader(tokenString);
        return tokenReader.nextToken();
    }

}
