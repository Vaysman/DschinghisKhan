package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VehicleType {
    @JsonProperty("Термос")
    THERMOS("Термос"),
    @JsonProperty("Тент")
    TENT("Тент"),
    @JsonProperty("Рефрижератор")
    REFRIGERATOR("Рефрижератор"),
    @JsonProperty("Борт")
    BORT("Борт");

    private String typeName;

    VehicleType(String typeName) {
        this.typeName = typeName;
    }
}
