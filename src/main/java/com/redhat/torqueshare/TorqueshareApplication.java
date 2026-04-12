package com.redhat.torqueshare;

import com.redhat.torqueshare.configs.S3Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TorqueshareApplication {

    public static void main(String[] args) {
        SpringApplication.run(TorqueshareApplication.class, args);
    }

}
