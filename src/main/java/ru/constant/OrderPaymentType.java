package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum OrderPaymentType {
    @JsonProperty("По оригиналу")
    ORIGINAL("По оригиналу"),

    @JsonProperty("По сканам")
    COPY("По сканам");

    private String documentTypeName;

    OrderPaymentType(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }
}
