package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum  OrderStatus {
    @JsonProperty("Создано")
    CREATED("Создано"),
    @JsonProperty("Назначено")
    ASSIGNED("Назначено"),
    @JsonProperty("Принято")
    ACCEPTED("Принято"),
    @JsonProperty("Отказано")
    REJECTED("Отказано"),
    @JsonProperty("Доставлено")
    FINISHED("Доставлено");


    private String statusName;

    OrderStatus(String statusName) {
        this.statusName = statusName;
    }
}
