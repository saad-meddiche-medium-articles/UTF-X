package com.saadMeddiche.UTF_X.file_samples.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter @Setter
@Configuration @Validated
@ConfigurationProperties(prefix = "file-sample")
public class FileSampleConfig {

    private String textSample;

}