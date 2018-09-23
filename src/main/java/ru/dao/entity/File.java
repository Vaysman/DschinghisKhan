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
@Table(name = "files",indexes = {
        @Index(name = "files_id_uindex", columnList = "id", unique = true),
})
@EqualsAndHashCode
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @Column
    private String fileName;

    @Column
    private String path;
}
