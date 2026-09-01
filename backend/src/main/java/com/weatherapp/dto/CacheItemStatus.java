package com.weatherapp.dto;

public class CacheItemStatus {

    private final String status;
    private final Long ageSeconds;
    private final Long ttlSecondsLeft;

    public CacheItemStatus(String status, Long ageSeconds, Long ttlSecondsLeft) {
        this.status = status;
        this.ageSeconds = ageSeconds;
        this.ttlSecondsLeft = ttlSecondsLeft;
    }

    public String getStatus() {
        return status;
    }

    public Long getAgeSeconds() {
        return ageSeconds;
    }

    public Long getTtlSecondsLeft() {
        return ttlSecondsLeft;
    }
}
