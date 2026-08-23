package org.sanmibuh.cqrs.infrastructure;

import java.util.List;
import org.sanmibuh.cqrs.domain.CommandBus;
import org.sanmibuh.cqrs.domain.CommandHandler;
import org.sanmibuh.cqrs.domain.QueryBus;
import org.sanmibuh.cqrs.domain.QueryHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@AutoConfiguration
@ComponentScan(
  basePackages = "org.sanmibuh",
  useDefaultFilters = false,
  includeFilters = {
    @ComponentScan.Filter(
      type = FilterType.ASSIGNABLE_TYPE,
      classes = CommandHandler.class),
    @ComponentScan.Filter(
      type = FilterType.ASSIGNABLE_TYPE,
      classes = QueryHandler.class)
  })
public class CQRSAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(CommandBus.class)
  public SimpleCommandBus commandBus(final List<CommandHandler<?>> handlers) {
    return new SimpleCommandBus(handlers);
  }

  @Bean
  @ConditionalOnMissingBean(QueryBus.class)
  public SimpleQueryBus queryBus(final List<QueryHandler<?, ?>> handlers) {
    return new SimpleQueryBus(handlers);
  }
}
