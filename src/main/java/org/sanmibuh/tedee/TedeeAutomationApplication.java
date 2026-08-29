package org.sanmibuh.tedee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TedeeAutomationApplication {

  static void main(final String[] args) {
    SpringApplication.run(TedeeAutomationApplication.class, args);
  }
}
