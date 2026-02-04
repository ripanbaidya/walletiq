package com.walletiq.config.web;

import com.walletiq.api.annotation.PublicEndpoint;
import com.walletiq.api.annotation.RateLimited;
import com.walletiq.api.annotation.RequireConfiguration;
import com.walletiq.api.annotation.RequirePermission;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Customizes OpenAPI operations with additional information
 */
@Component
public class EnhancedOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        // Add rate limit information if annotation is present
        if (method.isAnnotationPresent(RateLimited.class)) {
            addRateLimitHeaders(operation, method.getAnnotation(RateLimited.class));
            addRateLimitDescription(operation, method.getAnnotation(RateLimited.class));
        }
        // Add public/private indicator
        if (method.isAnnotationPresent(PublicEndpoint.class)) {
            operation.addExtension("x-public", "yes");
            // Remove security requirements for public endpoints
            operation.setSecurity(List.of());
        } else {
            operation.addExtension("x-public", "no");
        }

        // Add required permissions to description
        if (method.isAnnotationPresent(RequirePermission.class)) {
            addPermissionDescription(operation, method.getAnnotation(RequirePermission.class));
        }

        // Add required configuration info
        if (method.isAnnotationPresent(RequireConfiguration.class)) {
            addConfigurationDescription(operation, method.getAnnotation(RequireConfiguration.class));
        }

        // Ensure description ends with period
        String desc = operation.getDescription();
        if (desc != null && !desc.trim().endsWith(".")) {
            operation.setDescription(desc + ".");
        }

        return operation;
    }

    /**
     * Adds rate limit headers to successful responses
     */
    private void addRateLimitHeaders(Operation operation, RateLimited rateLimited) {
        ApiResponse okResponse = operation.getResponses().get("200");
        if (okResponse == null) {
            okResponse = new ApiResponse().description("Successful operation");
            operation.getResponses().addApiResponse("200", okResponse);
        }

        if (okResponse.getHeaders() == null) {
            okResponse.headers(Map.of());
        }

        okResponse.addHeaderObject("x-ratelimit-limit", new Header()
                .schema(new IntegerSchema())
                .description("The number of allowed requests in the current period"));
        okResponse.addHeaderObject("x-ratelimit-remaining", new Header()
                .schema(new IntegerSchema())
                .description("The number of remaining requests in the current period"));
        okResponse.addHeaderObject("x-ratelimit-reset", new Header()
                .schema(new IntegerSchema())
                .description("The timestamp of the start of the next period"));
    }

    /**
     * Adds rate limit information to operation description
     */
    private void addRateLimitDescription(Operation operation, RateLimited rateLimited) {
        StringBuilder desc = new StringBuilder(operation.getDescription() != null ?
                operation.getDescription() : "");
        desc.append("\n\n**Rate limit:** This operation can be called up to ")
                .append(rateLimited.maxRequests())
                .append(" times every ")
                .append(formatDuration(rateLimited.windowSeconds()))
                .append(" for regular users");

        if (rateLimited.apiKeyMaxRequests() > 0) {
            desc.append(" and up to ")
                    .append(rateLimited.apiKeyMaxRequests())
                    .append(" times every ")
                    .append(formatDuration(rateLimited.apiKeyWindowSeconds()))
                    .append(" with API Keys");
        }

        desc.append(".\n\n");
        operation.setDescription(desc.toString());
    }

    /**
     * Adds permission requirements to operation description
     */
    private void addPermissionDescription(Operation operation, RequirePermission requirePermission) {
        String[] permissions = requirePermission.value();
        if (permissions.length > 0) {
            StringBuilder desc = new StringBuilder(operation.getDescription() != null ?
                    operation.getDescription() : "");
            desc.append("\n\n**Required permissions:** ")
                    .append(String.join(", ", permissions))
                    .append("\n\n");
            operation.setDescription(desc.toString());
        }
    }

    /**
     * Adds configuration requirements to operation description
     */
    private void addConfigurationDescription(Operation operation, RequireConfiguration config) {
        StringBuilder desc = new StringBuilder(operation.getDescription() != null ?
                operation.getDescription() : "");
        desc.append("\n\n**Required configuration:** This operation can only be called if the ")
                .append("configuration for `")
                .append(config.path())
                .append("` is `true`.\n\n");
        operation.setDescription(desc.toString());
    }

    /**
     * Formats duration in seconds to human-readable format
     */
    private String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            long hours = seconds / 3600;
            long remainingMinutes = (seconds % 3600) / 60;
            String result = hours + " hour" + (hours != 1 ? "s" : "");
            if (remainingMinutes > 0) {
                result += " " + remainingMinutes + " minute" + (remainingMinutes != 1 ? "s" : "");
            }
            return result;
        }
    }
}
