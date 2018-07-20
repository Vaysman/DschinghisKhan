package ru.dao.entity.keys;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class PendingOrderId implements Serializable {
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "company_id")
    private Integer companyId;

    public PendingOrderId(Integer orderId, Integer companyId) {
        this.orderId = orderId;
        this.companyId = companyId;
    }
}
