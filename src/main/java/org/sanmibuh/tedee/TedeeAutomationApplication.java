package org.sanmibuh.tedee;

import org.sanmibuh.framework.persistence.PersistenceRuntimeHints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ImportRuntimeHints(PersistenceRuntimeHints.class)
public class TedeeAutomationApplication {

  static void main(final String[] args) {
    SpringApplication.run(TedeeAutomationApplication.class, args);
  }
}
