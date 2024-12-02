package kr.co.inntavern.dripking;

import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class DripkingApplicationTests {

    @Autowired
    private DistilleryRepository distilleryRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testDistilleryRepository(){
        Distillery distillery = new Distillery();
        distillery.setAddress("Seoul");
        distillery.setName("Operation");
        distillery.setDescription("A fine distillery");

        Distillery savedDistillery = distilleryRepository.save(distillery);
        Optional<Distillery> foundDistillery = distilleryRepository.findById(savedDistillery.getId());

        assertThat(distillery.getAddress()).isEqualTo("Seoul");
    }

}
