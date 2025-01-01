package kr.co.inntavern.dripking;

import kr.co.inntavern.dripking.model.*;
import kr.co.inntavern.dripking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    public CommandLineRunner loadData(DistilleryRepository distilleryRepository, AlcoholRepository alcoholRepository, DestinationRepository destinationRepository, CategoryRepository categoryRepository, TagRepository tagRepository, UsersRepository usersRepository, ReviewRepository reviewRepository) {
        return args -> {
            for (int i = 0; i < 100; i++) {
                Destination destinations = Destination.builder()
                        .id((long) i)
                        .name("Destination " + i)
                        .description("Description " + i)
                        .build();
                destinationRepository.save(destinations);

                Distillery distilleries = Distillery.builder()
                        .id((long) i)
                        .name("Distillery " + i)
                        .address("Address " + i)
                        .description("Distillery Description " + i)
                        .href("https://upload.wikimedia.org/wikipedia/commons/e/e5/ENIAC-changing_a_tube.jpg")
                        .build();
                distilleryRepository.save(distilleries);

                Tag tags = Tag.builder()
                        .id((long) i)
                        .name("Tag " + i)
                        .description("Description " + i)
                        .build();
                tagRepository.save(tags);

                Category categories = Category.builder()
                        .id((long) i)
                        .name("Category " + i)
                        .description("Description " + i)
                        .build();

                categoryRepository.save(categories);

                Alcohol alcohols = Alcohol.builder()
                        .id((long) i)
                        .name("Alcohol " + i)
                        .strength(40 + i)
                        .size(700 + i)
                        .description("Alcohol Description " + i)
                        .href("https://upload.wikimedia.org/wikipedia/commons/e/e5/ENIAC-changing_a_tube.jpg")
                        .build();
                alcoholRepository.save(alcohols);

                Users users = Users.builder()
                        .id((long) i)
                        .nickname("User " + i)
                        .email("User" + i + "@example.com")
                        .password("password")
                        .build();
                usersRepository.save(users);

                Review reviews = Review.builder()
                        .id((long) i)
                        .rating((byte) 10)
                        .reviewType(ReviewType.valueOf("DESTINATION"))
                        .target_id((long) i)
                        .contents("Contents " + i)
                        .users(usersRepository.findById((long) 1).get())
                        .build();
                reviewRepository.save(reviews);
            }
        };
    }
}
