package com.walletiq.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the permissions required to access an API endpoint.
 * <p>This annotation specifies one or more permission identifiers that
 * a caller must possess in order to invoke the annotated endpoint.</p>
 * <p>{@code @RequirePermission} is intended to express the API contract
 * and to support documentation, tooling, and validation. It does not
 * enforce authorization by itself.</p>
 * <p>Actual permission checks must be implemented separately using security
 * mechanisms such as Spring Security, method security, filters, or API gateways.
 * </p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * {@code
 * @RequirePermission({"USER_READ", "USER_WRITE"})
 * @GetMapping("/users/{id}")
 * public User getUser(...) { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * One or more permission identifiers required to access the endpoint.
     *
     * @return required permission names
     */
    String[] value();
}
