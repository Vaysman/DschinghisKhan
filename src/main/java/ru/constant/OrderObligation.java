package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderObligation {
    @JsonProperty("Обязательная")
    MANDATORY("Обязательная"),
    @JsonProperty("Необязательная")
    NON_MANDATORY("Необязательная");

    private String obligationName;

    public String getObligationName() {
        return obligationName;
    }

    OrderObligation(String obligationName) {
        this.obligationName = obligationName;
    }
}
