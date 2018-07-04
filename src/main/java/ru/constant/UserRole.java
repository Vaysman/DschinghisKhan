package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum UserRole {
    @JsonProperty("Админ")
    ADMIN("Админ"),
    @JsonProperty("ТЭК")
    TRANSPORT_COMPANY("ТЭК"),
    @JsonProperty("Диспетчер")
    DISPATCHER("Диспетчер");

    @Getter
    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }
}
