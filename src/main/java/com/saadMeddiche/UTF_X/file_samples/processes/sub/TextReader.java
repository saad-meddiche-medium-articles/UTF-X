package com.saadMeddiche.UTF_X.file_samples.processes.sub;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.util.List;

@Slf4j
public class TextReader implements SubProcess {

    @Override
    public String name() {
        return "text_reader";
    }

    @Override
    public void execute(List<FileSample> fileSamples, FileSampleConfig config) {

        for(var fileSample : fileSamples) {

            try {
                String text = Files.readString(fileSample.getFilePath(), fileSample.getStandardCharset());
                log.info(" - Text in {} is: {}", fileSample.getPaintedFileName(), text);
            }
            catch (Exception e) {
                log.info(" - Error: failed to read text from file {}", fileSample.getPaintedFileName());
            }

        }

    }

}