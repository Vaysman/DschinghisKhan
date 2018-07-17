package ru.dao.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "route_points", indexes = {
        @Index(name = "route_points_id_index", columnList = "id", unique = false),
        @Index(name = "route_points_point_id_index", columnList = "point_id", unique = false)
})
public class RoutePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @JsonView(DataTablesOutput.View.class)
    private Integer id;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="POINT_ID", referencedColumnName = "ID")
    private Point point;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer queueNumber;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer distance;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer cost;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer loadingTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name="ROUTE_ID", referencedColumnName = "ID")
    private Route route;

}
