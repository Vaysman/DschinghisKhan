package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
@Table(name = "users",indexes = {
        @Index(name = "users_id_index", columnList = "id"),
        @Index(name = "users_login_index", columnList = "login"),
        @Index(name = "users_originator_index", columnList = "originator")
})
@ToString(exclude = {"company",})
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
    @NotNull
    @JsonView(DataTablesOutput.View.class)
    private String login;

    @Column
    @JsonIgnoreProperties
    private String salt;

    @Column
    @JsonIgnoreProperties
    private String passAndSalt;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonView(DataTablesOutput.View.class)
    private UserRole userRole = UserRole.ROLE_TRANSPORT_COMPANY;

    @Column
    private Integer originator;

    @Column(name = "email")
    @JsonView(DataTablesOutput.View.class)
    private String email;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @PrePersist
    private void prePersist(){
        if(!passAndSalt.isEmpty()) {
            final String encodedPassword = DigestUtils.md5DigestAsHex(this.passAndSalt.getBytes());
            this.salt = RandomStringGenerator.randomAlphaNumeric(16);
            this.passAndSalt = DigestUtils.md5DigestAsHex((encodedPassword+this.salt).getBytes());
        }
    }

    @PreUpdate
    private void preUpdate(){
        if(this.salt.isEmpty() && !passAndSalt.isEmpty()) {
            final String encodedPassword = DigestUtils.md5DigestAsHex(this.passAndSalt.getBytes());
            this.salt = RandomStringGenerator.randomAlphaNumeric(16);
            this.passAndSalt = DigestUtils.md5DigestAsHex((encodedPassword+this.salt).getBytes());
        }
    }


}
