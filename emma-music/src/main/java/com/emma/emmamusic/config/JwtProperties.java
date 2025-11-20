package com.emma.emmamusic.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.security.jwt")
public record JwtProperties(
        String secretKey,
        long expiration
) {
}