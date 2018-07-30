import com.google.maps.model.GeocodingResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.Application;
import ru.service.GeocodingService;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestEntityManager
@SpringBootTest(classes = Application.class)
public class GeocodingServiceTest {
    @Autowired
    GeocodingService geocodingService;


    @Test
    public void testGeocodingService(){
        try{
            GeocodingResult[] results = geocodingService.getAddressCoordinates("Тюмень");
            assertThat(results).isNotEmpty();
        } catch (Exception e){
            e.printStackTrace();
            assertThat(false).isEqualTo(true);
        }

    }
}

