package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.transaction.annotation.Transactional;
import ru.constant.PaymentType;

import javax.persistence.*;

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
    private String name;

    @Column
    private String phone;

    @Column
    private String passportNumber;

    @Column
    private String licenseNumber;

    @Column
    private Integer rating;

    @Column
    private Boolean hasMobileApp;

    @Column
    private Boolean isTracked;

    @Column
    private Boolean isHired;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    //Company id
    @Column
    private Integer originator;
}
