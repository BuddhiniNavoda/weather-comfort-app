package com.weatherapp.config;

import org.junit.jupiter.api.Test;

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
}
