import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.Application;
import ru.constant.CompanyType;
import ru.dao.entity.Company;
import ru.dao.entity.User;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestEntityManager
@Transactional
@SpringBootTest(classes = Application.class)
public class CompanyListenerTest {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void createCompany_whenSave_thenCheckUser(){
        Company company = Company
                .builder()
                .email("test@test.test")
                .name("ООО Тестерони")
                .originator(0)
                .type(CompanyType.TRANSPORT)
                .build();
        companyRepository.save(company);


        List<Company> companyList = companyRepository.findTop10ByNameContaining("ООО Тестерони");
        assertThat(companyList.size()).isNotEqualTo(0);
        Company savedCompany = companyList.get(0);
        User user = userRepository.findByLogin("Testeroni").orElse(null);
        assertThat(user).isNotEqualTo(null);
        assertThat(savedCompany).isNotEqualTo(null);
        assertThat(savedCompany).isEqualTo(company);
    }
}
