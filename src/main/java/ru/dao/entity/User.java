package ru.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.DigestUtils;
import ru.constant.UserRole;
import ru.util.generator.RandomStringGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Entity
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
@Table(name = "users",indexes = {
        @Index(name = "users_id_index", columnList = "id"),
        @Index(name = "users_login_index", columnList = "login")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @JsonView(DataTablesOutput.View.class)
    @Column
    private Integer id;

    @Column
//    @JsonView(DataTablesOutput.View.class)
    private String username;

    @Column
    @NotNull
//    @JsonView(DataTablesOutput.View.class)
    private String login;

    @Column
    private String salt;

    @Column
    private String passAndSalt;

    @Column
    @Enumerated(EnumType.STRING)
//    @JsonView(DataTablesOutput.View.class)
    private UserRole userRole;

    @Column
    private Integer originator;


    @PrePersist
    private void prePersist(){
        if(!passAndSalt.isEmpty()) {
            final String encodedPassword = DigestUtils.md5DigestAsHex(this.passAndSalt.getBytes());
            this.salt = RandomStringGenerator.randomAlphaNumeric(16);
            this.passAndSalt = DigestUtils.md5DigestAsHex((encodedPassword+this.salt).getBytes());
        }
        if (userRole==null) this.userRole=UserRole.TRANSPORT_COMPANY;
        System.out.println("prePersist called");
    }

    @PreUpdate
    private void preUpdate(){
        if(this.salt.isEmpty() && !passAndSalt.isEmpty()) {
            final String encodedPassword = DigestUtils.md5DigestAsHex(this.passAndSalt.getBytes());
            this.salt = RandomStringGenerator.randomAlphaNumeric(16);
            this.passAndSalt = DigestUtils.md5DigestAsHex((encodedPassword+this.salt).getBytes());
        }
        System.out.println("preUpdate called");
    }


}
