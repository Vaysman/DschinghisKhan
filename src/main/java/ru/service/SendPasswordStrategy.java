package ru.service;

import lombok.Getter;

@Getter
public enum SendPasswordStrategy {
    NEW_USER("Регистрационные данные","Зарегистрирован пользователь для транспортой компании "),
    FORGOT_PASSWORD("Восстановление пароля","Новые данные для входа в систему под компанией ");
    private String header;
    private String text;

    SendPasswordStrategy(String header, String text) {
        this.header = header;
        this.text = text;
    }
}
