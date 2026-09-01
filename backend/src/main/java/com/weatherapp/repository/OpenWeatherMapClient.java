package com.weatherapp.repository;

import com.weatherapp.dto.OpenWeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Repository
public class OpenWeatherMapClient {

    private static final Logger log = LoggerFactory.getLogger(OpenWeatherMapClient.class);

    private final RestClient restClient;
    private final String apiKey;

    public OpenWeatherMapClient(
            RestClient.Builder restClientBuilder,
            @Value("${openweathermap.base-url}") String baseUrl,
            @Value("${openweathermap.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public OpenWeatherResponse fetchWeather(String cityId) {
        if (apiKey == null || apiKey.isBlank() || "paste_your_key_here".equals(apiKey)) {
            throw new IllegalStateException(
                    "Add your OpenWeatherMap key in application.properties as openweathermap.api-key"
            );
        }

        try {
            log.info("Calling OpenWeatherMap for city id {}", cityId);
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("id", cityId)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(OpenWeatherResponse.class);
        } catch (RestClientResponseException e) {
            throw new IllegalStateException(
                    "OpenWeatherMap error for city " + cityId + ": " + e.getResponseBodyAsString(),
                    e
            );
        }
    }
}
