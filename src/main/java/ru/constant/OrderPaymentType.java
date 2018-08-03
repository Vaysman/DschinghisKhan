package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum OrderPaymentType {
    @JsonProperty("По оригиналу")
    ORIGINAL("По оригиналу"),

    @JsonProperty("Предоплата")
    PRE_PAYMENT("Предоплата"),
    @JsonProperty("Оплата после выгрузки")
    AFTER_PAYMENT("Оплата после выгрузки"),

    @JsonProperty("Оплата по факту погрузки")
    FACT_PAYMENT("Оплата по факту погрузки"),

    @JsonProperty("По сканам")
    COPY("По сканам");

    private String documentTypeName;

    OrderPaymentType(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }
}
