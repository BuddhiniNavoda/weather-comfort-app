package com.weatherapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllowedAccessServiceTest {

    @Test
    void onlyListedEmailsAreAllowed() {
        Auth0AccessProperties properties = new Auth0AccessProperties();
        properties.setAllowedEmails("careers@fidenz.com,  buddhinikaluwila1999@gmail.com");
        AllowedAccessService service = new AllowedAccessService(properties);

        assertTrue(service.emailAllowed("Careers@Fidenz.com"));
        assertFalse(service.emailAllowed("random@gmail.com"));
        assertTrue(service.emailAllowed(""));
    }

    @Test
    void mfaIsAcceptedFromAmrClaim() {
        Auth0AccessProperties properties = new Auth0AccessProperties();
        AllowedAccessService service = new AllowedAccessService(properties);
        Jwt jwt = jwtWithClaims(Map.of("amr", List.of("pwd", "mfa")));

        assertTrue(service.mfaCompleted(jwt));
    }

    @Test
    void mfaIsAcceptedFromCustomClaim() {
        Auth0AccessProperties properties = new Auth0AccessProperties();
        AllowedAccessService service = new AllowedAccessService(properties);
        Jwt jwt = jwtWithClaims(Map.of("https://weather-api/mfa", true));

        assertTrue(service.mfaCompleted(jwt));
        assertTrue(service.mfaCompleted(jwtWithClaims(Map.of("email", "a@b.com"))));
        assertFalse(service.mfaCompleted(jwtWithClaims(Map.of("amr", List.of("pwd")))));
    }

    private static Jwt jwtWithClaims(Map<String, Object> claims) {
        Map<String, Object> all = new HashMap<>();
        all.put("sub", "user");
        all.putAll(claims);
        return new Jwt(
                "token",
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T01:00:00Z"),
                Map.of("alg", "none"),
                all
        );
    }
}

