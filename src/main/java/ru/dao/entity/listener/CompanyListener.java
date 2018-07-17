package ru.dao.entity.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.constant.UserRole;
import ru.dao.entity.Company;
import ru.dao.entity.User;
import ru.dao.repository.UserRepository;
import ru.util.generator.RandomStringGenerator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.PrePersist;
import java.util.HashSet;
import java.util.Set;

@Component
public class CompanyListener {


    private static JavaMailSender sender;
    private static UserRepository userRepository;

    @Autowired
    public void init(JavaMailSender sender, UserRepository userRepository){
        CompanyListener.sender = sender;
        CompanyListener.userRepository = userRepository;
    }

    @PrePersist
    private void prePersist(Company company) throws MessagingException {
        String userPassword = RandomStringGenerator.randomAlphaNumeric(8);
        User user = User.builder()
                .login(company.getShortName())
                .userRole(UserRole.ROLE_TRANSPORT_COMPANY)
                .username(company.getShortName())
                .company(company)
                .originator(company.getOriginator())
                .email(company.getEmail())
                .passAndSalt(userPassword)
                .build();
        Set<User> userList = new HashSet<>();
        userList.add(user);
        company.setUsers(userList);
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setTo(company.getEmail());
        helper.setText("Зарегистрирован пользователь для транспортой компании: " +
                company.getName()+
        "\nЛогин: "+user.getLogin()+
        "\nПароль: "+userPassword);
        helper.setSubject("Регистрационные данные");
        sender.send(message);

    }
}
