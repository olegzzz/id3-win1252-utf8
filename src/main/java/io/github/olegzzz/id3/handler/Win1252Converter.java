/**
 * MIT License
 *
 * Copyright (c) 2017 Oleg Zenchenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.olegzzz.id3.handler;

import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

public class Win1252Converter implements Function<String, String>{

    private static final int UTF8_START=0x0410;
    private static final int OFFSET = UTF8_START - Win1252Range.WIN1252_START;

    private static final IntPredicate rangePredicate = new Win1252Range();

    private final IntUnaryOperator win1252toUtf8 = (i) -> {
        if (rangePredicate.test(i))
            return i + OFFSET;
        else
            return i;
    };

    @Override
    public String apply(String s) {
        return new String(s.codePoints().map(win1252toUtf8).toArray(), 0, s.length());
    }
}
