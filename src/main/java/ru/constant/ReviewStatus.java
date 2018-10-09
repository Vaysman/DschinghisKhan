package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReviewStatus {

    @JsonProperty("Ожидает ответа перевозчиков")
    CREATED("Ожидает ответа перевозчиков"),
    @JsonProperty("Есть ответ от перевозчиков")
    RESPONDED("Есть ответ от перевозчиков"),
    @JsonProperty("Все перевозчики ответили")
    COMPLETE("Все перевозчики ответили");

    String description;

    ReviewStatus(String description) {
        this.description = description;
    }
}
