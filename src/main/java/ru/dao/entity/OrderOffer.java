package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

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
@EqualsAndHashCode(exclude = {"order","company","transport","driver","managerCompany","additionalDrivers","additionalTransports"})
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


    @ManyToMany
    @JoinTable(
            name = "additional_drivers_for_offers",
            joinColumns = { @JoinColumn(name = "offer_id") },
            inverseJoinColumns = { @JoinColumn(name = "driver_id") },
            indexes = {@Index(name = "additional_drivers_for_offers_offer_id_index", columnList = "offer_id")}
    )
    private Set<Driver> additionalDrivers;

    @ManyToMany
    @JoinTable(
            name = "additional_transports_for_offers",
            joinColumns = { @JoinColumn(name = "offer_id") },
            inverseJoinColumns = { @JoinColumn(name = "transport_id") },
            indexes = {@Index(name = "additional_transports_for_offers_offer_id_index", columnList = "offer_id")}
    )
    private Set<Transport> additionalTransports;

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
