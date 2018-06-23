package ru.dao.entities;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.UserRole;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column
    private Integer id;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String username;

    @Column
    @JsonView(DataTablesOutput.View.class)
    private String login;

    @Column
    private String salt;

    @Column
    private String passAndSalt;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole userRole;


}
