package com.weatherapp.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AllowedAccessService {

    public static final String EMAIL_CLAIM = "https://weather-api/email";

    private final Auth0AccessProperties properties;

    public AllowedAccessService(Auth0AccessProperties properties) {
        this.properties = properties;
    }

    public Set<String> allowedEmails() {
        return Arrays.stream(properties.getAllowedEmails().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    public String emailFrom(Jwt jwt) {
        String custom = jwt.getClaimAsString(EMAIL_CLAIM);
        if (custom != null && !custom.isBlank()) {
            return custom.trim().toLowerCase(Locale.ROOT);
        }
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email.trim().toLowerCase(Locale.ROOT);
        }
        return "";
    }

    public boolean emailAllowed(String email) {
        Set<String> allowed = allowedEmails();
        if (allowed.isEmpty()) {
            return true;
        }
        if (email == null || email.isBlank()) {
            // Auth0 access tokens do not include email unless a Login Action adds it.
            // Login was already gated by Auth0, so missing email is not a deny.
            return true;
        }
        return allowed.contains(email.trim().toLowerCase(Locale.ROOT));
    }
}
