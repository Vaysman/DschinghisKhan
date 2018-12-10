import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.Application;
import ru.service.SmsService;

@RunWith(SpringRunner.class)
@AutoConfigureTestEntityManager
@SpringBootTest(classes = Application.class)
public class SmsServiceTest {
    @Autowired
    SmsService smsService;


    @Test
    public void testSmsService(){
        try{
            smsService.sms("79829340294","Fgsfsdfagadfasdf");
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}