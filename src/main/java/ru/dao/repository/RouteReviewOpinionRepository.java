package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entity.RouteReviewOpinion;

import java.util.List;

public interface RouteReviewOpinionRepository extends PagingAndSortingRepository<RouteReviewOpinion, Integer> {
    List<RouteReviewOpinion> findAllByCompanyId(Integer companyId);
}
