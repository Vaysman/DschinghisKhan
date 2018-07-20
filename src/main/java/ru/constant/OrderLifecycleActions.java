package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderLifecycleActions {

    @JsonProperty("Назначил ТК")
    ASSIGN("Назначил ТК"),
    @JsonProperty("Отклонил заявку")
    REJECT_ORDER("Отклонил заявку"),
    @JsonProperty("Отклонил предложение перевозчика")
    DECLINE_OFFER("Отклонил предложение перевозчика"),
    @JsonProperty("Принял заявку")
    ACCEPTED("Принял заявку"),
    @JsonProperty("Утвердил заявку")
    CONFIRMED("Утвердил заявку"),
    @JsonProperty("Изменил статус")
    CHANGE_STATUS("Изменил статус"),
    @JsonProperty("Подтвердил доставку")
    CONFIRM_DELIVERY("Подтвердил доставку"),
    @JsonProperty("Система изменила статус")
    STATUS_TRANSIT("Система изменила статус"),
    @JsonProperty("Подтвердил получение документов")
    DOCUMENT_DELIVERED("Подтвердил получение документов"),
    @JsonProperty("Заявил о оплате заявки")
    PAYMENT_CONFIRMED("Заявил о оплате заявки"),
    @JsonProperty("Заявил о неоплате заявки")
    PAYMENT_REJECTED("Заявил о неоплате заявки"),
    @JsonProperty("Заявил о получении оплаты по заявке")
    PAYMENT_RECEIVED("Заявил о получении оплаты по заявке");


    private String actionName;

    OrderLifecycleActions(String actionName) {
        this.actionName = actionName;
    }
}
