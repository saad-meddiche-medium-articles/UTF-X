package com.saadMeddiche.UTF_X.file_samples.processes.sub;

import com.saadMeddiche.UTF_X.file_samples.FileSample;
import com.saadMeddiche.UTF_X.file_samples.config.FileSampleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
public class BytePrinter implements SubProcess {

    @Override
    public String name() {
        return "byte_writer";
    }

    @Override
    public void execute(List<FileSample> fileSamples, FileSampleConfig config) {

        for(var fileSample : fileSamples) {

            try {

                byte[] bytes = this.extractBytes(fileSample.getFilePath());

                log.info(" - File [{}] bytes are: {}", fileSample.getPaintedFileName(), formatBytes(bytes));

            }
            catch (Exception e) {
                log.info(AnsiOutput.toString(AnsiColor.BRIGHT_RED, " - Error: failed to extract bytes from file {}"), fileSample.getFileName());
            }

        }

    }

    private byte[] extractBytes(Path filePath) {

        try(FileInputStream inputStream = new FileInputStream(filePath.toFile())) {

            return inputStream.readAllBytes();

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String formatBytes(byte[] bytes) {

        StringJoiner sb = new StringJoiner(",");

        for(byte b : bytes) {
            sb.add(formatByte(b));
        }

        return "[" + sb.toString() + "]";

    }

    public String formatByte(byte b) {
        // b & 0xFF prevents sign extension for negative bytes
        String bits = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        return "0b" + bits.substring(0, 4) + "_" + bits.substring(4);
    }

}