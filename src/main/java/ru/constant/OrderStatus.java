package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum  OrderStatus {
    @JsonProperty("Создано")
    CREATED("Создано"),

    @JsonProperty("Назначено")
    ASSIGNED("Назначено"),

    @JsonProperty("Принято")
    ACCEPTED("Принято"),

    @JsonProperty("Подтверждено")
    CONFIRMED("Подтверждено"),

    @JsonProperty("Отказано")
    REJECTED("Отказано"),

    @JsonProperty("Доставлено")
    FINISHED("Доставлено"),

    @JsonProperty("Изменена стоимость")
    PRICE_CHANGED("Изменена стоимость"),

    @JsonProperty("Прибыл на погрузку")
    LOADING("Прибыл на погрузку"),

    @JsonProperty("В пути")
    EN_ROUTE("В пути"),

    @JsonProperty("Опоздание на погрузку")
    LATE_LOAD("Опоздание на погрузку"),

    @JsonProperty("Опоздание на разгрузку")
    LATE_UNLOAD("Опоздание на разгрузку"),

    @JsonProperty("ДТП")
    ACCIDENT("ДТП"),

    @JsonProperty("Форс-мажор")
    SITUATION("Форс-мажор"),

    @JsonProperty("Простой на погрузке")
    STALE_LOAD("Простой на погрузке"),

    @JsonProperty("Простой на разгрузке")
    STALE_UNLOAD("Простой на разгрузке"),

    @JsonProperty("Доставлено")
    DELIVERED("Доставлено"),

    @JsonProperty("Претензия")
    PRETENSION("Претензия"),

    @JsonProperty("Претензия перевозчика")
    CMP_PRETENSION("Претензия перевозчика"),

    @JsonProperty("Подтверждение доставки")
    DELIVERY_CONFD("Подтверждение доставки"),

    @JsonProperty("Ожидает возврата документов")
    DOCUMENT_RETURN("Ожидает возврата документов"),

    @JsonProperty("Документы получены")
    GOT_DOCUMENTS("Документы получены"),

    @JsonProperty("Ожидает оплаты")
    PAY_PENDING("Ожидает оплаты"),

    @JsonProperty("Оплачено")
    PAYED("Оплачено"),

    @JsonProperty("Подтверждение оплаты")
    PAYMENT_CONFD("Подтверждение оплаты"),

    @JsonProperty("Не оплачено")
    NOT_PAYED("Не оплачено");


    private String statusName;

    public String getStatusName() {
        return statusName;
    }

    OrderStatus(String statusName) {
        this.statusName = statusName;
    }
}
