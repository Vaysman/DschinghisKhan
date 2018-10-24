package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum LoadingType {
    @JsonProperty("Боковая")
    SIDE("Боковая"),
    @JsonProperty("Верхняя")
    UPPER("Верхняя"),
    @JsonProperty("Задняя")
    BACK("Задняя");

    @Getter
    private String typeName;

    LoadingType(String typeName) {
        this.typeName = typeName;
    }
}
