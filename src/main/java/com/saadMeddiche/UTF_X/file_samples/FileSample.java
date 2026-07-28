package com.saadMeddiche.UTF_X.file_samples;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public interface FileSample {

    String getFileName();

    Path getFilePath();

    Charset getStandardCharset();

    default boolean doesFileExist() {
        return Files.exists(getFilePath());
    }

}