package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum VehicleType {
    @JsonProperty("Тягач")
    MOVER("Тягач"),
    @JsonProperty("Прицеп")
    CONTAINER("Прицеп"),
    @JsonProperty("ТС")
    TRANSPORT("ТС"),
    @JsonProperty("Полуприцеп")
    HALF_CONT("Полуприцеп");

    @Getter
    private String name;

    VehicleType(String name) {
        this.name = name;
    }
}
