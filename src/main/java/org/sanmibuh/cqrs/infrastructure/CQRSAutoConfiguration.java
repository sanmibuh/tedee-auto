package org.sanmibuh.cqrs.infrastructure;

import org.sanmibuh.cqrs.domain.CommandHandler;
import org.sanmibuh.cqrs.domain.QueryHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@AutoConfiguration
@ComponentScan(basePackageClasses = CQRSAutoConfiguration.class)
@ComponentScan(
    basePackages = "org.sanmibuh",
    useDefaultFilters = false,
    includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CommandHandler.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = QueryHandler.class)
    })

public class CQRSAutoConfiguration {
}
