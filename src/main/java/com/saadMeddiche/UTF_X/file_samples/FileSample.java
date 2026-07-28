package com.saadMeddiche.UTF_X.file_samples;

import org.springframework.boot.ansi.AnsiElement;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public interface FileSample {

    String getFileName();

    Path getFilePath();

    Charset getStandardCharset();

    AnsiElement[] getAnsiElements(); // Ansi color & background used for log

    default boolean doesFileExist() {
        return Files.exists(getFilePath());
    }

}