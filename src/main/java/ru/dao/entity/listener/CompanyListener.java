package ru.dao.entity.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.constant.CompanyType;
import ru.constant.ContactType;
import ru.constant.UserRole;
import ru.dao.entity.Company;
import ru.dao.entity.Contact;
import ru.dao.entity.Point;
import ru.dao.entity.User;
import ru.dao.repository.ContactRepository;
import ru.dao.repository.PointRepository;
import ru.dao.repository.UserRepository;
import ru.util.generator.RandomStringGenerator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import java.util.HashSet;
import java.util.Set;

import static ru.util.Translit.cyr2lat;
import static ru.util.Translit.removeAbbreviations;

@Component
public class CompanyListener {


    private static JavaMailSender sender;
    private static UserRepository userRepository;
    private static ContactRepository contactRepository;
    private static PointRepository pointRepository;

    @Autowired
    public void init(JavaMailSender sender, UserRepository userRepository, ContactRepository contactRepository, PointRepository pointRepository){
        CompanyListener.sender = sender;
        CompanyListener.userRepository = userRepository;
        CompanyListener.contactRepository = contactRepository;
        CompanyListener.pointRepository = pointRepository;
    }

    @PrePersist
    private void prePersist(Company company) throws MessagingException {
        if(!company.getType().equals(CompanyType.TRANSPORT)) {
            return;
        }else {
            company.setShortName(removeAbbreviations(company.getName()));
            String companyUserLogin = cyr2lat(company.getShortName().replaceAll(" ",""));
            while (userRepository.findByLogin(companyUserLogin).isPresent()){
                companyUserLogin= companyUserLogin+"1";
            }
            String userPassword = RandomStringGenerator.randomAlphaNumeric(8);
            User user = User.builder()
                    .login(companyUserLogin)
                    .userRole(UserRole.ROLE_TRANSPORT_COMPANY)
                    .username(company.getShortName())
                    .company(company)
                    .email(company.getEmail())
                    .passAndSalt(userPassword)
                    .build();
            Set<User> userList = new HashSet<>();
            userList.add(user);
            company.setUsers(userList);

            Point point = new Point();
            pointRepository.save(point);
            company.setPoint(point);


            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setFrom("tarificationsquad@gmail.com");
            helper.setTo(company.getEmail());
            helper.setText("Зарегистрирован пользователь для транспортой компании: " +
                    company.getName()+
                    "\nЛогин: "+user.getLogin()+
                    "\nПароль: "+userPassword);
            helper.setSubject("Регистрационные данные");
            if(!company.getEmail().equals("test@tesе.test")) sender.send(message);
        }


    }

    @PostPersist
    public void postPersist(Company company){
        Contact contact = Contact.builder()
                .company(company)
                .email(company.getEmail())
                .type(ContactType.PRIMARY)
                .build();

        contactRepository.save(contact);
    }
}
