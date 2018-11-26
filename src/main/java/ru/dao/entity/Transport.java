package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.transaction.annotation.Transactional;
import ru.constant.LoadingType;
import ru.constant.VehicleBodyType;
import ru.constant.VehicleType;
import ru.dao.entity.converter.LoadingTypeArrayToStringConverter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
@Transactional
@Table(name = "transports",indexes = {
        @Index(name = "transports_id_uindex", columnList = "id"),
        @Index(name = "transports_number_index", columnList = "number"),
        @Index(name = "transports_originator_index", columnList = "originator")
})
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column
    private Integer id;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String number;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Boolean isGps = false;

    @Column
    @JsonView(DataTablesOutput.View.class)
    @Enumerated(EnumType.STRING)
    private VehicleType type = VehicleType.TRANSPORT;

    @Column
    @JsonView(DataTablesOutput.View.class)
    @Enumerated(EnumType.STRING)
    private VehicleBodyType bodyType = VehicleBodyType.TENT;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer tonnage;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer volume;

    @Column
    @JsonView(DataTablesOutput.View.class)
    @Convert(converter = LoadingTypeArrayToStringConverter.class)
    private Set<LoadingType> loadingType = new HashSet<>();

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Boolean conics = false;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Boolean hydrobort = false;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String comment;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer originator;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String wialonId;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.ALL})
    @JoinTable(
            name = "files_to_transport",
            joinColumns = { @JoinColumn(name = "transport_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") },
            indexes = {@Index(name = "files_to_transport_transport_id_index", columnList = "transport_id"),
                    @Index(name = "files_to_transport_file_id_index", columnList = "file_id")}
    )
    Set<StoredFile> files = new HashSet<>();


}
