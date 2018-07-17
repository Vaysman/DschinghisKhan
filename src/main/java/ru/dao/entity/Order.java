package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.OrderObligation;
import ru.constant.OrderStatus;
import ru.dao.entity.converter.StringSetToStringConverter;
import ru.util.generator.RandomIntGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Entity
@Table(name = "orders",indexes = {
        @Index(name = "orders_number_uindex", columnList = "number", unique = true),
        @Index(name = "orders_transport_company_id_index", columnList = "transport_company_id"),
        @Index(name = "orders_driver_id_index", columnList = "driver_id"),
        @Index(name = "orders_transport_id_index", columnList = "transport_id"),
        @Index(name = "orders_route_id_index", columnList = "route_id"),
})
@EqualsAndHashCode(exclude = {"route","driver","transport","company","assignedCompanies"})
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "ROUTE_ID", referencedColumnName = "ID")
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "TRANSPORT_COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "DRIVER_ID", referencedColumnName = "ID")
    private Driver driver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "TRANSPORT_ID", referencedColumnName = "ID")
    private Transport transport;

    @JsonView(DataTablesOutput.View.class)
    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "drop_points",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "point_id") },
            indexes = {@Index(name = "drop_points_order_id_index", columnList = "order_id"),
                    @Index(name = "drop_points_point_id_index", columnList = "point_id")}
    )
    private Set<Point> dropPoints = new HashSet<>();


    @Column(name = "requirements")
    @Convert(converter = StringSetToStringConverter.class)
    @JsonView(DataTablesOutput.View.class)
    private Set<String> requirements = new HashSet<>();

    @Column(name = "cargo")
    @Convert(converter = StringSetToStringConverter.class)
    @JsonView(DataTablesOutput.View.class)
    private Set<String> cargo = new HashSet<>();

    @Column
    @JsonFormat(pattern = "d/M/yyyy")
    @JsonView(DataTablesOutput.View.class)
    private Date paymentDate;

    @Column
    @JsonFormat(pattern = "d/M/yyyy")
    @JsonView(DataTablesOutput.View.class)
    private Date documentReturnDate;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer rating;

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
    private OrderObligation orderObligation;

    @Column
    private Integer originator;




    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pending_orders",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "transport_company_id") },
            indexes = {@Index(name = "pending_orders_order_id_index", columnList = "order_id"),
                    @Index(name = "pending_orders_transport_company_id_index", columnList = "transport_company_id")}
    )
    private Set<Company> assignedCompanies = new HashSet<>();



    @PrePersist
    private void setPricing(){
        routePrice = route.getTotalCostNds();
        if(dispatcherPrice==null) dispatcherPrice=routePrice;
    }
}


