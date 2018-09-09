package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_offers",indexes = {
        @Index(name = "order_offers_id_index", columnList = "id"),
        @Index(name = "order_offers_manager_company_id_index", columnList = "manager_company_id"),
        @Index(name="order_offers_order_id_company_id_index", columnList = "order_id, company_id")
})
@EqualsAndHashCode(exclude = {"order","company","transport","driver","managerCompany"})
public class OrderOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "TRANSPORT_ID")
    private Transport transport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "DRIVER_ID")
    private Driver driver;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float proposedPrice;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float dispatcherPrice;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String proposedPriceComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_COMPANY_ID")
    private Company managerCompany;

    @Column
    private LocalDateTime offerDatetime;


}
