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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class TokenParameters implements List<TokenParameters.Element> {
    private List<Element> elements;

    public TokenParameters() {
        this.elements = new ArrayList<>();
    }

    public List<Element> getElements() {
        return this.elements;
    }

    public String write() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.elements.size(); i++) {
            builder.append(this.elements.get(i).toString());
            if(i < this.elements.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public int size() {
        return elements.size();
    }

    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    public boolean removeIf(Predicate<? super Element> filter) {
        return elements.removeIf(filter);
    }

    public boolean addAll(Collection<? extends Element> c) {
        return elements.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends Element> c) {
        return elements.addAll(index, c);
    }

    public boolean retainAll(Collection<?> c) {
        return elements.retainAll(c);
    }

    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public boolean remove(Object o) {
        return elements.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return elements.containsAll(c);
    }

    public List<Element> subList(int fromIndex, int toIndex) {
        return elements.subList(fromIndex, toIndex);
    }

    public boolean contains(Object o) {
        return elements.contains(o);
    }

    public void replaceAll(UnaryOperator<Element> operator) {
        elements.replaceAll(operator);
    }

    public void add(int index, Element element) {
        elements.add(index, element);
    }

    public boolean removeAll(Collection<?> c) {
        return elements.removeAll(c);
    }

    public Element get(int index) {
        return elements.get(index);
    }

    public Iterator<Element> iterator() {
        return elements.iterator();
    }

    public ListIterator<Element> listIterator(int index) {
        return elements.listIterator(index);
    }

    public void sort(Comparator<? super Element> c) {
        elements.sort(c);
    }

    public Element remove(int index) {
        return elements.remove(index);
    }

    public Stream<Element> stream() {
        return elements.stream();
    }

    public void clear() {
        elements.clear();
    }

    public Stream<Element> parallelStream() {
        return elements.parallelStream();
    }

    public Element set(int index, Element element) {
        return elements.set(index, element);
    }

    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    public boolean add(Element element) {
        return elements.add(element);
    }

    public Object[] toArray() {
        return elements.toArray();
    }

    public ListIterator<Element> listIterator() {
        return elements.listIterator();
    }

    public void forEach(Consumer<? super Element> action) {
        elements.forEach(action);
    }

    public Spliterator<Element> spliterator() {
        return elements.spliterator();
    }

    public static class Element {
        private Optional<String> string;
        private Optional<Token> token;

        public Element(String string) {
            this.string = Optional.of(string);
            this.token = Optional.empty();
        }

        public Element(Token token) {
            this.token = Optional.of(token);
            this.string = Optional.empty();
        }

        public Optional<String> getString() {
            return this.string;
        }

        public Optional<Token> getToken() {
            return this.token;
        }

        public String toString() {
            if(this.getString().isPresent()) {
                return "\"".concat(Strings.escape(this.getString().get())).concat("\"");
            } else if (this.getToken().isPresent()) {
                return this.getToken().get().write();
            } else {
                return null;
            }
        }
    }

}
