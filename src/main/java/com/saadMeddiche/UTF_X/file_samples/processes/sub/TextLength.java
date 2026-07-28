package com.saadMeddiche.UTF_X.file_samples.processes.sub;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import java.util.List;

@Slf4j
public class TextLength implements SubProcess {

    @Override
    public String name() {
        return "writer";
    }

    @Override
    public void execute(List<FileSample> fileSamples, FileSampleConfig config) {

        for(var fileSample : fileSamples) {

            byte[] fileBytes = config.getTextSample().getBytes(fileSample.getStandardCharset());

            log.info(" - Text bytes in {}: {}", fileSample.getPaintedFileName(), fileBytes);
            log.info(" - Simple bytes length in {}: {}", fileSample.getPaintedFileName(), fileBytes.length);

        }

    }

}