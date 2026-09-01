package com.weatherapp.cache;

import com.weatherapp.dto.OpenWeatherResponse;
import com.weatherapp.dto.WeatherListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherCache {

    private final long ttlSeconds;

    private final ConcurrentHashMap<String, CacheEntry<OpenWeatherResponse>> rawByCityId = new ConcurrentHashMap<>();
    private volatile CacheEntry<WeatherListResponse> processed;

    private volatile String lastProcessedLookup = "MISS";
    private final ConcurrentHashMap<String, String> lastRawLookups = new ConcurrentHashMap<>();

    public WeatherCache(@Value("${cache.ttl-seconds:300}") long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public Optional<WeatherListResponse> getProcessed() {
        CacheEntry<WeatherListResponse> entry = processed;
        if (isFresh(entry)) {
            lastProcessedLookup = "HIT";
            return Optional.of(entry.getData());
        }
        lastProcessedLookup = "MISS";
        return Optional.empty();
    }

    public void putProcessed(WeatherListResponse data) {
        processed = new CacheEntry<>(data, Instant.now());
    }

    public Optional<OpenWeatherResponse> getRaw(String cityId) {
        CacheEntry<OpenWeatherResponse> entry = rawByCityId.get(cityId);
        if (isFresh(entry)) {
            lastRawLookups.put(cityId, "HIT");
            return Optional.of(entry.getData());
        }
        lastRawLookups.put(cityId, "MISS");
        return Optional.empty();
    }

    public void putRaw(String cityId, OpenWeatherResponse data) {
        rawByCityId.put(cityId, new CacheEntry<>(data, Instant.now()));
    }

    public String getLastProcessedLookup() {
        return lastProcessedLookup;
    }

    public Map<String, String> getLastRawLookups() {
        return new LinkedHashMap<>(lastRawLookups);
    }

    public Optional<CacheEntry<WeatherListResponse>> peekProcessed() {
        return Optional.ofNullable(processed);
    }

    public Optional<CacheEntry<OpenWeatherResponse>> peekRaw(String cityId) {
        return Optional.ofNullable(rawByCityId.get(cityId));
    }

    public boolean isFresh(CacheEntry<?> entry) {
        if (entry == null) {
            return false;
        }
        return ageSeconds(entry) < ttlSeconds;
    }

    public long ageSeconds(CacheEntry<?> entry) {
        return Duration.between(entry.getSavedAt(), Instant.now()).getSeconds();
    }

    public void clearLastRawLookups(List<String> cityIds) {
        lastRawLookups.clear();
        for (String cityId : cityIds) {
            lastRawLookups.put(cityId, "MISS");
        }
    }
}
