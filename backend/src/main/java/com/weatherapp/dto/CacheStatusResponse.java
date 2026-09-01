package com.weatherapp.dto;

import java.util.List;
import java.util.Map;

public class CacheStatusResponse {

    private final long ttlSeconds;
    private final Map<String, Object> lastWeatherRequest;
    private final CacheItemStatus processed;
    private final List<RawCityCacheStatus> raw;

    public CacheStatusResponse(
            long ttlSeconds,
            Map<String, Object> lastWeatherRequest,
            CacheItemStatus processed,
            List<RawCityCacheStatus> raw
    ) {
        this.ttlSeconds = ttlSeconds;
        this.lastWeatherRequest = lastWeatherRequest;
        this.processed = processed;
        this.raw = raw;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public Map<String, Object> getLastWeatherRequest() {
        return lastWeatherRequest;
    }

    public CacheItemStatus getProcessed() {
        return processed;
    }

    public List<RawCityCacheStatus> getRaw() {
        return raw;
    }

    public static class RawCityCacheStatus {

        private final String cityId;
        private final String status;
        private final Long ageSeconds;
        private final Long ttlSecondsLeft;

        public RawCityCacheStatus(String cityId, String status, Long ageSeconds, Long ttlSecondsLeft) {
            this.cityId = cityId;
            this.status = status;
            this.ageSeconds = ageSeconds;
            this.ttlSecondsLeft = ttlSecondsLeft;
        }

        public String getCityId() {
            return cityId;
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
}
