package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.RouteReviewOpinion;

import java.util.List;

@PreAuthorize("isFullyAuthenticated()")
public interface RouteReviewOpinionRepository extends PagingAndSortingRepository<RouteReviewOpinion, Integer> {
    List<RouteReviewOpinion> findAllByCompanyId(Integer companyId);
}
