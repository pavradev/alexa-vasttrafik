package com.github.pavradev.alexaskills.vasttrafik.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartureBoard {
    @JsonProperty("Departure")
    List<Departure> departureList;

    public List<Departure> getDepartureList() {
        return departureList;
    }

    public void setDepartureList(List<Departure> departureList) {
        this.departureList = departureList;
    }

    @Override
    public String toString() {
        return "DepartureBoard{" +
                "departureList=" + departureList +
                '}';
    }
}
