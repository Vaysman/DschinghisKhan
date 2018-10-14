package ru.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.AuthToken;
import ru.constant.ReviewStatus;
import ru.dao.entity.Company;
import ru.dao.entity.Route;
import ru.dao.entity.RouteReview;
import ru.dao.entity.RouteReviewOpinion;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.RouteRepository;
import ru.dao.repository.RouteReviewOpinionRepository;
import ru.dao.repository.RouteReviewRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
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


    @PostMapping(value="/sendOpinion/{opinionId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    public String sendOpinion(@PathVariable Integer opinionId, @RequestBody MultiValueMap<String, String> map, HttpServletRequest request){
        RouteReviewOpinion opinion = opinionRepository.findById(opinionId).orElseThrow(()->new IllegalArgumentException("Мнения не существует, либо ревью было удалено"));
        opinion.setPrice(Float.valueOf(map.get("price").get(0)));
        opinion.setComment(map.get("comment").get(0));
        if(opinion.getReview().getOpinions().stream().filter(x->x.getPrice()==null).count()==0){
            opinion.getReview().setStatus(ReviewStatus.COMPLETE);
        } else {
            opinion.getReview().setStatus(ReviewStatus.RESPONDED);
        }
        reviewRepository.save(opinion.getReview());
        opinionRepository.save(opinion);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }

    @PostMapping(value = "/create/{routeId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasAuthority('DISPATCHER')")
    @ResponseBody
    public String createReview(@PathVariable Integer routeId, @RequestBody List<Integer> companyIds) {
        try {
            AuthToken authentication = (AuthToken) SecurityContextHolder.getContext().getAuthentication();
            Company dispatcherCompany = companyRepository.findById(authentication.getCompanyId())
                    .orElseThrow(()->new IllegalStateException("Пожалуйста, перезайдите в систему"));
            RouteReview review = new RouteReview();
            Route route = routeRepository.findById(routeId)
                    .orElseThrow(() -> new IllegalArgumentException("Маршрута не существует"));

            List<RouteReviewOpinion> opinions = new ArrayList<>();
            companyRepository.findAllById(companyIds).forEach(x -> opinions.add(RouteReviewOpinion.builder()
                    .company(x)
                    .review(review)
                    .build()));
            if (opinions.size() == 0) throw new IllegalArgumentException("Компании не указаны");

            review.setStatus(ReviewStatus.CREATED);
            review.setRoute(route);
            review.setOpinions(opinions);
            review.setCompany(dispatcherCompany);
            reviewRepository.save(review);
            return "Success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
