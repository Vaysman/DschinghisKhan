package ru.dao.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.LoadingType;
import ru.constant.VehicleBodyType;
import ru.dao.entity.listener.RouteListener;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

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
@EntityListeners(RouteListener.class)
@EqualsAndHashCode(exclude = {"company","routePoints"})
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
    private Float totalCost;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float totalCostNds;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float costPerKilometer;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float costPerKilometerNds;

//    @Column
//    @JsonView(DataTablesOutput.View.class)
//    private Float costPerPrr;
//
//    @Column
//    @JsonView(DataTablesOutput.View.class)
//    private Float costPerPrrNds;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float costPerBox;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float tonnage;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float volume;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float costPerBoxNds;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float costPerPallet;
    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float costPerPalletNds;

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
    private VehicleBodyType vehicleType= VehicleBodyType.TENT;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String comment;



    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "TRANSPORT_COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY, cascade = { CascadeType.ALL})
    @OrderBy("queueNumber ASC")
    private SortedSet<RoutePoint> routePoints = new TreeSet<>();


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
//        if(costPerPrr==null && costPerPrrNds!=null){
//            costPerPrr=calculateNoNds(costPerPrrNds);
//        }
//        if(costPerPrr!=null && costPerPrrNds==null){
//            costPerPrrNds=calculateNds(costPerPrr);
//        }
        if(costPerPallet==null && costPerPalletNds!=null){
            costPerPallet=calculateNoNds(costPerPalletNds);
        }
        if(costPerPallet!=null && costPerPalletNds==null){
            costPerPalletNds=calculateNds(costPerPallet);
        }
    }

    private Float calculateNds(Float noNds){
        return (float) (noNds*1.18);
    }

    private Float calculateNoNds(Float nds){
        return (float) (nds * 0.82);
    }
}
