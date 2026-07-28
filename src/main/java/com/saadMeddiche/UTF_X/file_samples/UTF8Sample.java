package com.saadMeddiche.UTF_X.file_samples;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Component
public class UTF8Sample implements FileSample {

    @Override
    public String getFileName() {
        return "utf_8.txt";
    }

    @Override
    public Path getFilePath() {
        return Path.of(getFileName());
    }

    @Override
    public Charset getStandardCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public AnsiElement[] getAnsiElements() {
        return new AnsiElement[]{AnsiColor.GREEN};
    }

}