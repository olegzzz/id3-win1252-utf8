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

import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.File;
import java.util.stream.Stream;

import static io.github.olegzzz.id3.config.AppConfigBuilder.Opt.*;


public class AppConfigBuilder {

    private static final Logger LOGGER = LogManager.getLogger(AppConfigBuilder.class);

    enum Opt {
        DRY(Option.builder().longOpt("dry-run").hasArg(false).desc("do not actually change files").build()),
        NO_BKP(Option.builder().longOpt("no-backup").desc("do not make a copy of a file before processing").build()),
        FILE(Option.builder().longOpt("file").desc("single file or root directory for processing").hasArg(true).argName("FILE").numberOfArgs(1).required(true).build()),
        VERBOSE(new Option("v", "verbose"));

        private final Option opt;

        Opt(Option opt) {
            this.opt = opt;
        }

        public Option getOpt() {
            return opt;
        }
    }

    public static final Options options;

    static {
        options  = new Options();
        Stream.of(Opt.values()).map(Opt::getOpt).forEach(options::addOption);
    }

    private CommandLine cmdLine;

    public AppConfigBuilder(String[] args) throws ParseException {
        this.cmdLine = new DefaultParser().parse(options, args);
    }



    public AppConfig build() {
        final File target = new File(cmdLine.getOptionValue(FILE.opt.getLongOpt()));
        if (target.exists() && target.canRead() && target.canWrite()) {
            AppConfig  cfg = new AppConfig();

            final boolean isVerbose = cmdLine.hasOption(VERBOSE.opt.getOpt());

            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            Configuration config = ctx.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            loggerConfig.setLevel(isVerbose ? Level.TRACE : Level.DEBUG);
            ctx.updateLoggers();

            cfg.setVerbose(isVerbose);
            LOGGER.trace("{}: {}", VERBOSE.opt.getDescription(), cfg.isVerbose());

            cfg.setDryRun(cmdLine.hasOption(DRY.opt.getLongOpt()));
            LOGGER.trace("{}: {}", DRY.opt.getDescription(), cfg.isDryRun());

            cfg.setFile(target.toURI());
            LOGGER.trace("{}: {}", FILE.opt.getDescription(), cfg.getFile());

            cfg.setNoBackups(cmdLine.hasOption(NO_BKP.opt.getLongOpt()));
            LOGGER.trace("{}: {}", NO_BKP.opt.getDescription(), cfg.isNoBackups());

            return cfg;
        } else {
            throw new IllegalArgumentException(String.format("Location %s is not accessible", target));
        }

    }
}
