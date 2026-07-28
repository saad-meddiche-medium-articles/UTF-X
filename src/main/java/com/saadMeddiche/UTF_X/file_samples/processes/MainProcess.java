package com.saadMeddiche.UTF_X.file_samples.processes;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;
import com.saadMeddiche.UTF_X.file_samples.processes.sub.FileSizeFetcher;
import com.saadMeddiche.UTF_X.file_samples.processes.sub.SubProcess;
import com.saadMeddiche.UTF_X.file_samples.processes.sub.TextLength;
import com.saadMeddiche.UTF_X.file_samples.processes.sub.TextWriter;
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

        this.startSubProcess(new TextWriter());

        this.startSubProcess(new TextLength());

        this.startSubProcess(new FileSizeFetcher());

    }

    private void startSubProcess(SubProcess subProcess) {

        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "Starting sub-process [{}]"), subProcess.name());
        subProcess.execute(fileSamples, config);
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_BLUE, "Sub-process [{}] is finished"), subProcess.name());

    }

}