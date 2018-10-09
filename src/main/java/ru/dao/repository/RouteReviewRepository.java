package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entity.RouteReview;

import java.util.List;

public interface RouteReviewRepository extends PagingAndSortingRepository<RouteReview, Integer> {
    List<RouteReview> findAllByCompanyId(Integer companyId);
}
