package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.OrderObligation;
import ru.constant.OrderStatus;
import ru.dao.entity.converter.StringArrayToStringConverter;
import ru.util.generator.RandomIntGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Entity
@Table(name = "orders",indexes = {
        @Index(name = "orders_drop_point_id_index", columnList = "drop_point_id"),
        @Index(name = "orders_number_uindex", columnList = "number", unique = true),
        @Index(name = "orders_pickup_point_id_index", columnList = "pickup_point_id"),
        @Index(name = "orders_drop_point_id_index", columnList = "drop_point_id", unique = true),
        @Index(name = "orders_transport_company_id_index", columnList = "transport_company_id"),
        @Index(name = "orders_route_id_index", columnList = "route_id"),
})
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
    @JoinColumn(name = "PICKUP_POINT_ID", referencedColumnName = "ID")
    private Point pickupPoint;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "DROP_POINT_ID", referencedColumnName = "ID")
    private Point dropPoint;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "drop_points",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "point_id") },
            indexes = {@Index(name = "drop_points_order_id_index", columnList = "order_id"),
                    @Index(name = "drop_points_point_id_index", columnList = "point_id")}
    )
    private List<Point> dropPoints = new ArrayList<>();


    @Column(name = "requirements")
    @Convert(converter = StringArrayToStringConverter.class)
    @JsonView(DataTablesOutput.View.class)
    private List<String> requirements = new ArrayList<>();

    @Column(name = "cargo")
    @Convert(converter = StringArrayToStringConverter.class)
    @JsonView(DataTablesOutput.View.class)
    private List<String> cargo = new ArrayList<>();

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
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private OrderObligation orderObligation;

    @Column
    private Integer originator;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "pending_orders",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "transport_company_id") },
            indexes = {@Index(name = "pending_orders_order_id_index", columnList = "order_id"),
                    @Index(name = "pending_orders_transport_company_id_index", columnList = "transport_company_id")}
    )
    private List<Company> assignedCompanies = new ArrayList<>();


}


