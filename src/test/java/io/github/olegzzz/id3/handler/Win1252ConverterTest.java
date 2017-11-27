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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("A converter")
class Win1252ConverterTest {

    Win1252Converter converter;

    @BeforeEach
    void setup() {
        converter = new Win1252Converter();
    }

    @Test
    @DisplayName("skips if no win1252")
    void skip() {
        String expected = " Foo";
        assertEquals(expected, converter.apply(expected));
    }

    @Test
    @DisplayName("converts win1252 and skips non-win1252")
    void some() {
        String original = "SomÀÁêe";
        String expected = "SomАБкe";
        assertEquals(expected, converter.apply(original));
    }

    @Test
    @DisplayName("converts win1252")
    void all() {
        String original = "ÀÁê";
        String expected = "АБк";
        assertEquals(expected, converter.apply(original));
    }



}