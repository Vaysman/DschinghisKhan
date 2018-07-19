package ru.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pending_orders", indexes = {
    @Index(name = "pending_orders_order_id_index", columnList = "order_id"),
    @Index(name = "pending_orders_transport_company_id", columnList = "transport_company_id")
})
@EqualsAndHashCode(exclude = {"order","company"})
public class PendingOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSPORT_COMPANY_ID")
    private Company company;

    @Column
    private Float proposedPrice;
}


