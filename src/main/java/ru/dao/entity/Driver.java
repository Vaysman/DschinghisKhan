package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.transaction.annotation.Transactional;
import ru.constant.DriverPaymentType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
@Transactional
@Table(name = "drivers",indexes = {
        @Index(name = "drivers_id_uindex", columnList = "id"),
        @Index(name = "drivers_name_index", columnList = "name"),
        @Index(name = "drivers_originator_index", columnList = "originator")
})
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column
    private Integer id;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String name;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String phone;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String passportNumber;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String licenseNumber;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer rating;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Boolean hasMobileApp;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String mobileAppNumber;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Boolean isTracked;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String trackingNumber;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private Boolean isHired;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private DriverPaymentType paymentType;

    //Company id
    @Column
    @JsonView(DataTablesOutput.View.class)
    private Integer originator;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.ALL})
    @JoinTable(
            name = "files_to_drivers",
            joinColumns = { @JoinColumn(name = "driver_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") },
            indexes = {@Index(name = "files_to_drivers_driver_id_index", columnList = "driver_id"),
                    @Index(name = "files_to_drivers_file_id_index", columnList = "file_id")}
    )
    private Set<File> files = new HashSet<>();


}
