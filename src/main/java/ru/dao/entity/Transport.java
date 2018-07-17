package ru.dao.entity;

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
    private String number;

    @Column
    private Boolean isGps = false;

    @Column
    @Enumerated(EnumType.STRING)
    private VehicleType type = VehicleType.TRANSPORT;

    @Column
    @Enumerated(EnumType.STRING)
    private VehicleBodyType bodyType = VehicleBodyType.TENT;

    @Column
    private Integer tonnage;

    @Column
    private Integer volume;

    @Column
    @Convert(converter = LoadingTypeArrayToStringConverter.class)
    private Set<LoadingType> loadingType = new HashSet<>();

    @Column
    private Boolean conics = false;

    @Column
    private Boolean hydrobort = false;

    @Column
    private String comment;

    @Column
    private Integer originator;

    @Column
    private String wialonId;


}
