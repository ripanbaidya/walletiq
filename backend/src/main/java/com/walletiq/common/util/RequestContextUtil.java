package com.walletiq.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for accessing HTTP request context information,
 * Used in exception handling to capture request metadata.
 */
public final class RequestContextUtil {
    private static final String X_REQUEST_ID = "X-Request-ID";

    private RequestContextUtil() {
    }

    /**
     * Gets the current HTTP servlet request
     */
    public static Optional<HttpServletRequest> getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        return Optional.ofNullable(attributes).map(ServletRequestAttributes::getRequest);
    }

    /**
     * Gets the current request path
     */
    public static String getCurrentRequestPath() {
        return getHttpServletRequest().map(HttpServletRequest::getRequestURI).orElse("unknown");
    }

    /**
     * Generate a unique trace ID for request tracking
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gets request ID from header or generates new one
     */
    public static String getOrGenerateRequestId() {
        return getHttpServletRequest()
                .map(request -> request.getHeader(X_REQUEST_ID))
                .filter(id -> id != null && !id.isBlank())
                .orElseGet(RequestContextUtil::generateTraceId);
    }
}
