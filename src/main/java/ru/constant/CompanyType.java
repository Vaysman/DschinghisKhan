package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CompanyType {
    @JsonProperty("Транспортная компания")
    TRANSPORT("Транспортная компания"),
    @JsonProperty("Клиент")
    RECEIVING("Клиент"),
    @JsonProperty("Диспетчерская компания")
    DISPATCHER("Диспетчерская компания");;

    private String typeName;

    CompanyType(String typeName) {
        this.typeName = typeName;
    }
}
