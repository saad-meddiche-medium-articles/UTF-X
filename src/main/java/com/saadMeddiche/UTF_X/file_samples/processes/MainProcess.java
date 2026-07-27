package com.saadMeddiche.UTF_X.file_samples.processes;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;
import com.saadMeddiche.UTF_X.file_samples.processes.sub.FileSampleLength;
import com.saadMeddiche.UTF_X.file_samples.processes.sub.FileSampleWriter;
import com.saadMeddiche.UTF_X.file_samples.processes.sub.SubProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component @Slf4j
@RequiredArgsConstructor
public class MainProcess implements CommandLineRunner {

    private final FileSampleConfig config;
    private final List<FileSample> fileSamples;

    @Override
    public void run(String... args) {

        this.startSubProcess(new FileSampleWriter());

        this.startSubProcess(new FileSampleLength());

    }

    private void startSubProcess(SubProcess subProcess) {

        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "Starting sub-process [{}]"), subProcess.name());
        subProcess.execute(fileSamples, config);
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "Sub-process [{}] is finished"), subProcess.name());

    }

}