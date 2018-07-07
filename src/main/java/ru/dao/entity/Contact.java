package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "contacts", indexes = {
        @Index(name = "contacts_transport_company_id_index", columnList = "transport_company_id"),
        @Index(name = "contacts_originator_index", columnList = "originator")
})
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String taxation;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String address;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String factAddress;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String phone;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private int numberOfTransports;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_company_id")
    @JsonView(DataTablesOutput.View.class)
    private Company company;

    @Column
    private Integer originator;
}
