import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import ru.Application;
import ru.constant.CompanyType;
import ru.constant.UserRole;
import ru.dao.entity.Company;
import ru.dao.entity.Point;
import ru.dao.entity.User;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.PointRepository;
import ru.dao.repository.UserRepository;
import ru.dto.json.user.UserRegistrationData;
import ru.service.RegisterService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestEntityManager
@Transactional
@SpringBootTest(classes = Application.class)
public class RegistrationTest {


    @Autowired
    private RegisterService registerService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PointRepository pointRepository;

    @Test
    public void registerUser_thenGetCompany_thenGetPoint_thenGetUser_thenCheckPassword(){
        try {
        UserRegistrationData registrationData = UserRegistrationData.builder()
                .companyName("ИП tCompany")
                .email("test@test.test")
                .pointName("testPointName")
                .build();

        User registeredUser = registerService.registerDispatcher(registrationData);

        List<Company> foundCompanies = companyRepository.findTop10ByNameContaining("tCompany");
        assertThat(foundCompanies.size()).isNotEqualTo(0);

        Company company = foundCompanies.get(0);

        assertThat(registeredUser.getCompany()).isEqualTo(company);
        assertThat(company.getType()).isEqualTo(CompanyType.DISPATCHER);


        User user = userRepository.findByLogin("tcomp").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_DISPATCHER);
        assertThat(user.getSalt()).isNotEmpty();
        assertThat(user.getOriginator()).isEqualTo(company.getId());
        assertThat(user).isEqualTo(registeredUser);

        String correctPassAndSalt = DigestUtils.md5DigestAsHex((DigestUtils.md5DigestAsHex(("test").getBytes())+user.getSalt()).getBytes());
        assertThat(user.getPassAndSalt()).isEqualTo(correctPassAndSalt);

        List<Point> points = pointRepository.findTop10ByNameContainingAndOriginator("testPointName",company.getId());
        assertThat(points.size()).isNotEqualTo(0);
        Point point = points.get(0);
        assertThat(company.getPoint()).isEqualTo(point);

        registerService.setAuthorized(registeredUser,"test");
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
        //If you made it here - congrats
        } catch (Exception e){
            assertThat(false).isEqualTo(true);
        }
    }



}
