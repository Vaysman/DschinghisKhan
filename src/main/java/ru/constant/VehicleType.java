package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VehicleType {
    @JsonProperty("Тягач")
    MOVER("Тягач"),
    @JsonProperty("Полуприцеп/Прицеп")
    CONTAINER("Полуприцеп/Прицеп"),
    @JsonProperty("Транспорт")
    TRANSPORT("Транспорт");

    private String name;

    VehicleType(String name) {
        this.name = name;
    }
}
