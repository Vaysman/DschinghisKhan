package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.RouteReview;

import java.util.List;
@PreAuthorize("isFullyAuthenticated()")
public interface RouteReviewRepository extends PagingAndSortingRepository<RouteReview, Integer> {
    List<RouteReview> findAllByCompanyId(Integer companyId);
}
