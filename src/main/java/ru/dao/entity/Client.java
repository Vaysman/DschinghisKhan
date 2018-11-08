package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "clients", indexes = {
        @Index(name = "clients_id_index", columnList = "id"),
        @Index(name = "clients_name_index", columnList = "name"),
        @Index(name = "clients_originator_index", columnList = "originator")
})
@EqualsAndHashCode(exclude = {"company"})
public class Client {
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
    private String phone;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String contact;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer originator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
}
