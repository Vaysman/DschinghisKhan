package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.CompanyType;
import ru.dao.entity.listener.CompanyListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "transport_companies", indexes = {
        @Index(name = "transport_companies_id_index", columnList = "id"),
        @Index(name = "transport_companies_point_id_index", columnList = "point_id"),
        @Index(name = "transport_companies_originator_index", columnList = "originator")
})
@EqualsAndHashCode(exclude = {"users","point","pendingOrders","pendingOrderSet"})
@ToString(exclude = {"users","point","pendingOrders","pendingOrderSet"})
@EntityListeners(CompanyListener.class)
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
    @JsonView(DataTablesOutput.View.class)
    private String email;

    @Column
    private Integer originator;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
            name = "pending_orders",
            joinColumns = { @JoinColumn(name = "transport_company_id") },
            inverseJoinColumns = { @JoinColumn(name = "order_id") },
            indexes = {@Index(name = "pending_orders_order_id_index", columnList = "order_id"),
                    @Index(name = "pending_orders_transport_company_id_index", columnList = "transport_company_id")}
    )
    private Set<Order> pendingOrders = new HashSet<>();

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private Set<PendingOrder> pendingOrderSet = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    Set<User> users = new HashSet<>();

}


