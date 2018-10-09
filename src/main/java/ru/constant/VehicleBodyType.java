package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum VehicleBodyType {
    @JsonProperty("Термос")
    THERMOS("Термос"),
    @JsonProperty("Тент")
    TENT("Тент"),
    @JsonProperty("Рефрижератор")
    REFRIGERATOR("Рефрижератор"),
    @JsonProperty("Борт")
    BORT("Борт");

    @Getter
    private String typeName;

    VehicleBodyType(String typeName) {
        this.typeName = typeName;
    }
}
