package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "contracts", indexes = {
        @Index(name = "contracts_company_id_index", columnList = "company_id"),
        @Index(name = "contracts_file_id_index", columnList = "file_id"),
//        @Index(name = "contracts_initiative_company_id_index", columnList = "initiative_company_id"),
})
@EqualsAndHashCode(exclude = {"company","file"})
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FILE_ID", referencedColumnName = "ID")
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @JsonView(DataTablesOutput.View.class)
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "INITIATIVE_COMPANY_ID", referencedColumnName = "ID")
    @Column
    private Integer initiativeCompanyId;

}
