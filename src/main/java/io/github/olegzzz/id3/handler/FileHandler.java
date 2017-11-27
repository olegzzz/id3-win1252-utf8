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

import io.github.olegzzz.id3.config.AppConfig;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileHandler {

    private static final Logger LOGGER = LogManager.getLogger(FileHandler.class);

    private final Predicate<String> win1252Predicate;
    private final Function<String, String> converter;
    private final Function<File, String> getBkpPath = (f) -> f.getAbsolutePath() + "~";

    private final Consumer<Stream<File>> chain = (s) -> s
            .map(this::backup)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::readMp3)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::convert)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(this::save);

    private final AppConfig config;

    public FileHandler(AppConfig config, Function<String, String> converter, Predicate<String> predicate) {
        this.config = config;
        this.converter = converter;
        this.win1252Predicate = predicate;
    }

    public void handle() {
        final File target = new File(config.getFile());
        if (target.isFile()) {
            chain.accept(Collections.singletonList(target).stream());
        } else {
            chain.accept(FileUtils.listFiles(target, new String[]{"mp3"}, true).parallelStream());
        }
    }

    protected Optional<File> backup(final File file) {
        if (config.needBackup()) {
            final File bkpFile = new File(getBkpPath.apply(file));
            try {
                FileUtils.copyFile(file, bkpFile);
                LOGGER.trace("File [{}] saved to [{}]", file, bkpFile);
                return Optional.of(file);
            } catch (IOException e) {
                LOGGER.error("Unable to backup file [{}]", file, e);
                return Optional.empty();
            }
        } else {
            LOGGER.trace("Dry run. No backup needed for [{}]", file);
            return Optional.of(file);
        }
    }

    protected Optional<MP3File> readMp3(final File file) {
        try {
            return Optional.of((MP3File) AudioFileIO.read(file));
        } catch (Exception e) {
            LOGGER.error("Unable to read mp3 file [{}]", file, e);
            return Optional.empty();
        }
    }

    protected void save(final MP3File mp3File) {
        if (!config.isDryRun()) {
            try {
                mp3File.commit();
                LOGGER.trace("Mp3 saved [{}]", mp3File.getFile().getPath());
            } catch (CannotWriteException e) {
                LOGGER.error("Unable to save mp3 file [{}]", mp3File, e);
            }
        }
    }

    protected Optional<MP3File> convert(final MP3File mp3File) {
        final Tag tag = mp3File.getTag();
        final MP3File finalFile = mp3File;
        try {
            final long cnt = Stream.of(FieldKey.values())
                    .filter(key -> win1252Predicate.test(tag.getFirst(key)))
                    .map(key -> {
                        final String original = tag.getFirst(key);
                        final String converted = converter.apply(original);
                        try {
                            tag.setField(key, converted);
                            LOGGER.trace("File: [{}] Field: [{}] Value: [{}] Set: [{}]", finalFile.getFile().getPath(), key, original, converted);
                        } catch (FieldDataInvalidException e) {
                            LOGGER.error("Unable to set [{}] = [{}]", key, converted, e);
                        }
                        return key;
                    }).count();
            if (cnt > 0) {
                LOGGER.debug("[{}] processed, [{}] fields changed", finalFile.getFile().getPath(), cnt);
            } else {
                LOGGER.debug("[{}] no win1252, skipped", finalFile.getFile().getPath());
            }

            return Optional.of(mp3File);
        } catch (Exception e) {
            LOGGER.error("Unable to process mp3 file [{}]", mp3File, e);
            return Optional.empty();
        }
    }

}
