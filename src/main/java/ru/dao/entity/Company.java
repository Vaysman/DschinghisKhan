package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.CompanyType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "transport_companies", indexes = {
        @Index(name = "transport_companies_id_index", columnList = "id"),
        @Index(name = "transport_companies_point_id_index", columnList = "point_id"),
        @Index(name = "transport_companies_user_id_index", columnList = "user_id"),
        @Index(name = "transport_companies_originator_index", columnList = "originator")
})
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String name;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String shortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POINT_ID")
    @JsonView(DataTablesOutput.View.class)
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonView(DataTablesOutput.View.class)
    private User user;

    @OneToMany(mappedBy = "company")
    private List<Route> routes;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String inn;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer numberOfTransports;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String atiCode;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String accountantName;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String ocved;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String ocpo;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String ogrn;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private CompanyType type;

    @Column
    private Integer originator;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "pending_orders",
            joinColumns = { @JoinColumn(name = "transport_company_id") },
            inverseJoinColumns = { @JoinColumn(name = "order_id") },
            indexes = {@Index(name = "pending_orders_order_id_index", columnList = "order_id"),
                    @Index(name = "pending_orders_transport_company_id_index", columnList = "transport_company_id")}
    )
    private List<Order> pendingOrders = new ArrayList<>();

}


