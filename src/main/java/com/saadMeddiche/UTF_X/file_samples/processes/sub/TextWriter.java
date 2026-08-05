package com.saadMeddiche.UTF_X.file_samples.processes.sub;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import java.nio.file.Files;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Slf4j
public class TextWriter implements SubProcess {

    @Override
    public String name() {
        return "text_writer";
    }

    @Override
    public void execute(List<FileSample> fileSamples, FileSampleConfig config) {

        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, " - Sample chars: {}"), config.getTextSample().toCharArray());
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_WHITE, " - Simple char length: {}"), config.getTextSample().length());

        for(var fileSample : fileSamples) {

            try {
                Files.writeString(fileSample.getFilePath(),  config.getTextSample(), fileSample.getStandardCharset(), TRUNCATE_EXISTING, CREATE);
            }
            catch (Exception e) {
                log.info(AnsiOutput.toString(AnsiColor.BRIGHT_RED, " - Error: failed to write text in sample {}"), fileSample.getFileName());
            }

        }

    }

}