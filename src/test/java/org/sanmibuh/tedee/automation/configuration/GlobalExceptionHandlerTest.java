package org.sanmibuh.tedee.automation.configuration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Serial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sanmibuh.ddd.domain.DomainException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
      .standaloneSetup(new StubController())
      .setControllerAdvice(new GlobalExceptionHandler())
      .build();
  }

  @Test
  void should_return400_withDetail_whenDomainExceptionIsThrown() throws Exception {
    mockMvc.perform(get("/test/domain-exception"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.detail").value("domain rule violated"));
  }

  @RestController
  static class StubController {

    @GetMapping("/test/domain-exception")
    void throwDomainException() {
      throw new StubDomainException();
    }

    static class StubDomainException extends DomainException {

      @Serial
      private static final long serialVersionUID = -1440054683212399969L;

      StubDomainException() {
        super("domain rule violated");
      }
    }
  }
}
