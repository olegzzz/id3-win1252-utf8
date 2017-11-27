# Id3-win1252-UTF8

# Overview

The id3-win1252-utf8 tool converts ID3 tag text with cyrillic content erroneously encoded with Windows-1252 (CP-1252)
to UTF8. The source file has to be known to contain cyrillic content encoded with Windows-1252 encoding.

The idea emerged after certain old mp3 files were found to be incompatible with iTunes app to display their titles.

The application uses Jaudiotagger library (http://id3.org/Developer%20Information) which handles id3 tags according to
ID3v2 specification (http://id3.org/Developer%20Information).

# Requirements
* JDK/JRE 8

# Usage

## Build from sources

`./gradlew clean build`

## Compiled version

Download and extract zip/tar file

## Example usages

* `./bin/id3-win1252-utf8 --file /some/file.mp3`
* `./bin/id3-win1252-utf8 -v --file /some/directory`

# Command line arguments
## Mandatory
* `--file FILENAME` - a single file or a directory. If a directory passed it searches for mp3 files recursively.

## Optional
* `-v` or `--verbose` - verbose mode
* `--dry-run` - do not apply actual conversion
* `--no-backup` - do not make a backup file before applying changes