package ru.dao.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.constant.CompanyType;
import ru.dao.entity.Company;
import ru.dao.entity.Route;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Companies {
    public static Specification<Company> companiesForRoute(final Route route) {
        return (final Root<Company> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("routes"), route.getId()));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Company> companiesForUser(final Integer originator) {
        return (final Root<Company> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("originator"), originator));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public static Specification<Company> companiesByType(final CompanyType companyType){
        return (final Root<Company> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), companyType);
    }
}
