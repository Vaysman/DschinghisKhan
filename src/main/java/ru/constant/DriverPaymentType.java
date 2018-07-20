package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DriverPaymentType {
    @JsonProperty("Наличными")
    CASH("Наличными"),

    @JsonProperty("Безналичный рассчет")
    NO_CASH("Безналичный рассчет");

    private String name;

    DriverPaymentType(String name) {
        this.name = name;
    }
}
