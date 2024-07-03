package com.java.parzival;

import com.java.parzival.Configurations.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableMongoAuditing
@EnableConfigurationProperties(RsaKeyProperties.class)
public class ParzivalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParzivalApplication.class, args);
    }

}
