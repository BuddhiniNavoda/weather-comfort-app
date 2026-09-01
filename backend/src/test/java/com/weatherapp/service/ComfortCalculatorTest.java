package com.weatherapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComfortCalculatorTest {

    private final ComfortCalculator calculator = new ComfortCalculator();

    @Test
    void idealWeatherIsNearOneHundred() {
        double score = calculator.calculate(22.0, 50, 0.0);
        assertEquals(100.0, score);
    }

    @Test
    void hotHumidWindyCityScoresLowerThanIdeal() {
        double colombo = calculator.calculate(30.58, 74, 5.84);
        assertTrue(colombo < 70);
        assertTrue(colombo > 40);
    }

    @Test
    void missingTemperatureGivesZeroTempPart() {
        double score = calculator.calculate(null, 50, 0.0);
        assertEquals(50.0, score);
    }

    @Test
    void scoreStaysBetweenZeroAndOneHundred() {
        double score = calculator.calculate(50.0, 100, 20.0);
        assertEquals(0.0, score);
    }
}
