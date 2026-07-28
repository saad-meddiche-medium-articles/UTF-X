package com.saadMeddiche.UTF_X.file_samples;

import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface FileSample {

    String getFileName();

    Path getFilePath();

    Charset getStandardCharset();

    AnsiElement[] getAnsiElements(); // Ansi color & background used for log

    default boolean doesFileExist() {
        return Files.exists(getFilePath());
    }

    default String getPaintedFileName() {

        List<Object> objects = new ArrayList<>(getAnsiElements().length + 2);

        objects.addAll(Arrays.asList(getAnsiElements()));

        objects.add(getFileName());

        return AnsiOutput.toString(objects.toArray());

    }

}