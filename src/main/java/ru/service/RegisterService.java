package ru.service;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.configuration.authentication.AuthToken;
import ru.constant.CompanyType;
import ru.constant.ContactType;
import ru.constant.UserRole;
import ru.dao.entity.Company;
import ru.dao.entity.Contact;
import ru.dao.entity.Point;
import ru.dao.entity.User;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.ContactRepository;
import ru.dao.repository.PointRepository;
import ru.dao.repository.UserRepository;
import ru.dto.json.user.UserRegistrationData;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.HashSet;

@Service
public class RegisterService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final JavaMailSender sender;
    private final ResourceLoader resourceLoader;
    private final ContactRepository contactRepository;
    private final UserInfoService userInfoService;

    @Autowired
    public RegisterService(CompanyRepository companyRepository, UserRepository userRepository, PointRepository pointRepository, JavaMailSender sender, ResourceLoader resourceLoader, ContactRepository contactRepository, UserInfoService userInfoService) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
        this.sender = sender;
        this.resourceLoader = resourceLoader;
        this.contactRepository = contactRepository;
        this.userInfoService = userInfoService;
    }


    public void setAuthorized(User user, String password){
        UserCredentials forgedCredentials = new UserCredentials(user.getUsername(),password);
        Authentication auth = new AuthToken(user, forgedCredentials, Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name())), userInfoService);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }



    @Transactional
    public User register(UserRegistrationData registrationData) throws Exception {
        userRepository.findByLogin(registrationData.getLogin()).ifPresent((x)->{
            throw new IllegalArgumentException("Данный пользователь уже существует");
        });
        companyRepository.findFirstByInn(registrationData.getInn()).ifPresent((x) -> {
            throw new IllegalArgumentException(String.format("Компания с таким ИНН уже существует\n(%s/%s)",x.getName(),x.getEmail()));
        });

        Point point = Point.builder()
                    .address(registrationData.getPointAddress())
                    .name(registrationData.getPointName())
                    .build();
        pointRepository.save(point);

        String[] abbreviations = {"ОАО","ЗАО","ООО","ИП","ПАО","\"","'","!","{","}","(",")","<",">"};
        String shortName = registrationData.getCompanyName();

        for(String abbreviation : abbreviations){
            shortName = shortName.replace(abbreviation,"");
        }

        Company company = Company
                .builder()
                .type(CompanyType.DISPATCHER)
                .shortName(shortName.trim())
                .name(registrationData.getCompanyName())
                .inn(registrationData.getInn())
                .email(registrationData.getEmail())
                .kpp(registrationData.getKpp())
                .ocpo(registrationData.getOcpo())
                .ocved(registrationData.getOcved())
                .ogrn(registrationData.getOgrn())
                .directorFullname(registrationData.getDirectorFullname())
                .point(point)

                //i feel like i should explain this bit with putting an empty hashSet into managedOffers;
                //See, right after register() is used, the brand new user is redirected to the mainPage,
                //where AuthorizedController requires managedOffers to be loaded.
                //But since register() of this service and of RegisterController are transactional,
                //the new user never leaves persistence context and managed offer stays as null
                //instead of being an empty HashSet if we don't specify it in the builder.
                //If we build an object using Lombok's builder - it doesn't account for default field values,
                //as it would with it's generated all/no parameters constructor.
                //For example, if we specify default value as private String userName = "" in User,
                //and then build a new user using User.Builder(),
                //then newUser.getUserName() will be equal to null. Kinda like that.
                .managedOffers(new HashSet<>())
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
                .username(company.getShortName())
                .userRole(UserRole.ROLE_DISPATCHER)
                .passAndSalt(registrationData.getPassword())
                .company(company)
                .build();
        userRepository.save(user);

        if(!company.getEmail().equals("test@test.test")) {
            try {
                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom("tarificationsquad@gmail.com");
                helper.setTo(company.getEmail());
                helper.setSubject("Регистрация прошла успешно!");
                helper.setText("Теперь вам доступен весь функционал сервиса \"Кулуртай\"!\n" +
                        "Ведите работу с вашими перевозчиками, добавляйте новых, обменивайтесь информацией онлайн.\n" +
                        "В приложении подробная инструкция по работе с сервисом. В любое время вам поможет служба поддержки пользователей." +
                        "\n\nС уважением,\n" +
                        "команда проекта \"Курултай\".\n");
                helper.addAttachment("Kurulway.pdf", new ByteArrayResource(IOUtils.toByteArray(resourceLoader.getResource("classpath:Kurulway.pdf").getInputStream())));
                sender.send(message);
            } catch (MessagingException e) {
                throw new IllegalArgumentException(String.format("Невозможно отослать письмо о регистрации:\n%s", e.getMessage()));
            }
        }

        Contact contact = Contact
                .builder()
                .company(company)
                .email(company.getEmail())
                .point(point)
                .type(ContactType.PRIMARY)
                .build();

        contactRepository.save(contact);



        return user;
    }
}
