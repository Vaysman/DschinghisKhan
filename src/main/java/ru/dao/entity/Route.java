package ru.dao.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.LoadingType;
import ru.constant.VehicleType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "routes", indexes = {
        @Index(name = "routes_id_index", columnList = "id"),
        @Index(name = "routes_name_index", columnList = "name"),
        @Index(name = "routes_transport_companies_id_fk", columnList = "transport_company_id")
})
public class Route {
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
    private Double totalCost;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double totalCostNds;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double costPerKilometer;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double costPerKilometerNds;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double costPerPrr;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double costPerPrrNds;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double costPerBox;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double tonnage;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double volume;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double costPerBoxNds;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer tempTo;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer tempFrom;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private LoadingType loadingType;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private VehicleType vehicleType;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String comment;



    @ManyToOne
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "TRANSPORT_COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @OneToMany(mappedBy = "point", fetch = FetchType.LAZY)
    private List<RoutePoint> routePoints = new ArrayList<>();


    @Column
    private Integer originator;



}
