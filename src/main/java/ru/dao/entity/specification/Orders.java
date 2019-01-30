package ru.dao.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.constant.OrderStatus;
import ru.dao.entity.Company;
import ru.dao.entity.Order;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Orders {
    public static Specification<Order> ordersForUser(final Integer originator) {
        return (final Root<Order> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> criteriaBuilder.equal(root.get("originator"),originator);
    }

    public static Specification<Order> ordersForUserBetweenDates(final Integer originator, final Date from, final Date to) {
        return (final Root<Order> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final Predicate[] predicates = new Predicate[2];
            predicates[0] =(criteriaBuilder.between(root.get("dispatchDate"),from,to));
            predicates[1] =(criteriaBuilder.equal(root.get("originator"),originator));
            return criteriaBuilder.and(predicates);
        };
    }

    public static Specification<Order> ordersNotInStatus(OrderStatus[] orderStatuses){
        return (final Root<Order> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            for (OrderStatus orderStatus : orderStatuses) {
                predicates.add(criteriaBuilder.notEqual(root.<OrderStatus>get("status"), orderStatus));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));
        };
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
