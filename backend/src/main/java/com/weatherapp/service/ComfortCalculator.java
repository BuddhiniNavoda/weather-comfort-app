package com.weatherapp.service;

import org.springframework.stereotype.Component;

@Component
public class ComfortCalculator {

    private static final double IDEAL_TEMPERATURE = 22.0;
    private static final double TEMPERATURE_PENALTY = 5.0;

    private static final double IDEAL_HUMIDITY = 50.0;
    private static final double HUMIDITY_PENALTY = 1.5;

    private static final double WIND_PENALTY = 10.0;

    private static final double WEIGHT_TEMPERATURE = 0.50;
    private static final double WEIGHT_HUMIDITY = 0.30;
    private static final double WEIGHT_WIND = 0.20;

    public double calculate(Double temperature, Integer humidity, Double windSpeed) {
        double tempScore = temperatureScore(temperature);
        double humidityScore = humidityScore(humidity);
        double windScore = windScore(windSpeed);

        double comfort = WEIGHT_TEMPERATURE * tempScore
                + WEIGHT_HUMIDITY * humidityScore
                + WEIGHT_WIND * windScore;

        return roundTwoDecimals(clamp(comfort));
    }

    double temperatureScore(Double temperature) {
        if (temperature == null) {
            return 0;
        }
        return clamp(100 - Math.abs(temperature - IDEAL_TEMPERATURE) * TEMPERATURE_PENALTY);
    }

    double humidityScore(Integer humidity) {
        if (humidity == null) {
            return 0;
        }
        return clamp(100 - Math.abs(humidity - IDEAL_HUMIDITY) * HUMIDITY_PENALTY);
    }

    double windScore(Double windSpeed) {
        double speed = windSpeed == null ? 0 : windSpeed;
        return clamp(100 - speed * WIND_PENALTY);
    }

    private double clamp(double value) {
        return Math.min(100, Math.max(0, value));
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
