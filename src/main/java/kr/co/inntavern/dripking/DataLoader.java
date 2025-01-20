package kr.co.inntavern.dripking;

import kr.co.inntavern.dripking.model.*;
import kr.co.inntavern.dripking.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    @Bean
    public CommandLineRunner loadData(DistilleryRepository distilleryRepository, AlcoholRepository alcoholRepository, DestinationRepository destinationRepository, CategoryRepository categoryRepository, TagRepository tagRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        return args -> {

            for (int i = 1; i <= 100; i++) {
                Category categories = Category.builder()
                        .name("Whisky " + i )
                        .description("맥아 효소로 녹말을 포함하고 있는 곡물 재료를 당화시키고 발효[5] 및 증류하여 오크통에 숙성시킨 증류주. 간단히 말해서 목통숙성곡물증류주(木桶熟成穀物蒸溜酒)라고 할 수 있다. " + i)
                        .build();
                categoryRepository.save(categories);

                Destination destinations = Destination.builder()
                        .name("Destination " + i)
                        .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book." + i)
                        .img_url("https://upload.wikimedia.org/wikipedia/commons/e/ea/Taipei_Skyline_2022.06.29.jpg")
                        .itemType(ItemType.DESTINATION)
                        .build();
                destinationRepository.save(destinations);

                Distillery distilleries = Distillery.builder()
                        .name("Distillery " + i)
                        .address("Address " + i)
                        .description("Distillery Description " + i)
                        .img_url("https://upload.wikimedia.org/wikipedia/commons/f/f0/Yamazaki_Distillery_%E5%B1%B1%E5%B4%8E%E8%92%B8%E7%95%99%E6%89%8005.jpg")
                        .itemType(ItemType.DISTILLERY)
                        .destination(destinationRepository.findById((long) i).get())
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
                        .distillery(distilleryRepository.findById((long) i).get())
                        .category(categoryRepository.findById((long) i).get())
                        .img_url("https://upload.wikimedia.org/wikipedia/commons/e/e5/Jim_Beam_White_Label.jpg")
                        .itemType(ItemType.ALCOHOL)
                        .build();
                alcoholRepository.save(alcohols);

                User user = new User();
                user.setNickname("User" + i);
                user.setEmail("User" + i + "@example.com");
                user.setPassword("password");

                userRepository.save(user);

                Review reviews = Review.builder()
                        .rating((byte) 10)
                        .reviewType(ReviewType.valueOf("ALCOHOL"))
                        .target_id((long) i)
                        .contents("리버시블이라활용도가좋네요.어느정도두께감이있어서따뜻할것같아요.플리스촉감도좋고,부드럽네요.착용해보니양쪽다무난하니다예쁘네요.블랙색상이라때도안타고좋아요.무난해서암때나착용가능해서좋아요.\n" +
                                "\n" + i)
                        .user(userRepository.findById((long) i).get())
                        .build();
                reviewRepository.save(reviews);
            }

        };
    }
}
