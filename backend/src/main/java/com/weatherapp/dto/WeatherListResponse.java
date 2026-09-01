package com.weatherapp.dto;

import java.util.List;

public class WeatherListResponse {

    private final String source;
    private final String fetchedAt;
    private final int count;
    private final List<CityWeather> cities;

    public WeatherListResponse(String source, String fetchedAt, int count, List<CityWeather> cities) {
        this.source = source;
        this.fetchedAt = fetchedAt;
        this.count = count;
        this.cities = cities;
    }

    public String getSource() {
        return source;
    }

    public String getFetchedAt() {
        return fetchedAt;
    }

    public int getCount() {
        return count;
    }

    public List<CityWeather> getCities() {
        return cities;
    }
}
