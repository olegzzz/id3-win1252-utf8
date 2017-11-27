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
package io.github.olegzzz.id3.config;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AppConfigBuilder")
class AppConfigBuilderTest {

    public static final String SUFFIX = ".mp3";

    @Nested
    @DisplayName("when new created")
    class Constructor {

        @Test
        @DisplayName("throws MissingOptionException when no options passed")
        void failOnEmptyCommandLine() throws ParseException {
            assertThrows(MissingOptionException.class, () -> new AppConfigBuilder(null));
        }

        @Test
        @DisplayName("throws MissingArgumentException when --file has not argument")
        void failOnEmptyArgument() throws ParseException {
            assertThrows(MissingArgumentException.class, () -> new AppConfigBuilder(new String[]{"--file"}));
        }

        @Test
        void noExWhenMandatoryOptionPassed() throws ParseException {
            new AppConfigBuilder(new String[]{"--file", "sample.mp3"});
        }
    }

    @Nested
    @DisplayName("when build called on unsupported file/directory")
    class BuildOnBadFile {

        @Test
        @DisplayName("throws IllegalArgumentException when target file does not exist")
        void absentFile() {
            assertThrows(IllegalArgumentException.class, () -> new AppConfigBuilder(new String[]{"--file", "absent.mp3"}).build());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when target file is not readable")
        void nonReadableFile() throws IOException {
            File file = File.createTempFile(UUID.randomUUID().toString(), SUFFIX);
            file.setReadable(false);
            assertThrows(IllegalArgumentException.class, () -> new AppConfigBuilder(new String[]{"--file", file.getAbsolutePath()}).build());
            assertTrue(file.delete(), () -> String.format("Unable to delete %s", file.getAbsolutePath()));
        }

        @Test
        @DisplayName("throws IllegalArgumentException when target file is not writable")
        void nonWritableFile() throws IOException {
            File file = File.createTempFile(UUID.randomUUID().toString(), SUFFIX);
            file.setWritable(false);
            assertThrows(IllegalArgumentException.class, () -> new AppConfigBuilder(new String[]{"--file", file.getAbsolutePath()}).build());
            assertTrue(file.delete(), () -> String.format("Unable to delete %s", file.getAbsolutePath()));
        }

        @Test
        @DisplayName("throws IllegalArgumentException when target dir is absent")
        void absentDir() throws IOException {
            assertThrows(IllegalArgumentException.class, () -> new AppConfigBuilder(new String[]{"--file", "somedir"}).build());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when target dir is not readable")
        void nonReadableDir() throws IOException {
            File file = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
            file.setReadable(false);
            assertThrows(IllegalArgumentException.class, () -> new AppConfigBuilder(new String[]{"--file", file.getAbsolutePath()}).build());
            assertTrue(file.delete(), () -> String.format("Unable to delete %s", file.getAbsolutePath()));
        }

        @Test
        @DisplayName("throws IllegalArgumentException when target dir is not writable")
        void nonWritableDir() throws IOException {
            File file = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
            file.setWritable(false);
            assertThrows(IllegalArgumentException.class, () -> new AppConfigBuilder(new String[]{"--file", file.getAbsolutePath()}).build());
            assertTrue(file.delete(), () -> String.format("Unable to delete %s", file.getAbsolutePath()));
        }

    }

    @Nested
    @DisplayName("when build called")
    class Build {

        File file;

        @BeforeEach
        void setup() throws IOException {
            file = File.createTempFile(UUID.randomUUID().toString(), SUFFIX);
        }

        @AfterEach
        void teardown() {
            file.delete();
        }

        @Test
        @DisplayName("parses verbose option")
        void verbose() throws ParseException {
            AppConfig config = new AppConfigBuilder(new String[]{"-v", "--file", file.getAbsolutePath()}).build();
            assertTrue(config.isVerbose(), "verbose");
            assertFalse(config.isDryRun(), "dry run");
            assertFalse(config.isNoBackups(), "no backups");
        }

        @Test
        @DisplayName("parses dry-run option")
        void dryRun() throws ParseException {
            AppConfig config = new AppConfigBuilder(new String[]{"--dry-run", "--file", file.getAbsolutePath()}).build();
            assertFalse(config.isVerbose(), "verbose");
            assertTrue(config.isDryRun(), "dry run");
            assertFalse(config.isNoBackups(), "no backups");
        }

        @Test
        @DisplayName("parses no-backups option")
        void noBackup() throws ParseException {
            AppConfig config = new AppConfigBuilder(new String[]{"--no-backup", "--file", file.getAbsolutePath()}).build();
            assertFalse(config.isVerbose(), "verbose");
            assertFalse(config.isDryRun(), "dry run");
            assertTrue(config.isNoBackups(), "no backups");
        }

        @Test
        @DisplayName("parses file option")
        void file() throws ParseException {
            File f = file.getAbsoluteFile();
            AppConfig config = new AppConfigBuilder(new String[]{"--file", f.getAbsolutePath()}).build();
            assertEquals(f.toURI(), config.getFile());
        }

        @Test
        @DisplayName("parses no-backups and verbose options")
        void noBackupAndVerbose() throws ParseException {
            AppConfig config = new AppConfigBuilder(new String[]{"-v", "--no-backup", "--file", file.getAbsolutePath()}).build();
            assertTrue(config.isVerbose(), "verbose");
            assertFalse(config.isDryRun(), "dry run");
            assertTrue(config.isNoBackups(), "no backups");
        }

        @Test
        @DisplayName("parses no-backups, verbose and dry-run options")
        void noBackupAndVerboseAndDryRun() throws ParseException {
            AppConfig config = new AppConfigBuilder(new String[]{"-v", "--no-backup", "--dry-run", "--file", file.getAbsolutePath()}).build();
            assertTrue(config.isVerbose(), "verbose");
            assertTrue(config.isDryRun(), "dry run");
            assertTrue(config.isNoBackups(), "no backups");
        }

    }

}

