package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.OrderObligation;
import ru.constant.OrderPaymentType;
import ru.constant.OrderStatus;
import ru.dao.entity.converter.StringSetToStringConverter;
import ru.util.generator.RandomIntGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "orders",indexes = {
        @Index(name = "orders_number_uindex", columnList = "number", unique = true),
        @Index(name = "orders_transport_company_id_index", columnList = "transport_company_id"),
        @Index(name = "orders_driver_id_index", columnList = "driver_id"),
        @Index(name = "orders_transport_id_index", columnList = "transport_id"),
        @Index(name = "orders_route_id_index", columnList = "route_id"),
})
@EqualsAndHashCode(exclude = {"route","driver","transport","company","assignedCompanies","offers","files","additionalTransports","additionalDrivers"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String number = "LSS-"+RandomIntGenerator.randomAlphaNumeric(8).toString();

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private OrderStatus status = OrderStatus.CREATED;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROUTE_ID", referencedColumnName = "ID")
    private Route route;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSPORT_COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
    private Driver driver;



    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSPORT_ID", referencedColumnName = "ID")
    private Transport transport;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "additional_drivers_for_orders",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "driver_id") },
            indexes = {@Index(name = "additional_drivers_for_orders_order_id_index", columnList = "order_id")}
    )
    private Set<Driver> additionalDrivers;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "additional_transports_for_orders",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "transport_id") },
            indexes = {@Index(name = "additional_transports_for_orders_order_id_index", columnList = "order_id")}
    )
    private Set<Transport> additionalTransports;

    @JsonView(DataTablesOutput.View.class)
    @Column(name = "requirements")
    @Convert(converter = StringSetToStringConverter.class)
    private Set<String> requirements = new HashSet<>();

    @JsonView(DataTablesOutput.View.class)
    @Column(name = "cargo")
    @Convert(converter = StringSetToStringConverter.class)
    private Set<String> cargo = new HashSet<>();

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer paymentDate = 0;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer documentReturnDate = 0;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer rating = 0;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float routePrice;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float dispatcherPrice;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float proposedPrice;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private OrderObligation orderObligation = OrderObligation.NON_MANDATORY;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer originator;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private OrderPaymentType paymentType = OrderPaymentType.ORIGINAL;

    @Column
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonView(DataTablesOutput.View.class)
    private Date statusChangeDate = new Date();

    @Column
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    @JsonView(DataTablesOutput.View.class)
    private Date dispatchDate;


    @Column
    @JsonView(DataTablesOutput.View.class)
    private String cargoDescription ="";

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float cargoWeight;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float cargoVolume;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer numberOfPallets = 0;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float cargoHeight;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float cargoWidth;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float cargoLength;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Boolean afterLoad;


    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REFRESH})
    @JsonIgnore
    @JoinTable(
            name = "pending_orders",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "transport_company_id") },
            indexes = {@Index(name = "pending_orders_order_id_index", columnList = "order_id"),
                    @Index(name = "pending_orders_transport_company_id_index", columnList = "transport_company_id")}
    )
    private Set<Company> assignedCompanies = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<OrderOffer> offers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.ALL})
    @JoinTable(
            name = "files_to_orders",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") },
            indexes = {@Index(name = "files_to_orders_order_id_index", columnList = "order_id"),
                    @Index(name = "files_to_orders_file_id_index", columnList = "file_id")}
    )
    private Set<StoredFile> files;

    @PrePersist
    private void setPricing(){
        routePrice = route.getTotalCostNds();
        if(dispatcherPrice==null) dispatcherPrice=routePrice;
    }
}


