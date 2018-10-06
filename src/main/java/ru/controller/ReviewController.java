package ru.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.AuthToken;
import ru.dao.entity.Company;
import ru.dao.entity.Route;
import ru.dao.entity.RouteReview;
import ru.dao.entity.RouteReviewOpinion;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.RouteRepository;
import ru.dao.repository.RouteReviewOpinionRepository;
import ru.dao.repository.RouteReviewRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final RouteRepository routeRepository;
    private final RouteReviewRepository reviewRepository;
    private final RouteReviewOpinionRepository opinionRepository;
    private final CompanyRepository companyRepository;

    public ReviewController(RouteRepository routeRepository, RouteReviewRepository reviewRepository, RouteReviewOpinionRepository opinionRepository, CompanyRepository companyRepository) {
        this.routeRepository = routeRepository;
        this.reviewRepository = reviewRepository;
        this.opinionRepository = opinionRepository;
        this.companyRepository = companyRepository;
    }


    @PostMapping(value = "/create/{routeId}")
    @PreAuthorize("hasAuthority('DISPATCHER')")
    @Transactional
    public String changeStatus(@PathVariable Integer routeId, @RequestBody List<Integer> companyIds) {
        try {
            AuthToken authentication = (AuthToken) SecurityContextHolder.getContext().getAuthentication();
            Company dispatcherCompany = companyRepository.findById(authentication.getCompanyId())
                    .orElseThrow(()->new IllegalStateException("Пожалуйста, перезайдите в систему"));
            RouteReview review = new RouteReview();
            Route route = routeRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Маршрута не существует"));

            Set<RouteReviewOpinion> opinions = new HashSet<>();
            companyRepository.findAllById(companyIds).forEach(x -> opinions.add(RouteReviewOpinion.builder()
                    .company(x)
                    .review(review)
                    .build()));
            if (opinions.size() == 0) throw new IllegalArgumentException("Компании не указаны");

            review.setRoute(route);
            review.setOpinions(opinions);
            review.setCompany(dispatcherCompany);
            return "Success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
