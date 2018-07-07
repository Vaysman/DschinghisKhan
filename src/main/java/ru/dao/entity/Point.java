package ru.dao.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Entity
@Table(name = "points",indexes = {
        @Index(name = "point_id_index", columnList = "id"),
        @Index(name = "point_name_index", columnList = "name")
})
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
    private Integer originator;
}
