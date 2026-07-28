package com.saadMeddiche.UTF_X.file_samples;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
public class UTF16LESample extends UTF16Sample {

    @Override
    public String getFileName() {
        return "utf_16LE.txt";
    }

    @Override
    public Charset getStandardCharset() {
        return StandardCharsets.UTF_16LE;
    }

}