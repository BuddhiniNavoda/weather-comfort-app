package com.weatherapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CitiesFile {

    @JsonProperty("List")
    private List<CityItem> list = new ArrayList<>();

    public List<CityItem> getList() {
        return list;
    }

    public void setList(List<CityItem> list) {
        this.list = list;
    }
}
