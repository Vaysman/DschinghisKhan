package ru.dto.json.order;

import lombok.Data;

import java.util.Set;

@Data
public class OrderAcceptData {
    private Integer companyId;
    private Integer driverId;
    private Integer transportId;
    private Float proposedPrice;
    private String proposedPriceComment;
    private Set<Integer> additionalDrivers;
    private Set<Integer> additionalTransports;
}
