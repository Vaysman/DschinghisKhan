package ru.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.constant.OrderObligation;
import ru.dao.entity.RouteReview;
import ru.dao.entity.RouteReviewOpinion;
import ru.dao.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserInfoService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final RouteReviewOpinionRepository opinionRepository;
    private final RouteReviewRepository reviewRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public UserInfoService(CompanyRepository companyRepository, UserRepository userRepository, ContactRepository contactRepository, RouteReviewOpinionRepository opinionRepository, RouteReviewRepository reviewRepository, ContractRepository contractRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.opinionRepository = opinionRepository;
        this.reviewRepository = reviewRepository;
        this.contractRepository = contractRepository;
    }


    public List<Map<String,String>> getPendingOrders(Integer companyId){
        return companyRepository.findById(companyId)
                .get().getPendingOrders()
                        .stream()
                        .map(x -> {
                            Map<String, String> orderMap = new HashMap<>();
                            orderMap.put("id", x.getId().toString());
                            orderMap.put("number", x.getNumber());
                            orderMap.put("isMandatory", String.valueOf(x.getOrderObligation().equals(OrderObligation.MANDATORY)));
                            return orderMap;
                        }).collect(Collectors.toList());
    }

    public List<Map<String,String>> getManagedOffers(Integer companyId){
        return companyRepository.findById(companyId).get()
                        .getManagedOffers()
                        .stream()
                        .map(x -> {
                            Map<String, String> orderMap = new HashMap<>();
                            orderMap.put("id", x.getId().toString());
                            orderMap.put("orderNumber", x.getOrderNumber());
                            orderMap.put("isPriceChanged", String.valueOf((!Objects.equals(x.getProposedPrice(), x.getDispatcherPrice()))));
                            orderMap.put("companyName", x.getCompany().getName());
                            orderMap.put("companyId", x.getCompany().getId().toString());
                            return orderMap;
                        }).collect(Collectors.toList());

    }



    public List<Map<String,String>> getReceivedContracts(Integer companyId){
        return  contractRepository.findAllByCompanyId(companyId).stream().map(x->{
            Map<String,String> contractMap = new HashMap<>();
            contractMap.put("id", x.getId().toString());
            contractMap.put("contractName", x.getFile().getFileName());
            contractMap.put("companyName", x.getInitiativeCompany().getName());
            return contractMap;
        }).collect(Collectors.toList());

    }

    public List<RouteReviewOpinion> getOpinions(Integer companyId){
        return opinionRepository.findAllByCompanyId(companyId).stream().peek(x-> {
            Hibernate.initialize(x.getReview());
            Hibernate.initialize(x.getReview().getCompany());
        }).collect(Collectors.toList());
    }


    public List<Map<String,String>> getSentContracts(Integer companyId){
        return contractRepository.findAllByInitiativeCompanyId(companyId)
                .stream().map(x -> {
            Map<String,String> contractMap = new HashMap<>();
            contractMap.put("id", x.getId().toString());
            contractMap.put("contractName", x.getFile().getFileName());
            contractMap.put("companyName", x.getCompany().getName());
            contractMap.put("companyId", x.getCompany().getId().toString());
            return contractMap;
        }).collect(Collectors.toList());
    }

    public List<RouteReview> getReviews(Integer companyId){
        return reviewRepository.findAllByCompanyId(companyId).stream().peek(x->Hibernate.initialize(x.getRoute())).collect(Collectors.toList());
    }




}
