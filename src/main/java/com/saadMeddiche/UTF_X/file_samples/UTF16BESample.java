package com.saadMeddiche.UTF_X.file_samples;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Component
public class UTF16BESample implements FileSample {

    @Override
    public String getFileName() {
        return "utf_16BE.txt";
    }

    @Override
    public Path getFilePath() {
        return Path.of(getFileName());
    }

    @Override
    public Charset getStandardCharset() {
        return StandardCharsets.UTF_16BE;
    }

}