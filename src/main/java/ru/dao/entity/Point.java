package ru.dao.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.dao.entity.listener.PointListener;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "points",indexes = {
        @Index(name = "point_id_index", columnList = "id"),
        @Index(name = "point_name_index", columnList = "name")
})
@EqualsAndHashCode(exclude = {"client"})
@ToString(exclude = {"client"})
@EntityListeners(PointListener.class)
public class Point {
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
    private String address;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String fullAddress;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String comment;

    @Column
    private Integer originator;

    @Column
    private Double x;

    @Column
    private Double y;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String workTime;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "CLIENT_ID")
    @JsonView(DataTablesOutput.View.class)
    private Client client;
}
