package com.weatherapp.service;

import com.weatherapp.cache.CacheEntry;
import com.weatherapp.cache.WeatherCache;
import com.weatherapp.dto.CacheItemStatus;
import com.weatherapp.dto.CacheStatusResponse;
import com.weatherapp.dto.CacheStatusResponse.RawCityCacheStatus;
import com.weatherapp.repository.CityFileReader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheStatusService {

    private final WeatherCache weatherCache;
    private final CityFileReader cityFileReader;

    public CacheStatusService(WeatherCache weatherCache, CityFileReader cityFileReader) {
        this.weatherCache = weatherCache;
        this.cityFileReader = cityFileReader;
    }

    public CacheStatusResponse getStatus() {
        long ttl = weatherCache.getTtlSeconds();

        Map<String, Object> lastWeatherRequest = new LinkedHashMap<>();
        lastWeatherRequest.put("processed", weatherCache.getLastProcessedLookup());
        lastWeatherRequest.put("raw", weatherCache.getLastRawLookups());

        CacheItemStatus processed = toItemStatus(weatherCache.peekProcessed().orElse(null), ttl);

        List<RawCityCacheStatus> raw = new ArrayList<>();
        for (String cityId : cityFileReader.readCityIds()) {
            CacheEntry<?> entry = weatherCache.peekRaw(cityId).orElse(null);
            CacheItemStatus item = toItemStatus(entry, ttl);
            raw.add(new RawCityCacheStatus(
                    cityId,
                    item.getStatus(),
                    item.getAgeSeconds(),
                    item.getTtlSecondsLeft()
            ));
        }

        return new CacheStatusResponse(ttl, lastWeatherRequest, processed, raw);
    }

    private CacheItemStatus toItemStatus(CacheEntry<?> entry, long ttl) {
        if (entry == null) {
            return new CacheItemStatus("MISS", null, null);
        }
        long age = weatherCache.ageSeconds(entry);
        if (weatherCache.isFresh(entry)) {
            return new CacheItemStatus("HIT", age, Math.max(0, ttl - age));
        }
        return new CacheItemStatus("MISS", age, 0L);
    }
}
