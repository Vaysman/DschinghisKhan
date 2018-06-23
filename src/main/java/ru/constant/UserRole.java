package ru.constant;

import lombok.Getter;

public enum UserRole {
    ADMIN("Админ"),
    TRANSPORT_COMPANY("ТЭК");

    @Getter
    private String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }
}
