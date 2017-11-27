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
package io.github.olegzzz.id3;

import io.github.olegzzz.id3.config.AppConfigBuilder;
import io.github.olegzzz.id3.handler.FileHandler;
import io.github.olegzzz.id3.handler.Win1252Converter;
import io.github.olegzzz.id3.handler.Win1252Predicate;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.IOException;

import static io.github.olegzzz.id3.config.AppConfigBuilder.options;


public class App {

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String[] args) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, CannotWriteException {

        if (args.length == 0) {
            new HelpFormatter().printHelp("java -jar <app.jar>", options);
        } else {
            try {
                new FileHandler(new AppConfigBuilder(args).build(), new Win1252Converter(), new Win1252Predicate()).handle();
            } catch (ParseException e) {
                LOGGER.error("Unable to parse program options", e);
                System.exit(-1);
            }
        }

    }

}