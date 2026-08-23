package org.sanmibuh.cqrs.infrastructure;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.example.StubCommandHandler;
import com.example.TestApplication;

@SpringBootTest(
  classes = TestApplication.class)
class CQRSAutoConfigurationTest {

  @Autowired
  private ApplicationContext context;

  @Test
  void should_registerHandlerBean_whenHandlerIsOutsideLibraryPackage() {
    then(context.getBeansOfType(StubCommandHandler.class)).isNotEmpty();
  }
}
