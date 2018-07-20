package ru.dao.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.dao.entity.OrderHistory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class OrderHistories {
    public static Specification<OrderHistory> historyForOrder(final Integer orderId) {
        return (final Root<OrderHistory> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("orderId"), orderId));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
