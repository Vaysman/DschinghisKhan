package ru.dao.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.dao.entity.Transport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Transports {
    public static Specification<Transport> transportsForUser(final Integer originator) {
        return (final Root<Transport> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("originator"), originator));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
