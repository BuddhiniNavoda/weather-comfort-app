package com.weatherapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth0")
public class Auth0AccessProperties {

    private String allowedEmails = "";

    public String getAllowedEmails() {
        return allowedEmails;
    }

    public void setAllowedEmails(String allowedEmails) {
        this.allowedEmails = allowedEmails == null ? "" : allowedEmails;
    }

    public boolean isRequireMfa() {
        return requireMfa;
    }

    public void setRequireMfa(boolean requireMfa) {
        this.requireMfa = requireMfa;
    }
}
