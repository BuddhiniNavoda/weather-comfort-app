package com.weatherapp.controller;

import com.weatherapp.service.CacheStatusService;
import com.weatherapp.service.WeatherService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WeatherController {

    private final WeatherService weatherService;
    private final CacheStatusService cacheStatusService;

    public WeatherController(WeatherService weatherService, CacheStatusService cacheStatusService) {
        this.weatherService = weatherService;
        this.cacheStatusService = cacheStatusService;
    }

    @GetMapping("/api/weather")
    public ResponseEntity<?> getWeather() {
        try {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(weatherService.getWeatherForCities());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/cache/status")
    public ResponseEntity<?> getCacheStatus() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(cacheStatusService.getStatus());
    }
}
