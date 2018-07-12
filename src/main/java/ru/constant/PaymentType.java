package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {
    @JsonProperty("Наличными")
    CASH("Наличными"),
    @JsonProperty("Безналичный рассчет")
    NO_CASH("Безналичный рассчет");

    private String name;

    PaymentType(String name) {
        this.name = name;
    }
}
