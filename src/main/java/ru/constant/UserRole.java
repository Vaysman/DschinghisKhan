package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum UserRole {
    @JsonProperty("Админ")
    ROLE_ADMIN("Админ"),
    @JsonProperty("ТЭК")
    ROLE_TRANSPORT_COMPANY("ТЭК"),
    @JsonProperty("Диспетчер")
    ROLE_DISPATCHER("Диспетчер");

    @Getter
    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }
}
