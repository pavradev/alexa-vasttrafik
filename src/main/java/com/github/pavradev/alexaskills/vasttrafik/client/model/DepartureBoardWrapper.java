package com.github.pavradev.alexaskills.vasttrafik.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartureBoardWrapper {
    @JsonProperty("DepartureBoard")
    private DepartureBoard departureBoard;

    public DepartureBoard getDepartureBoard() {
        return departureBoard;
    }

    public void setDepartureBoard(DepartureBoard departureBoard) {
        this.departureBoard = departureBoard;
    }

    @Override
    public String toString() {
        return "DepartureBoardWrapper{" +
                "departureBoard=" + departureBoard +
                '}';
    }
}
