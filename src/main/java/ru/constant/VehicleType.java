package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VehicleType {
    @JsonProperty("Тягач")
    MOVER("Тягач"),
    @JsonProperty("Прицеп")
    CONTAINER("Прицеп"),
    @JsonProperty("ТС")
    TRANSPORT("ТС"),
    @JsonProperty("Полуприцеп")
    HALF_CONT("Полуприцеп");

    private String name;

    VehicleType(String name) {
        this.name = name;
    }
}
