package ru.dao.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "cargo_types", indexes = {
        @Index(name = "cargo_types_id_uindex", columnList = "id"),
        @Index(name = "cargo_types_name_uindex", columnList = "name")
})
public class CargoType {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column
    private String name;

}
