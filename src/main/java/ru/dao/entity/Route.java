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
        @Index(name = "routes_transport_companies_id_fk", columnList = "transport_company_id"),
        @Index(name = "routes_originator_index", columnList = "originator")
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
    private Double costPerPallet;
    @Column
    @JsonView(DataTablesOutput.View.class)
    private Double costPerPalletNds;

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
//    @JsonView(DataTablesOutput.View.class)
    private VehicleType vehicleType;

    @Column
//    @JsonView(DataTablesOutput.View.class)
    private String comment;



    @ManyToOne
//    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "TRANSPORT_COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private List<RoutePoint> routePoints = new ArrayList<>();


    @Column
    private Integer originator;


    //Calculating NDS/noNDS, if user didn't do so himself, oof..
    @PrePersist
    @PreUpdate
    private void calculatePrices(){
        if(totalCost==null && totalCostNds!=null){
            totalCost=calculateNoNds(totalCostNds);
        }
        if(totalCostNds==null && totalCost!=null){
            totalCostNds = calculateNds(totalCost);
        }
        if(costPerBox==null && costPerBoxNds!=null){
            costPerBox=calculateNoNds(costPerBoxNds);
        }
        if(costPerBox!=null && costPerBoxNds==null){
            costPerBoxNds=calculateNds(costPerBox);
        }
        if(costPerKilometer==null && costPerKilometerNds!=null){
            costPerKilometer=calculateNoNds(costPerKilometerNds);
        }
        if(costPerKilometer!=null && costPerKilometerNds==null){
            costPerKilometerNds=calculateNds(costPerKilometer);
        }
        if(costPerPrr==null && costPerPrrNds!=null){
            costPerPrr=calculateNoNds(costPerPrrNds);
        }
        if(costPerPrr!=null && costPerPrrNds==null){
            costPerPrrNds=calculateNds(costPerPrr);
        }
        if(costPerPallet==null && costPerPalletNds!=null){
            costPerPallet=calculateNoNds(costPerPalletNds);
        }
        if(costPerPallet!=null && costPerPalletNds==null){
            costPerPalletNds=calculateNds(costPerPallet);
        }
    }

    private Double calculateNds(Double noNds){
        return noNds*1.18;
    }

    private Double calculateNoNds(Double nds){
        return nds*0.82;
    }
}
