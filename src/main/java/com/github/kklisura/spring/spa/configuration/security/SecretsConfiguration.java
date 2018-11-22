package com.github.kklisura.spring.spa.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Secrets configuration.
 *
 * @author Kenan Klisura
 */
@Configuration
@PropertySource("classpath:.secrets.properties")
public class SecretsConfiguration {
}
