package com.weatherapp.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.entity.CitiesFile;
import com.weatherapp.entity.CityItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Repository
public class CityFileReader {

    private final ObjectMapper objectMapper;
    private final String citiesFileName;

    public CityFileReader(
            ObjectMapper objectMapper,
            @Value("${cities.file}") String citiesFileName
    ) {
        this.objectMapper = objectMapper;
        this.citiesFileName = citiesFileName;
    }

    public List<String> readCityIds() {
        CitiesFile citiesFile = readFile();
        return citiesFile.getList().stream()
                .map(CityItem::getCityCode)
                .toList();
    }

    private CitiesFile readFile() {
        try (InputStream input = openCitiesFile()) {
            return objectMapper.readValue(input, CitiesFile.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + citiesFileName, e);
        }
    }

    private InputStream openCitiesFile() throws IOException {
        Path onDisk = Path.of(citiesFileName);
        if (Files.exists(onDisk)) {
            return Files.newInputStream(onDisk);
        }
        return new ClassPathResource(citiesFileName).getInputStream();
    }
}
