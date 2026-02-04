package com.walletiq.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an API endpoint as publicly accessible.
 * <p>Endpoints annotated with {@code @PublicEndpoint} do not require authentication
 * and can be accessed without providing credentials.</p>
 * <p>This annotation serves as a declarative marker for API contracts and documentation
 * (e.g. OpenAPI / Swagger). It does not enforce authentication behavior by itself.</p>
 * <p>Actual access control must be implemented separately using security mechanisms
 * such as Spring Security filters, interceptors, or API gateways.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * {@code
 * @PublicEndpoint
 * @GetMapping("/health")
 * public HealthStatus health() { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {
}
