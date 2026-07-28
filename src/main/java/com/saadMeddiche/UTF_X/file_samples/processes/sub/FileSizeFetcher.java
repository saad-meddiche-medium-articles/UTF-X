package com.saadMeddiche.UTF_X.file_samples.processes.sub;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@Slf4j
public class FileSizeFetcher implements SubProcess {

    @Override
    public String name() {
        return "file_size_fetcher";
    }

    @Override
    public void execute(List<FileSample> fileSamples, FileSampleConfig config) {

        for(var fileSample : fileSamples) {

            if(!fileSample.doesFileExist()) continue;

            BasicFileAttributeView attributeView = Files.getFileAttributeView(fileSample.getFilePath(), BasicFileAttributeView.class);

            BasicFileAttributes attributes = null;
            try {
                attributes = attributeView.readAttributes();
            }
            catch (Exception e) {
                log.info(AnsiOutput.toString(AnsiColor.BRIGHT_RED, " - Error: failed to fetch length for sample {}"), config.getTextSample());
            }

            if(attributes == null) continue;

            long sampleSize = attributes.size();

            log.info(AnsiOutput.toString(AnsiColor.BLUE, "File [{}] size is: {}"), fileSample.getFileName(), sampleSize);

        }

    }

}