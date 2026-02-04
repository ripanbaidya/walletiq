package com.walletiq.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares rate-limit metadata for an API endpoint.
 * <p>This annotation is used to describe how frequently an endpoint can
 * be called within a given time window. It is primarily intended for API
 * documentation and tooling (e.g. OpenAPI / Swagger) and does not enforce
 * rate limiting by itself.</p>
 * <p>Runtime enforcement of rate limits must be handled separately by
 * infrastructure components such as filters, interceptors, gateways, or
 * external rate-limiting services.</p>
 * <p><b>Example usage:</b></p>
 * <pre>
 * {@code
 * @RateLimited(maxRequests = 10, windowSeconds = 60)
 * @GetMapping("/users")
 * public User getUser() { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    /**
     * Maximum number of requests allowed for a regular authenticated user
     * within the specified time window.
     *
     * @return maximum allowed requests
     */
    int maxRequests() default 30;

    /**
     * Duration of the rate-limit window in seconds for regular users.
     *
     * @return window size in seconds
     */
    long windowSeconds() default 60;

    /**
     * Maximum number of requests allowed when accessing the endpoint
     * using an API key.
     * <p> A value of {@code 0} indicates that no separate API-key-specific
     * limit is defined and the regular user limit should be used.</p>
     *
     * @return maximum allowed API-key requests
     */
    int apiKeyMaxRequests() default 0;

    /**
     * Duration of the rate-limit window in seconds for API-key access.
     *
     * @return API-key window size in seconds
     */
    long apiKeyWindowSeconds() default 60;
}
