package ru.dao.entity;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Table(name = "route_review_opinions", indexes = {
        @Index(name = "route_review_opinions_id_index", columnList = "id"),
        @Index(name = "route_review_opinions_company_id_index", columnList = "company_id"),
        @Index(name = "route_review_opinions_review_id_index", columnList = "review_id")
})
@Builder
@EqualsAndHashCode(exclude = {"company","review"})
public class RouteReviewOpinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(DataTablesOutput.View.class)
    @Column(name = "id")
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "ID")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(DataTablesOutput.View.class)
    @JoinColumn(name = "review_id", referencedColumnName = "ID")
    private RouteReview review;

    @Column
    private Float price;

    @Column
    private String comment;

}
