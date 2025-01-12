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
            Category categories = Category.builder()
                    .name("Whisky")
                    .description("맥아 효소로 녹말을 포함하고 있는 곡물 재료를 당화시키고 발효[5] 및 증류하여 오크통에 숙성시킨 증류주. 간단히 말해서 목통숙성곡물증류주(木桶熟成穀物蒸溜酒)라고 할 수 있다.")
                    .build();

            categoryRepository.save(categories);

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

                Alcohol alcohols = Alcohol.builder()
                        .name("Alcohol " + i)
                        .strength(40 + i)
                        .size(700 + i)
                        .description("Alcohol Description " + i)
                        .distillery(distilleryRepository.findById((long) 1).get())
                        .category(categoryRepository.findById((long) 1).get())
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
