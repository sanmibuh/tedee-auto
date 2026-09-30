package org.sanmibuh.tedee;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@EnableConfigurationProperties(ServerProperties.class)
@ImportRuntimeHints(ServerRuntimeHints.class)
public class ServerConfiguration {}
