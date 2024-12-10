package kr.co.inntavern.dripking;

import kr.co.inntavern.dripking.model.*;
import kr.co.inntavern.dripking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    public CommandLineRunner loadData(DistilleryRepository distilleryRepository, AlcoholRepository alcoholRepository, DestinationRepository destinationRepository, CategoryRepository categoryRepository, TagRepository tagRepository){
        return args ->{
            for(int i=0; i<100; i++){
                Destination destinations = Destination.builder()
                        .id((long) i)
                        .name("Destination " + i)
                        .description("Description " + i)
                        .build();
                destinationRepository.save(destinations);



                Distillery distillerys = Distillery.builder()
                        .id((long) i)
                        .name("Distillery " + i)
                        .address("Address " + i)
                        .description("Description " + i)
                        .href("http://naver.com" + i)
                        .build();
                distilleryRepository.save(distillerys);



                Tag tags = Tag.builder()
                        .id((long) i)
                        .name("Tag " + i)
                        .description("Description " + i)
                        .build();
                tagRepository.save(tags);

                Category categorys = Category.builder()
                        .id((long) i)
                        .name("Category " + i)
                        .description("Description " + i)
                        .build();

                categoryRepository.save(categorys);



                Alcohol alcohols = Alcohol.builder()
                        .id((long) i)
                        .name("Alcohol " + i)
                        .strength(40 + i)
                        .size(700 + i)
                        .description("Description " + i)
                        .href("http://naver.com" + i)
                        .build();
                alcoholRepository.save(alcohols);
            }
        };
    }
}
