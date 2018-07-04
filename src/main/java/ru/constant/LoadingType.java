package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LoadingType {
    @JsonProperty("Боковая")
    SIDE("Боковая"),
    @JsonProperty("Верхняя")
    UPPER("Верхняя"),
    @JsonProperty("Задняя")
    BACK("Задняя");

    private String typeName;

    LoadingType(String typeName) {
        this.typeName = typeName;
    }
}
