package org.sanmibuh.tedee.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TedeeAutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TedeeAutomationApplication.class, args);
    }
}
