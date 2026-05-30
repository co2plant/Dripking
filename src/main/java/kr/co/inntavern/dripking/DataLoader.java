package kr.co.inntavern.dripking;

import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.*;
import kr.co.inntavern.dripking.model.enumType.*;
import kr.co.inntavern.dripking.repository.*;
import kr.co.inntavern.dripking.security.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@Profile("dev")
public class DataLoader {
    static final List<String> LAUNCH_CATEGORY_NAMES = List.of(
            "위스키", "럼", "보드카", "진", "데킬라", "브랜디", "리큐르", "맥주", "사케", "전통주"
    );
    static final List<String> LAUNCH_COUNTRY_NAMES = List.of("일본", "한국", "미국");
    static final int DEV_ADMIN_PASSWORD_MIN_LENGTH = 16;
    static final int DEV_ADMIN_PASSWORD_MAX_LENGTH = 32;

    @Bean
    public CommandLineRunner loadData(DistilleryRepository distilleryRepository,
                                      AlcoholRepository alcoholRepository,
                                      DestinationRepository destinationRepository,
                                      CategoryRepository categoryRepository,
                                      TagRepository tagRepository,
                                      UserRepository userRepository,
                                      ReviewRepository reviewRepository,
                                      CountryRepository countryRepository,
                                      CityRepository cityRepository) {
        return args -> {
            Long[] ids = {1L, 2L, 3L ,4L, 5L, 6L, 7L, 8L, 9L, 10L};
            for(int i = 1; i <= LAUNCH_CATEGORY_NAMES.size(); i++){
                Category categories = Category.builder()
                        .name(LAUNCH_CATEGORY_NAMES.get(i - 1))
                        .description("맥아 효소로 녹말을 포함하고 있는 곡물 재료를 당화시키고 발효[5] 및 증류하여 오크통에 숙성시킨 증류주. 간단히 말해서 목통숙성곡물증류주(木桶熟成穀物蒸溜酒)라고 할 수 있다. " + i)
                        .build();
                categoryRepository.save(categories);
            }

            for(int i = 1; i <= LAUNCH_COUNTRY_NAMES.size(); i++){
                Country country = Country.builder()
                        .name(LAUNCH_COUNTRY_NAMES.get(i - 1))
                        .description("국가 설명 " + i)
                        .build();
                countryRepository.save(country);
            }

            Country japan = countryRepository.findByName("일본");
            if (japan == null) {
                throw new IllegalStateException("일본 국가 seed가 필요합니다.");
            }
            City city = City.builder()
                    .name("오사카")
                    .description("일본 오사카부의 현청 소재지이자, 일본 제2의 도시")
                    .country(japan)
                    .build();
            cityRepository.save(city);

            Destination destination = Destination.builder()
                    .name("오야마자키")
                    .description("오야마자키정은 교토부 오토쿠니군의 정이다. 오사카부 미시마군 시마모토정과 접한다. 교토부에서 가장 면적의 작은 정이다. 옛 야마시로국의 오토쿠니군에 속했다")
                    .imgUrl("https://upload.wikimedia.org/wikipedia/commons/8/80/Oyamazaki_stn.jpg")
                    .latitude(34.9023747484815d)
                    .longitude(135.68551060039104d)
                    .city(cityRepository.findById(1L).get())
                    .build();

            destinationRepository.save(destination);

            Distillery distillery = Distillery.builder()
                    .name("Yamazaki Distillery")
                    .address("5 Chome-2-1 Yamazaki, Shimamoto, Mishima District, Osaka 618-0001 일본")
                    .description("Distillery Description ")
                    .imgUrl("https://upload.wikimedia.org/wikipedia/commons/f/f0/Yamazaki_Distillery_%E5%B1%B1%E5%B4%8E%E8%92%B8%E7%95%99%E6%89%8005.jpg")
                    .destination(destinationRepository.findById(1L).get())
                    .latitude(34.89279866199688d)
                    .longitude(135.6744575716837d)
                    .build();
            distilleryRepository.save(distillery);

            Distillery distilleryAsahi = Distillery.builder()
                    .name("Asahi Beer Ōyamazaki Villa Museum of Art")
                    .address("일본 〒618-0071 Kyoto, Otokuni District, Oyamazaki, Zenihara−５−3")
                    .description("Distillery Description ")
                    .imgUrl("https://upload.wikimedia.org/wikipedia/commons/f/f0/Yamazaki_Distillery_%E5%B1%B1%E5%B4%8E%E8%92%B8%E7%95%99%E6%89%8005.jpg")
                    .destination(destinationRepository.findById(1L).get())
                    .latitude(34.89564237533231d)
                    .longitude(135.6797393092974d)
                    .build();
            distilleryRepository.save(distilleryAsahi);

            for (int i = 1; i <= 1000; i++) {
                Destination destinations = Destination.builder()
                        .name("Destination " + i)
                        .description("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book." + i)
                        .imgUrl("https://upload.wikimedia.org/wikipedia/commons/e/ea/Taipei_Skyline_2022.06.29.jpg")
                        .latitude(37.5665d)
                        .longitude(126.9780d)
                        .city(cityRepository.findById(1L).get())
                        .build();

                destinationRepository.save(destinations);

                Distillery distilleries = Distillery.builder()
                        .name("Distillery " + i)
                        .address("Address " + i)
                        .description("Distillery Description " + i)
                        .imgUrl("https://upload.wikimedia.org/wikipedia/commons/f/f0/Yamazaki_Distillery_%E5%B1%B1%E5%B4%8E%E8%92%B8%E7%95%99%E6%89%8005.jpg")
                        .destination(destinationRepository.findById((long) i).get())
                        .latitude(37.5665d)
                        .longitude(126.9780d)
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
                        .category(categoryRepository.findById(ids[i%10]).get())
                        .imgUrl("https://upload.wikimedia.org/wikipedia/commons/e/e5/Jim_Beam_White_Label.jpg")
                        .build();
                alcoholRepository.save(alcohols);

                User user = new User();
                user.setNickname("User" + i);
                user.setEmail("User" + i + "@example.com");
                user.setPassword("password");

                userRepository.save(user);

                Review reviews = Review.builder()
                        .rating((byte) 5)
                        .itemType(ItemType.ALCOHOL)
                        .targetId((long) i)
                        .contents("리버시블이라활용도가좋네요.어느정도두께감이있어서따뜻할것같아요.플리스촉감도좋고,부드럽네요.착용해보니양쪽다무난하니다예쁘네요.블랙색상이라때도안타고좋아요.무난해서암때나착용가능해서좋아요.\n" +
                                "\n" + i)
                        .user(userRepository.findById((long) i).get())
                        .build();
                reviewRepository.save(reviews);
            }

        };
    }

