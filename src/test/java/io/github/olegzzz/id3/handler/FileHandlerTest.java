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

import io.github.olegzzz.id3.MockitoExtension;
import io.github.olegzzz.id3.config.AppConfigBuilder;
import org.apache.commons.cli.ParseException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("A handler")
class FileHandlerTest {

    File file;
    FileHandler handler;

    Function<String, String> converter;
    Predicate<String> predicate;

    @BeforeEach
    void setup() throws IOException, ParseException {
        converter = spy(new Win1252Converter());
        predicate = spy(new Win1252Predicate());
        file = File.createTempFile(UUID.randomUUID().toString(), ".mp3");
        handler = new FileHandler(new AppConfigBuilder(new String[]{"--file", file.getAbsolutePath()}).build(),
                converter, predicate);
    }

    @AfterEach
    void tearDown() {
        file.delete();
    }

    @Nested
    @DisplayName("when backup option selected")
    class Backup{

        @Test
        @DisplayName("makes a copy of the file")
        void makeCopy() {
            handler.backup(file);
            File bkp = new File(file.getAbsolutePath() + "~");
            assertTrue(bkp.exists());
            assertTrue(bkp.delete());
        }

    }

    @Nested
    @DisplayName("when no backup option selected")
    class NoBackup{

        @BeforeEach
        void setup() throws IOException, ParseException {
            handler = new FileHandler(
                    new AppConfigBuilder(new String[]{"--no-backup", "--file", file.getAbsolutePath()}).build(),
                    converter, predicate);
        }


        @Test
        @DisplayName("does not make a copy of the file")
        void makeCopy() {
            handler.backup(file);
            File bkp = new File(file.getAbsolutePath() + "~");
            assertFalse(bkp.exists());
        }

    }


    @Test
    @DisplayName("returns empty when an exception")
    void readMP3ex() {
        assertFalse(handler.readMp3(file).isPresent());
    }

    @ExtendWith(MockitoExtension.class)
    @Nested
    class Convert {

        @Mock
        MP3File mp3File;
        @Spy
        Tag tag;

        @Test
        @DisplayName("returns instance of MP3File with converted fields")
        void convert() throws FieldDataInvalidException {

            String original = "FoêêÑÑ";
            String converted = converter.apply(original);

            when(mp3File.getFile()).thenReturn(file);
            when(mp3File.getTag()).thenReturn(tag);
            when(tag.getFirst(any(FieldKey.class))).thenReturn(original);

            handler.convert(mp3File);
            verify(predicate, times(FieldKey.values().length)).test(original);
            verify(converter, times(FieldKey.values().length + 1)).apply(original);
            verify(tag, times(FieldKey.values().length)).setField(any(FieldKey.class), eq(converted));

        }

    }

}