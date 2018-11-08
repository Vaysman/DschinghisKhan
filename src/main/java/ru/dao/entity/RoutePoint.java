package ru.dao.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
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
@EqualsAndHashCode(exclude = {"point","route","contact","client"})
public class RoutePoint implements Comparable<RoutePoint>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    @JsonView(DataTablesOutput.View.class)
    private Integer id;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="POINT_ID")
    private Point point;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer queueNumber;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer distance;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float cost;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Float prrCost;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer loadingTime;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String arrivalTime;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer timeEnRoute;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ROUTE_ID", referencedColumnName = "ID")
    private Route route;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CLIENT_ID", referencedColumnName = "ID")
    private Client client;

    @Override
    public int compareTo(RoutePoint o) {
        return this.queueNumber-o.queueNumber;
    }
}
