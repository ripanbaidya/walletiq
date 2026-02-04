package com.walletiq.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a configuration prerequisite for accessing an API endpoint.
 * <p>Endpoints annotated with {@code @RequireConfiguration} are intended
 * to be available only when a specific configuration property or feature
 * flag is enabled.</p>
 * <p>This annotation expresses an API contract and is primarily used for
 * documentation and tooling purposes (e.g. OpenAPI / Swagger). It does
 * <b>not</b> enforce configuration checks at runtime by itself.</p>
 * <p>Runtime enforcement must be handled separately using feature-flag
 * mechanisms, conditional logic, filters, or API gateways.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * {@code
 * @RequireConfiguration(path = "feature.new-user-flow")
 * @PostMapping("/users")
 * public User createUser(...) { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireConfiguration {

    /**
     * Configuration property or feature-flag path that must be enabled
     * for the endpoint to be accessible.
     *
     * @return configuration property path
     */
    String path();
}
