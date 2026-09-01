package com.weatherapp.dto;

public class CityWeather {

    private final long id;
    private final String name;
    private final String country;
    private final String description;
    private final Double temperature;
    private final Integer humidity;
    private final Double windSpeed;
    private final Integer clouds;
    private final String openWeatherUpdatedAt;
    private final double score;
    private int rank;

    public CityWeather(
            long id,
            String name,
            String country,
            String description,
            Double temperature,
            Integer humidity,
            Double windSpeed,
            Integer clouds,
            String openWeatherUpdatedAt,
            double score
    ) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description = description;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.clouds = clouds;
        this.openWeatherUpdatedAt = openWeatherUpdatedAt;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getDescription() {
        return description;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Integer getClouds() {
        return clouds;
    }

    public String getOpenWeatherUpdatedAt() {
        return openWeatherUpdatedAt;
    }

    public double getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
