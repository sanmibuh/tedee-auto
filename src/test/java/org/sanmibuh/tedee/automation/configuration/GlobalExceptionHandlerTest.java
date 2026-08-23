package org.sanmibuh.tedee.automation.configuration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.Serial;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sanmibuh.cqrs.domain.HandlerNotFoundException;
import org.sanmibuh.ddd.domain.DomainException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.SneakyThrows;

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
  @SneakyThrows
  void should_return400_withDetail_whenDomainExceptionIsThrown() {
    mockMvc.perform(get("/test/domain-exception"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.detail").value("domain rule violated"));
  }

  @Test
  @SneakyThrows
  void should_return500_whenHandlerNotFoundExceptionIsThrown() {
    mockMvc.perform(get("/test/handler-not-found"))
      .andExpect(status().isInternalServerError());
  }

  @RestController
  static class StubController {

    @GetMapping("/test/domain-exception")
    void throwDomainException() {
      throw new StubDomainException();
    }

    @GetMapping("/test/handler-not-found")
    void throwHandlerNotFoundException() {
      throw new HandlerNotFoundException(String.class);
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
