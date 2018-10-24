package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.CompanyType;
import ru.constant.TaxationType;
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
@EqualsAndHashCode(exclude = {"users", "point", "pendingOrders","transports","drivers","managedOffers"})
@ToString(exclude = {"users", "point", "pendingOrders","transports","drivers","managedOffers"})
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
    @JsonIgnore
    @JsonView(DataTablesOutput.View.class)
    private Point point;

    @Column(unique = true)
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
    private CompanyType type = CompanyType.TRANSPORT;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String email;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer originator;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(
            name = "pending_orders",
            joinColumns = { @JoinColumn(name = "transport_company_id") },
            inverseJoinColumns = { @JoinColumn(name = "order_id") },
            indexes = {@Index(name = "pending_orders_order_id_index", columnList = "order_id"),
                    @Index(name = "pending_orders_transport_company_id_index", columnList = "transport_company_id")}
    )
    private Set<Order> pendingOrders = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "managerCompany", cascade = CascadeType.ALL)
    private Set<OrderOffer> managedOffers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "originator", cascade = CascadeType.ALL)
    private Set<Transport> transports = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "originator", cascade = CascadeType.ALL)
    private Set<Driver> drivers = new HashSet<>();

    @Column
    @Enumerated(EnumType.STRING)
    private TaxationType taxationType;

    @Column
    private String kpp = "";

    @Column
    private String bik = "";

    @Column
    private String corresAccount = "";

    @Column
    private String curAccount = "";

    @Column
    private String bankName = "";

    @Column
    private String directorFullname = "";

    @Column
    private String chiefAccFullname = "";

//    @JsonView(DataTablesOutput.View.class)
//    @OneToMany(cascade = { CascadeType.ALL}, mappedBy = "company", fetch = FetchType.LAZY)
//    Set<Contract> receivedContracts = new HashSet<>();

}


