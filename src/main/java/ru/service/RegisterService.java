package ru.service;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.configuration.authentication.AuthToken;
import ru.configuration.authentication.CustomAuthenticationManager;
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
import ru.dto.json.user.CompanyPasswordResetRequest;
import ru.dto.json.user.UserRegistrationData;
import ru.util.Translit;
import ru.util.generator.RandomStringGenerator;

import javax.mail.MessagingException;
import java.util.*;

@Service
public class RegisterService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final ResourceLoader resourceLoader;
    private final ContactRepository contactRepository;
    private final UserInfoService userInfoService;
    private final MailService mailService;
    private final SmsService smsService;
    private final Translit translit = new Translit();
    private final CustomAuthenticationManager authenticationManager;

    @Value("${admin-email}")
    private String adminEmail;

    @Autowired
    public RegisterService(CompanyRepository companyRepository, UserRepository userRepository, PointRepository pointRepository, ResourceLoader resourceLoader, ContactRepository contactRepository, UserInfoService userInfoService, MailService mailService, SmsService smsService, CustomAuthenticationManager authenticationManager) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
        this.resourceLoader = resourceLoader;
        this.contactRepository = contactRepository;
        this.userInfoService = userInfoService;
        this.mailService = mailService;
        this.smsService = smsService;
        this.authenticationManager = authenticationManager;
    }

    public Authentication authorize(String username, String password) throws AuthenticationException {
        User user = userRepository.findByLogin(username).orElseThrow(() -> new IllegalArgumentException("Данного пользователя не существует"));
        Authentication auth = new AuthToken(user, password, Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name())), userInfoService);
        Authentication successfullAuthentication = authenticationManager.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(successfullAuthentication);
        return successfullAuthentication;
    }


    public void setAuthorized(User user, String password) {
        UserCredentials forgedCredentials = new UserCredentials(user.getUsername(), password);
        Authentication auth = new AuthToken(user, forgedCredentials, Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name())), userInfoService);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Transactional
    public void resendPassword(Integer companyId) throws Exception {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Компании не существует"));
        this.resendPassword(company);
    }


    @Transactional
    public void resendPassword(Company company) throws Exception {
        User user = company.getUsers().stream().findFirst().orElseThrow(() -> new IllegalStateException("У компании не имеется пользователей. Пожалуйста, свяжитесь с поддержкой."));
        String newPassword = RandomStringGenerator.randomAlphaNumeric(6);
        user.setSalt("");
        user.setPassAndSalt(newPassword);
        userRepository.save(user);
        if (translit.isValidEmail(user.getEmail())) {
            if (!company.getEmail().equals("test@tesе.test")) {
                mailService.send(company.getEmail(),
                        "Регистрационные данные",
                        ("Зарегистрирован пользователь для транспортой компании " +
                                company.getName() +
                                "\nЛогин: " + user.getLogin() +
                                "\nПароль: " + newPassword));
            }
        } else throw new IllegalStateException("Компания имеет некорректный E-mail");
    }

    @Transactional
    public void resendPassword(Company company, String eMail) throws Exception {
        User user = company.getUsers().stream().findFirst().orElseThrow(() -> new IllegalStateException("У компании не имеется пользователей. Пожалуйста, свяжитесь с поддержкой."));
        user.setEmail(eMail);
        this.resendPassword(company);
    }

    @Transactional
    public void resendPassword(Integer userId, String eMail, SendPasswordStrategy passwordStrategy) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Данного пользователя не существует"));
        String newPassword = RandomStringGenerator.randomAlphaNumeric(6);
        user.setSalt("");
        user.setPassAndSalt(newPassword);
        userRepository.save(user);
        if (eMail != null && !eMail.isEmpty() && translit.isValidEmail(eMail)) {
            if (!eMail.equals("test@tesе.test")) {
                mailService.send(eMail,
                        passwordStrategy.getHeader(),
                        (passwordStrategy.getText() +
                                user.getCompany().getName() +
                                "\nЛогин: " + user.getLogin() +
                                "\nПароль: " + newPassword));
            }
        } else throw new IllegalStateException("Указан некорректный E-mail");
    }



    //TODO: crop dispatcher's login to 10 symbols.
    @Transactional
    public User registerDispatcher(UserRegistrationData registrationData) throws Exception {

        companyRepository.findFirstByInn(registrationData.getInn()).ifPresent((x) -> {
            throw new IllegalArgumentException(String.format("Компания с таким ИНН уже существует\n(%s/%s)", x.getName(), x.getEmail()));
        });


        Point point = Point.builder()
                .name(registrationData.getPointName())
                .build();
        pointRepository.save(point);


        String shortName = translit.removeAbbreviations(registrationData.getCompanyName());

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
                //See, right after registerDispatcher() is used, the brand new user is redirected to the mainPage,
                //where AuthorizedController requires managedOffers to be loaded.
                //But since registerDispatcher() of this service and of RegisterController are transactional,
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

        point.setOriginator(company.getId());
        pointRepository.save(point);

        String companyUserLogin = translit.removeSpecialCharacters(translit.cyr2lat(company.getShortName()));
        while (userRepository.findByLogin(companyUserLogin).isPresent()) {
            companyUserLogin = companyUserLogin + "1";
        }

        String userPassword = RandomStringGenerator.randomAlphaNumeric(6);

        User user = User.builder()
                .login(companyUserLogin)
                .email(registrationData.getEmail())
//                .originator(company.getId())
                .username(company.getShortName())
                .userRole(UserRole.ROLE_DISPATCHER)
                .phone(registrationData.getPhone())
                .passAndSalt(userPassword)
                .hasAcceptedCookies(false)
                .company(company)
                .build();
        userRepository.save(user);

        if (!registrationData.getPhone().isEmpty()) {
            smsService.sms(translit.removeSpecialCharacters(registrationData.getPhone()), "Логин: " + companyUserLogin + "\nКод регистрации и пароль:" + userPassword);
        } else if (!company.getEmail().equals("test@test.test") && !company.getEmail().isEmpty()) {
            try {
                String text = "Теперь вам доступен весь функционал сервиса \"Кулуртай\"!\n" +
                        "Ведите работу с вашими перевозчиками, добавляйте новых, обменивайтесь информацией онлайн.\n" +
                        "В приложении подробная инструкция по работе с сервисом. В любое время вам поможет служба поддержки пользователей.\n\n" +
                        "Логин: " + user.getLogin() + "\n" +
                        "Код регистрации и пароль для входа:\n" +
                        userPassword +
                        "\n\nС уважением,\n" +
                        "команда проекта \"Курултай\".\n";
                mailService.send(company.getEmail(),
                        "Регистрация прошла успешно!",
                        text,
                        "Kurulway.pdf",
                        new ByteArrayResource(IOUtils.toByteArray(resourceLoader.getResource("classpath:Kurulway.pdf").getInputStream()))
                );
            } catch (MessagingException e) {
                throw new IllegalArgumentException(String.format("Невозможно отослать письмо о регистрации:\n%s", e.getMessage()));
            }
        } else {
            throw new IllegalArgumentException("Неправильно указан адрес почты, либо телефон");
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

    @Transactional
    public Company registerCompany(Company company) throws MessagingException {
        companyRepository.findFirstByInn(company.getInn()).ifPresent((x) -> {
            throw new IllegalArgumentException(String.format("Компания с таким ИНН уже существует\n(%s)<br> Если перевозчик не имеет доступа к системе - можете выслать сообщение о регистрации повторно.", x.getName()));
        });

        company.setShortName(translit.removeAbbreviations(company.getName()));
        String companyUserLogin = translit.removeSpecialCharacters(translit.cyr2lat(company.getShortName()));
        while (userRepository.findByLogin(companyUserLogin).isPresent()) {
            companyUserLogin = companyUserLogin + "1";
        }
        String userPassword = RandomStringGenerator.randomAlphaNumeric(8);
        User user = User.builder()
                .login(companyUserLogin)
                .userRole(UserRole.ROLE_TRANSPORT_COMPANY)
                .username(company.getShortName())
                .email(company.getEmail())
                .passAndSalt(userPassword)
                .hasAcceptedCookies(false)
                .build();
        userRepository.save(user);
        Set<User> userList = new HashSet<>();
        userList.add(user);
        company.setUsers(userList);

        Point point = new Point();
        pointRepository.save(point);
        company.setPoint(point);


        if (!company.getEmail().equals("test@tesе.test")) {
            mailService.send(company.getEmail(),
                    "Регистрационные данные",
                    ("Зарегистрирован пользователь для транспортой компании: " +
                            company.getName() +
                            "\nЛогин: " + user.getLogin() +
                            "\nПароль: " + userPassword));
        }

        Contact contact = Contact.builder()
                .company(company)
                .email(company.getEmail())
                .type(ContactType.PRIMARY)
                .build();

        contactRepository.save(contact);
        companyRepository.save(company);

        user.setCompany(company);
        userRepository.save(user);
        return company;
    }

    public Map<String, String> registerTransportCompanyProgrammatically(Company company) {
        Map<String, String> companyInfoMap = new HashMap<>();

        companyRepository.findFirstByInn(company.getInn()).ifPresent((x) -> {
            companyInfoMap.put("companyName", company.getName());
            companyInfoMap.put("error", String.format("Компания с таким ИНН уже существует\n(%s)", x.getName()));

        });
        if (companyInfoMap.containsKey("error")) {
            return companyInfoMap;
        }

        company.setShortName(translit.removeAbbreviations(company.getName()));
        String companyUserLogin = translit.removeSpecialCharacters(translit.cyr2lat(company.getShortName()));
        while (userRepository.findByLogin(companyUserLogin).isPresent()) {
            companyUserLogin = companyUserLogin + "1";
        }
        String userPassword = RandomStringGenerator.randomAlphaNumeric(8);
        User user = User.builder()
                .login(companyUserLogin)
                .userRole(UserRole.ROLE_TRANSPORT_COMPANY)
                .username(company.getShortName())
                .email(company.getEmail())
                .passAndSalt(userPassword)
                .hasAcceptedCookies(false)
                .build();
        userRepository.save(user);
        Set<User> userList = new HashSet<>();
        userList.add(user);
        company.setUsers(userList);

        Point point = new Point();
        pointRepository.save(point);
        company.setPoint(point);

        Contact contact = Contact.builder()
                .company(company)
                .email(company.getEmail())
                .type(ContactType.PRIMARY)
                .build();

        contactRepository.save(contact);
        companyRepository.save(company);

        user.setCompany(company);
        userRepository.save(user);

        companyInfoMap.put("companyName", company.getName());
        companyInfoMap.put("login", user.getLogin());
        companyInfoMap.put("password", userPassword);
        return companyInfoMap;
    }

    public void sendPasswordResetRequest(CompanyPasswordResetRequest request, Integer dispatcherCompanyId) throws MessagingException {
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new IllegalArgumentException("No such company exist"));
        if (!company.getType().equals(CompanyType.TRANSPORT)) {
            throw new IllegalStateException("Password reset request is only allowed for transport companies");
        }
        Company dispathcer = companyRepository.findById(dispatcherCompanyId).orElseThrow(() -> new IllegalStateException("Who is sending the request?"));
        mailService.send(adminEmail, "Запрос на восстановление пароля",
                "Поступил запрос на восстановление пароля для компании:" + company.getName() +
                        "\nОт диспетчера:" + dispathcer.getName() +
                        "\nДанные:\n" +
                        "\nE-mail:"+request.getEmail()+
                        "\nТелефон:"+request.getPhoneNumber()+
                        "\nКонтактное лицо:"+request.getContact()
        );
    }


    public void acceptCookies(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("No such user exists"));
        user.setHasAcceptedCookies(true);
        userRepository.save(user);
    }


}

