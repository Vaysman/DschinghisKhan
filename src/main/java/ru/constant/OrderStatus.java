package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum  OrderStatus {
    //grey
    @JsonProperty("Создано")
    CREATED("Создано", "#cccccc"),

    //our brand new blue
    @JsonProperty("Назначено")
    ASSIGNED("Назначено", "#1a2b6d"),

    //
    @JsonProperty("Принято")
    ACCEPTED("Принято", ""),

    @JsonProperty("Подтверждено")
    CONFIRMED("Подтверждено", ""),

    @JsonProperty("Отказано")
    REJECTED("Отказано", ""),

    @JsonProperty("Изменена стоимость")
    PRICE_CHANGED("Изменена стоимость", ""),

    @JsonProperty("Прибыл на погрузку")
    LOADING("Прибыл на погрузку", ""),

    @JsonProperty("В пути")
    EN_ROUTE("В пути", ""),

    @JsonProperty("Опоздание на погрузку")
    LATE_LOAD("Опоздание на погрузку", ""),

    @JsonProperty("Опоздание на разгрузку")
    LATE_UNLOAD("Опоздание на разгрузку", ""),

    @JsonProperty("ДТП")
    ACCIDENT("ДТП", ""),

    @JsonProperty("Форс-мажор")
    SITUATION("Форс-мажор", ""),

    @JsonProperty("Простой на погрузке")
    STALE_LOAD("Простой на погрузке", ""),

    @JsonProperty("Простой на разгрузке")
    STALE_UNLOAD("Простой на разгрузке", ""),

    @JsonProperty("Доставлено")
    DELIVERED("Доставлено", ""),

    @JsonProperty("Претензия")
    PRETENSION("Претензия", ""),

    @JsonProperty("Претензия перевозчика")
    CMP_PRETENSION("Претензия перевозчика", ""),

    @JsonProperty("Подтверждение доставки")
    DELIVERY_CONFD("Подтверждение доставки", ""),

    @JsonProperty("Ожидает возврата документов")
    DOCUMENT_RETURN("Ожидает возврата документов", ""),

    @JsonProperty("Документы получены")
    DOCS_RECEIVED("Документы получены", ""),

    @JsonProperty("Ожидает оплаты")
    PAY_PENDING("Ожидает оплаты", ""),

    @JsonProperty("Оплачено")
    PAYED("Оплачено", ""),

    @JsonProperty("Подтверждение оплаты")
    PAYMENT_CONFD("Подтверждение оплаты", "green"),

    @JsonProperty("Не оплачено")
    NOT_PAYED("Не оплачено", ""),

    @JsonProperty("Удалена")
    DELETED("Удалена", "");


    @Getter
    private String statusName;


    //Dispatchers have a dashboard. There - they have charts and all.
    //This property decides what color they'll have on the chart.
    //Preferably - #RRGGBB
    @Getter
    private String chartColor;

    public static OrderStatus[] getChangeableStatuses(){
        return new OrderStatus[]{
                OrderStatus.CONFIRMED,
                OrderStatus.EN_ROUTE,
                OrderStatus.LATE_LOAD,
                OrderStatus.LATE_UNLOAD,
                OrderStatus.STALE_UNLOAD,
                OrderStatus.STALE_LOAD,
                OrderStatus.PRETENSION,
                OrderStatus.CMP_PRETENSION,
                OrderStatus.LOADING,
                OrderStatus.ACCIDENT,
                OrderStatus.SITUATION,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERY_CONFD
        };
    }

    public static OrderStatus[] getPreDispatchStatuses(){
        return new OrderStatus[]{
                OrderStatus.CREATED,
                OrderStatus.ASSIGNED,
                OrderStatus.ACCEPTED,
                OrderStatus.REJECTED
        };
    }

    public static OrderStatus[] getStatusesInWork(){
        return new OrderStatus[]{
                OrderStatus.CONFIRMED,
                OrderStatus.EN_ROUTE,
                OrderStatus.LATE_LOAD,
                OrderStatus.LATE_UNLOAD,
                OrderStatus.STALE_UNLOAD,
                OrderStatus.STALE_LOAD,
                OrderStatus.PRETENSION,
                OrderStatus.CMP_PRETENSION,
                OrderStatus.LOADING,
                OrderStatus.ACCIDENT,
                OrderStatus.SITUATION,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERY_CONFD,
                OrderStatus.DOCUMENT_RETURN,
                OrderStatus.DOCS_RECEIVED,
                OrderStatus.PAY_PENDING,
                OrderStatus.PAYED,
                OrderStatus.NOT_PAYED,
        };
    }

    public static OrderStatus[] getDeliveredStatuses(){
        return new OrderStatus[]{
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERY_CONFD,
                OrderStatus.DOCUMENT_RETURN,
                OrderStatus.DOCS_RECEIVED,
                OrderStatus.PAY_PENDING,
                OrderStatus.NOT_PAYED,
                OrderStatus.PAYED
        };
    }

    OrderStatus(String statusName, String chartColor) {
        this.statusName = statusName;
        this.chartColor = chartColor;
    }
}
