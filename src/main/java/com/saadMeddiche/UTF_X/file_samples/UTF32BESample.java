package com.saadMeddiche.UTF_X.file_samples;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
public class UTF32BESample extends UTF32Sample {

    @Override
    public String getFileName() {
        return "utf_32BE.txt";
    }

    @Override
    public Charset getStandardCharset() {
        return StandardCharsets.UTF_32BE;
    }

}