package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.configuration.authentication.AuthToken;
import ru.constant.CompanyType;
import ru.constant.UserRole;
import ru.dao.entity.Company;
import ru.dao.entity.Point;
import ru.dao.entity.User;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.PointRepository;
import ru.dao.repository.UserRepository;
import ru.dto.json.user.UserRegistrationData;

import java.util.Collections;

@Service
public class UserManagementService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    @Autowired
    public UserManagementService(CompanyRepository companyRepository, UserRepository userRepository, PointRepository pointRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
    }


    public void setAuthorized(User user, String password){
        UserCredentials forgedCredentials = new UserCredentials(user.getUsername(),password);
        Authentication auth = new AuthToken(user, forgedCredentials, Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name())));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }



    @Transactional
    public User register(UserRegistrationData registrationData){
        if (userRepository.findByLogin(registrationData.getLogin()).isPresent()) throw new IllegalArgumentException("Данный пользователь уже существует");

        Point point = null;
        if(!registrationData.getPointAddress().isEmpty() && !registrationData.getPointName().isEmpty()){
            point = Point.builder()
                    .address(registrationData.getPointAddress())
                    .name(registrationData.getPointName())
                    .build();
            pointRepository.save(point);
        }

        Company company = Company
                .builder()
                .type(CompanyType.DISPATCHER)
                .shortName(registrationData.getCompanyShortName())
                .name(registrationData.getCompanyName())
                .email(registrationData.getEmail())
                .point(point)
                .build();
        companyRepository.save(company);

        if(point!=null) {
            point.setOriginator(company.getId());
            pointRepository.save(point);
        }

        User user = User.builder()
                .login(registrationData.getLogin())
                .email(registrationData.getEmail())
                .originator(company.getId())
                .username(registrationData.getCompanyShortName())
                .userRole(UserRole.ROLE_DISPATCHER)
                .passAndSalt(registrationData.getPassword())
                .company(company)
                .build();
        userRepository.save(user);


        return user;
    }
}
