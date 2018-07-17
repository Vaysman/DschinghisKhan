package ru.dao.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;
import java.util.Objects;

@Data
@Builder
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Objects.equals(id, point.id) &&
                Objects.equals(name, point.name) &&
                Objects.equals(address, point.address) &&
                Objects.equals(originator, point.originator);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, address, originator);
    }
}
