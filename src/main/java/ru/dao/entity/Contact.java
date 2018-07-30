package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "contacts", indexes = {
        @Index(name = "contacts_originator_index", columnList = "originator"),
        @Index(name = "contacts_name_index", columnList = "name")
})
@EqualsAndHashCode(exclude = {"point"})
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @JsonView(DataTablesOutput.View.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="POINT_ID", referencedColumnName = "ID")
    private Point point;


    @Column
    @JsonView(DataTablesOutput.View.class)
    private String phone;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String name;

    @Column
    private Integer originator;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String position;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String email;



}
