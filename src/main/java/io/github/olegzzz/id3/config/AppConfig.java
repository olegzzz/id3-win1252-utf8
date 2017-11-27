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

import java.net.URI;

public class AppConfig {

    public static final boolean DEFAULT_DRY_RUN = true;
    public static final boolean DEFAULT_NO_BKPS = false;
    public static final boolean DEFAULT_VERBOSE = true;


    private boolean noBackups;
    private boolean dryRun;
    private URI file;
    private boolean verbose;

    AppConfig() {
        this.noBackups = DEFAULT_NO_BKPS;
        this.dryRun = DEFAULT_DRY_RUN;
        this.verbose = DEFAULT_VERBOSE;
    }

    void setNoBackups(boolean noBackups) {
        this.noBackups = noBackups;
    }

    void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    void setFile(URI file) {
        this.file = file;
    }

    public boolean needBackup() {
        return !dryRun && !noBackups;
    }

    public URI getFile() {
        return file;
    }

    public boolean isNoBackups() {
        return noBackups;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
