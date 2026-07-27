package com.saadMeddiche.UTF_X.file_samples.processes.sub;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;

import java.util.List;

public interface SubProcess {
    String name();
    void execute(List<FileSample> fileSamples, FileSampleConfig config);
}