package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum UserRole {
    @JsonProperty("Админ")
    ROLE_ADMIN("Админ",OrderStatus.values()),
    @JsonProperty("ТЭК")
    ROLE_TRANSPORT_COMPANY("ТЭК",new OrderStatus[]{
            OrderStatus.EN_ROUTE,
            OrderStatus.ACCIDENT,
            OrderStatus.LOADING,
            OrderStatus.LATE_LOAD,
            OrderStatus.LATE_UNLOAD,
            OrderStatus.STALE_LOAD,
            OrderStatus.STALE_UNLOAD,
            OrderStatus.CMP_PRETENSION,
            OrderStatus.SITUATION
    }),
    @JsonProperty("Диспетчер")
    ROLE_DISPATCHER("Диспетчер",new OrderStatus[]{
            OrderStatus.EN_ROUTE,
            OrderStatus.ACCIDENT,
            OrderStatus.LOADING,
            OrderStatus.LATE_LOAD,
            OrderStatus.LATE_UNLOAD,
            OrderStatus.PRETENSION,
            OrderStatus.SITUATION
    });

    @Getter
    private final String roleName;

    @Getter
    private final OrderStatus[] orderStatuses;

    UserRole(String roleName, OrderStatus orderStatuses[]) {
        this.roleName = roleName;
        this.orderStatuses=orderStatuses;
    }
}
