package ru.dao.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.dao.entity.OrderOffer;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class OrderOffers {
    public static Specification<OrderOffer> orderOffersForCompany(final Integer companyId) {
        return (final Root<OrderOffer> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("company"), companyId));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
