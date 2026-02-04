package com.walletiq.config.web;

import com.walletiq.config.properties.ApplicationAPIProperties;
import com.walletiq.config.properties.ApplicationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {
    private static final String BEARER_AUTH = "BearerAuth";
    private static final String API_KEY_AUTH = "ApiKeyAuth";

    private final ApplicationProperties applicationProperties;
    private final ApplicationAPIProperties apiProperties;

    @Bean
    @Profile("!prod")
    public OpenAPI nonProductionOpenAPI() {
        return createOpenAPI(getDevelopmentServers(apiProperties.servers().dev()), true);
    }

    @Bean
    @Profile("prod")
    public OpenAPI productionOpenAPI() {
        return createOpenAPI(getProductionServers(apiProperties.servers().prod()), false);
    }

    /**
     * Configuration for open api
     */
    private OpenAPI createOpenAPI(List<Server> servers, boolean isDevelopment) {
        String authNote = """
                **Authentication:**
                Authentication is performed using the Authorization HTTP header in the format:
                
                `Authorization: Bearer YOUR_JWT_TOKEN`
                
                For API key authentication:
                
                `Authorization: ApiKey YOUR_API_KEY`
                
                **Rate Limiting:**
                There is a rate limit of `30 requests per minute` across all endpoints, with some
                endpoints having stricter limits.
                Rate limit information is provided in response headers:
                - `x-ratelimit-limit`: Maximum requests allowed in the current period
                - `x-ratelimit-remaining`: Remaining requests in the current period
                - `x-ratelimit-reset`: Timestamp when the limit resets
                """;
        Info info = new Info()
                .title(applicationProperties.name())
                .version(applicationProperties.version())
                .description(
                        "Documentation for the endpoints provided by the " +
                                applicationProperties.name() + " API server.\n\n" +
                                "An RAG Based Wallet Management Platform" +
                                "\n\n" + authNote
                )
                .contact(new Contact()
                        .name(applicationProperties.name())
                        .email(applicationProperties.support().email())
                )
                .license(new License()
                        .name(applicationProperties.license().name())
                        .url(applicationProperties.license().url())
                );
        info.addExtension("x-logo", Map.of("url", applicationProperties.logo()));
        return new OpenAPI()
                .info(info)
                .servers(servers)
                .tags(createApiTags(isDevelopment))
                .components(createSecurityComponents())
                ;
    }

    /**
     * Creates security components with multiple authentication schemes
     */
    private Components createSecurityComponents() {
        return new Components()
                .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Bearer token authentication. Use for user-specific operations.")
                )
                .addSecuritySchemes(API_KEY_AUTH, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("ApiKey")
                        .description("API Key authentication. Provides access to certain API endpoints with different rate limits.")
                );
    }

    /**
     * Creates organized API tags for better documentation structure
     */
    private List<Tag> createApiTags(boolean isDevelopment) {
        List<Tag> tags = List.of(
                createTag("test", "Test", "Test endpoints.", true),
                createTag("users", "User Management", "User account data and profile management.", true),
                createTag("authentication", "Authentication", "User authentication and authorization endpoints.", true),
                createTag("admin", "Administration", "Administrative endpoints. Require admin permissions.", false),
                createTag("configuration", "Server Configuration", "Server configuration and settings.", true),
                createTag("public", "Public", "Public endpoints that don't require authentication.", true)
        );

        // Add development tag only for non-production
        if (isDevelopment) {
            return Stream
                    .of(createTag("Development", "Development", "Development-only endpoints. Not available in production.", false))
                    .collect(java.util.stream.Collectors.toCollection(() -> new ArrayList<>(tags)));
        }

        return tags;
    }

    /* ---------------- Helper ---------------- */

    private Tag createTag(String name, String displayName, String description, boolean isPublic) {
        Tag tag = new Tag().name(name).description(description);
        tag.addExtension("x-displayName", displayName);
        tag.addExtension("x-public", isPublic ? "yes" : "no");
        return tag;
    }

    private List<Server> getDevelopmentServers(List<String> urls) {
        return urls.stream()
                .map(url -> new Server().url(url).description("Development Environment"))
                .toList();
    }

    private List<Server> getProductionServers(List<String> urls) {
        return urls.stream()
                .map(url -> new Server().url(url).description("Production Environment"))
                .toList();
    }
}