package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.ContactType;

import javax.persistence.*;

@Data
@Entity
@Builder
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name="COMPANY_ID")
    private Company company;

    @Column
    @Enumerated(EnumType.STRING)
    private ContactType type;


}
