package org.sanmibuh.tedee.lock.infrastructure.primary;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.sanmibuh.tedee.lock.infrastructure.TedeeInfrastructureConfiguration.CALLBACK_SECRET_HEADER;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.sanmibuh.tedee.lock.infrastructure.TedeeProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
final class TedeeWebhookAuthenticationInterceptor implements HandlerInterceptor {

  private final TedeeProperties tedeeProperties;

  @Override
  public boolean preHandle(
      final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
    if (isCallbackSecretValid(request.getHeader(CALLBACK_SECRET_HEADER))) {
      return true;
    }
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    return false;
  }

  private boolean isCallbackSecretValid(final @Nullable String callbackSecret) {
    return callbackSecret != null
        && MessageDigest.isEqual(
            tedeeProperties.callbackSecret().getBytes(UTF_8), callbackSecret.getBytes(UTF_8));
  }
}
