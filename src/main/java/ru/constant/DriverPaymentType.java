package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum DriverPaymentType {
    @JsonProperty("Наличными")
    CASH("Наличными"),

    @JsonProperty("Безналичный рассчет")
    NO_CASH("Безналичный рассчет");

    @Getter
    private String name;

    DriverPaymentType(String name) {
        this.name = name;
    }
}
