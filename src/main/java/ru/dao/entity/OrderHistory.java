package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import ru.constant.OrderLifecycleActions;
import ru.constant.OrderStatus;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Entity
@Builder
@Table(name = "order_history",indexes = {
        @Index(name = "order_history_company_id_index", columnList = "company_id", unique = true),
        @Index(name = "order_history_user_id_index", columnList = "user_id"),
        @Index(name = "order_history_id_index", columnList = "id"),
        @Index(name = "order_history_order_id_index", columnList = "order_id"),
})
@EqualsAndHashCode(exclude = {"order","user","company"})
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
    private Order order;

    @Column
    private String orderNumber;

    @Column
    private Float dispatcherPrice;

    @Column
    private Float companyPrice;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private OrderLifecycleActions action;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column
    private String actionUser;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Column
    private String actionCompany;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @Column
    private Date date = new Date();
}