    @Bean
    @ConditionalOnProperty(name = "app.dev-admin.enabled", havingValue = "true")
    public CommandLineRunner loadDevAdmin(UserRepository userRepository,
                                          AuthorityRepository authorityRepository,
                                          BCryptPasswordEncoder passwordEncoder,
                                          @Value("${app.dev-admin.email:}") String email,
                                          @Value("${app.dev-admin.password:}") String password,
                                          @Value("${app.dev-admin.nickname:local-admin}") String nickname) {
        return args -> {
            String normalizedEmail = requireText(email, "app.dev-admin.email");
            String normalizedPassword = requireText(password, "app.dev-admin.password");
            if (normalizedPassword.length() < DEV_ADMIN_PASSWORD_MIN_LENGTH
                    || normalizedPassword.length() > DEV_ADMIN_PASSWORD_MAX_LENGTH) {
                throw new IllegalStateException("app.dev-admin.password must be 16 to 32 characters.");
            }

            Authority adminAuthority = authorityRepository.findByName(UserRole.ADMIN)
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(UserRole.ADMIN).build()));

            User admin = userRepository.findByEmail(normalizedEmail).orElseGet(User::new);
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(normalizedPassword));
            admin.setNickname(requireText(nickname, "app.dev-admin.nickname"));
            admin.setEmailVerified(true);
            admin.setLocked(false);
            if (admin.getCreatedAt() == null) {
                admin.setCreatedAt(new Date());
            }
            admin.setRoles(new HashSet<>(Set.of(adminAuthority)));

            userRepository.save(admin);
        };
    }

    static String requireText(String value, String propertyName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(propertyName + " is required when app.dev-admin.enabled=true.");
        }
        return value.trim();
    }
}
