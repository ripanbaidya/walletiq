package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Application API properties
 */
@ConfigurationProperties(prefix = "app.api")
public record ApplicationAPIProperties(
        Servers servers

) {
    /**
     * Servers
     */
    public record Servers(
            List<String> dev,
            List<String> prod
    ) {
    }
}
