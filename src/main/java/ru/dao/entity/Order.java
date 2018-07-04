package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.OrderStatus;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Entity
@Table(name = "orders",indexes = {
        @Index(name = "orders_drop_point_id_index", columnList = "drop_point_id"),
        @Index(name = "orders_number_uindex", columnList = "number", unique = true),
        @Index(name = "orders_pickup_point_id_index", columnList = "pickup_point_id", unique = true),
        @Index(name = "orders_drop_point_id_index", columnList = "drop_point_id", unique = true),
        @Index(name = "orders_transport_company_id_index", columnList = "transport_company_id", unique = true),
        @Index(name = "orders_route_id_index", columnList = "route_id", unique = true),
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String number;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "ROUTE_ID", referencedColumnName = "ID")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "TRANSPORT_COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "PICKUP_POINT_ID", referencedColumnName = "ID")
    private Point pickupPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "DROP_POINT_ID", referencedColumnName = "ID")
    private Point dropPoint;


}
