package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum TaxationType {
    @JsonProperty("С НДС")
    NDS("С НДС"),
    @JsonProperty("Без НДС")
    NO_NDS("Без НДС");

    @Getter
    private String name;

    TaxationType(String name) {
        this.name = name;
    }
}
