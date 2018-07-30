package ru.dto.json.order;

import lombok.Data;

@Data
public class OrderAcceptData {
    private Integer companyId;
    private Integer driverId;
    private Integer transportId;
    private Float proposedPrice;
    private String proposedPriceComment;
}
