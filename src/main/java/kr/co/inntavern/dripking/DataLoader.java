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
                        .name("Destination " + i)
                        .description("Description " + i)
                        .build();
                destinationRepository.save(destinations);

                Distillery distilleries = Distillery.builder()
                        .name("Distillery " + i)
                        .address("Address " + i)
                        .description("Distillery Description " + i)
                        .img_url("https://upload.wikimedia.org/wikipedia/commons/d/d9/Jameson_Distillery_in_Midleton%2C_2016.jpg")
                        .build();
                distilleryRepository.save(distilleries);

                Tag tags = Tag.builder()
                        .name("Tag " + i)
                        .description("Description " + i)
                        .build();
                tagRepository.save(tags);

                Category categories = Category.builder()
                        .name("Category " + i)
                        .description("Description " + i)
                        .build();

                categoryRepository.save(categories);

                Alcohol alcohols = Alcohol.builder()
                        .name("Alcohol " + i)
                        .strength(40 + i)
                        .size(700 + i)
                        .description("Alcohol Description " + i)
                        .href("https://upload.wikimedia.org/wikipedia/commons/e/e5/Jim_Beam_White_Label.jpg")
                        .build();
                alcoholRepository.save(alcohols);

                Users users = Users.builder()
                        .nickname("User " + i)
                        .email("User" + i + "@example.com")
                        .password("password")
                        .build();
                usersRepository.save(users);

                Review reviews = Review.builder()
                        .rating((byte) 10)
                        .reviewType(ReviewType.valueOf("ALCOHOL"))
                        .target_id((long) i)
                        .contents("리버시블이라활용도가좋네요.어느정도두께감이있어서따뜻할것같아요.플리스촉감도좋고,부드럽네요.착용해보니양쪽다무난하니다예쁘네요.블랙색상이라때도안타고좋아요.무난해서암때나착용가능해서좋아요.\n" +
                                "\n" + i)
                        .users(usersRepository.findById((long) 1).get())
                        .build();
                reviewRepository.save(reviews);
            }
        };
    }
}
