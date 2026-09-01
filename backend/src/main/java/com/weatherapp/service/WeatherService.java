package com.weatherapp.service;

import com.weatherapp.cache.WeatherCache;
import com.weatherapp.dto.CityWeather;
import com.weatherapp.dto.OpenWeatherResponse;
import com.weatherapp.dto.WeatherListResponse;
import com.weatherapp.repository.CityFileReader;
import com.weatherapp.repository.OpenWeatherMapClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final CityFileReader cityFileReader;
    private final OpenWeatherMapClient openWeatherMapClient;
    private final ComfortCalculator comfortCalculator;
    private final WeatherCache weatherCache;

    public WeatherService(
            CityFileReader cityFileReader,
            OpenWeatherMapClient openWeatherMapClient,
            ComfortCalculator comfortCalculator,
            WeatherCache weatherCache
    ) {
        this.cityFileReader = cityFileReader;
        this.openWeatherMapClient = openWeatherMapClient;
        this.comfortCalculator = comfortCalculator;
        this.weatherCache = weatherCache;
    }

    public WeatherListResponse getWeatherForCities() {
        var cachedList = weatherCache.getProcessed();
        if (cachedList.isPresent()) {
            return cachedList.get();
        }

        List<String> cityIds = cityFileReader.readCityIds();
        weatherCache.clearLastRawLookups(cityIds);
        List<CityWeather> cities = new ArrayList<>();

        for (String cityId : cityIds) {
            OpenWeatherResponse data = weatherCache.getRaw(cityId).orElseGet(() -> {
                OpenWeatherResponse fresh = openWeatherMapClient.fetchWeather(cityId);
                weatherCache.putRaw(cityId, fresh);
                return fresh;
            });
            cities.add(toCityWeather(data));
        }

        cities.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        for (int i = 0; i < cities.size(); i++) {
            cities.get(i).setRank(i + 1);
        }

        WeatherListResponse response = new WeatherListResponse(
                "openweathermap",
                Instant.now().toString(),
                cities.size(),
                cities
        );
        weatherCache.putProcessed(response);
        return response;
    }

    private CityWeather toCityWeather(OpenWeatherResponse data) {
        String description = null;
        if (data.getWeather() != null && !data.getWeather().isEmpty()) {
            description = data.getWeather().get(0).getDescription();
        }

        Double temperature = data.getMain() != null ? data.getMain().getTemp() : null;
        Integer humidity = data.getMain() != null ? data.getMain().getHumidity() : null;
        Double windSpeed = data.getWind() != null ? data.getWind().getSpeed() : null;
        Integer clouds = data.getClouds() != null ? data.getClouds().getAll() : null;
        String country = data.getSys() != null ? data.getSys().getCountry() : null;

        String openWeatherUpdatedAt = data.getDt() > 0
                ? Instant.ofEpochSecond(data.getDt()).toString()
                : null;

        double score = comfortCalculator.calculate(temperature, humidity, windSpeed);

        return new CityWeather(
                data.getId(),
                data.getName(),
                country,
                description,
                temperature,
                humidity,
                windSpeed,
                clouds,
                openWeatherUpdatedAt,
                score
        );
    }
}
