package ru.dao.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.constant.OrderStatus;
import ru.dao.entity.Company;
import ru.dao.entity.Order;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Orders {
    public static Specification<Order> ordersForUser(final Integer originator) {
        return (final Root<Order> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> criteriaBuilder.equal(root.get("originator"),originator);
    }

    public static Specification<Order> ordersInStatus(OrderStatus[] orderStatuses) {
        return (final Root<Order> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            for (OrderStatus orderStatus : orderStatuses) {
                predicates.add(criteriaBuilder.equal(root.<OrderStatus>get("status"), orderStatus));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Order> ordersForCompany(final Integer companyId) {
        return (final Root<Order> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> criteriaBuilder.equal(root.<Company>get("company"),companyId);
    }

}
