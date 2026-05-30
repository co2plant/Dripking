package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.WishlistItemRequestDTO;
import kr.co.inntavern.dripking.dto.response.WishlistItemResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.City;
import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.Destination;
import kr.co.inntavern.dripking.model.Distillery;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.model.enumType.ItemType;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.CityRepository;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.DestinationRepository;
import kr.co.inntavern.dripking.repository.DistilleryRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.repository.WishlistItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:wishlist-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class WishlistServiceTest {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlcoholRepository alcoholRepository;

    @Autowired
    private DistilleryRepository distilleryRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void repeatedAddForSameUserTargetReturnsExistingWishlistItem() {
        User user = saveUser("wishlist-user@example.com", "wishlist-user");
        Alcohol alcohol = saveCatalog().alcohol();
        WishlistItemRequestDTO requestDTO = wishlistRequest(ItemType.ALCOHOL, alcohol.getId());

        WishlistItemResponseDTO firstResponse = wishlistService.addWishlistItem(user.getId(), requestDTO);
        WishlistItemResponseDTO secondResponse = wishlistService.addWishlistItem(user.getId(), requestDTO);

        assertThat(secondResponse.getWishlistItemId()).isEqualTo(firstResponse.getWishlistItemId());
        assertThat(secondResponse.getId()).isEqualTo(alcohol.getId());
        assertThat(secondResponse.getTargetId()).isEqualTo(alcohol.getId());
        assertThat(secondResponse.getItemType()).isEqualTo(ItemType.ALCOHOL);
        assertThat(secondResponse.getName()).isEqualTo("Single Malt");
        assertThat(secondResponse.getAddress()).isEqualTo("Yamazaki Distillery address");
        assertThat(wishlistItemRepository.findAllByUserIdOrderByCreatedAtAscIdAsc(user.getId())).hasSize(1);
    }

    @Test
    void wishlistSupportsCatalogTargetsAndRejectsCustomPlace() {
        User user = saveUser("wishlist-targets@example.com", "wishlist-targets");
        CatalogFixture catalog = saveCatalog();

        WishlistItemResponseDTO destinationItem = wishlistService.addWishlistItem(
                user.getId(),
                wishlistRequest(ItemType.DESTINATION, catalog.destination().getId())
        );
        WishlistItemResponseDTO distilleryItem = wishlistService.addWishlistItem(
                user.getId(),
                wishlistRequest(ItemType.DISTILLERY, catalog.distillery().getId())
        );

        assertThat(wishlistService.getWishlist(user.getId()))
                .extracting(WishlistItemResponseDTO::getWishlistItemId)
                .containsExactly(destinationItem.getWishlistItemId(), distilleryItem.getWishlistItemId());
        assertThat(destinationItem.getAddress()).isEqualTo("오사카, 일본");
        assertThat(distilleryItem.getAddress()).isEqualTo("Yamazaki Distillery address");

        assertThatThrownBy(() -> wishlistService.addWishlistItem(
                user.getId(),
                wishlistRequest(ItemType.CUSTOM_PLACE, 1L)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("추가할 수 없는 itemType");
    }

    @Test
    void wishlistRejectsMissingCatalogTarget() {
        User user = saveUser("wishlist-missing@example.com", "wishlist-missing");

        assertThatThrownBy(() -> wishlistService.addWishlistItem(
                user.getId(),
                wishlistRequest(ItemType.DESTINATION, 999L)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("여행지");
    }

    private User saveUser(String email, String nickname) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setPassword("encoded-password");
        user.setLocked(false);
        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    private WishlistItemRequestDTO wishlistRequest(ItemType itemType, Long targetId) {
        WishlistItemRequestDTO requestDTO = new WishlistItemRequestDTO();
        requestDTO.setItemType(itemType);
        requestDTO.setTargetId(targetId);
        return requestDTO;
    }

    private CatalogFixture saveCatalog() {
        Category category = categoryRepository.save(Category.builder()
                .name("위스키")
                .description("Whisky")
                .build());
        Country country = countryRepository.save(Country.builder()
                .name("일본")
                .description("Japan")
                .build());
        City city = cityRepository.save(City.builder()
                .name("오사카")
                .description("Osaka")
                .country(country)
                .build());
        Destination destination = destinationRepository.save(Destination.builder()
                .name("Yamazaki Area")
                .description("Destination")
                .imgUrl("https://example.com/destination.jpg")
                .city(city)
                .category(category)
                .latitude(34.9d)
                .longitude(135.6d)
                .build());
        Distillery distillery = distilleryRepository.save(Distillery.builder()
                .name("Yamazaki Distillery")
                .description("Distillery")
                .imgUrl("https://example.com/distillery.jpg")
                .address("Yamazaki Distillery address")
                .latitude(34.8d)
                .longitude(135.5d)
                .destination(destination)
                .build());
        Alcohol alcohol = alcoholRepository.save(Alcohol.builder()
                .name("Single Malt")
                .description("Alcohol")
                .imgUrl("https://example.com/alcohol.jpg")
                .category(category)
                .distillery(distillery)
                .strength(46.0f)
                .stated_age("12")
                .size(700.0f)
                .build());

        return new CatalogFixture(destination, distillery, alcohol);
    }

    private record CatalogFixture(Destination destination, Distillery distillery, Alcohol alcohol) {
    }
}
