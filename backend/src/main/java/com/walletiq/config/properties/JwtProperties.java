package com.walletiq.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String issuer,
        String header,
        String prefix,
        AccessTokenProperties accessToken,
        RefreshTokenProperties refreshToken,
        PasswordProperties password,
        RSAProperties rsa
) {
    public record AccessTokenProperties(
            long expiration
    ) {
    }

    public record RefreshTokenProperties(
            long expiration
    ) {
    }

    public record PasswordProperties(
            int encoderStrength,
            int maxLoginAttempts,
            long lockoutDuration
    ) {
    }

    public record RSAProperties(
            String algorithm,
            int keySize,
            String privateKeyPath,
            String publicKeyPath
    ) {
    }
}
