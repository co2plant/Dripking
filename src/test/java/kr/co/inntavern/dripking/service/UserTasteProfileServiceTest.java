package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.UserTasteProfileRequestDTO;
import kr.co.inntavern.dripking.dto.response.UserTasteProfileResponseDTO;
import kr.co.inntavern.dripking.model.Category;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.CategoryRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.repository.UserTasteProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:user-taste-profile-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class UserTasteProfileServiceTest {

    @Autowired
    private UserTasteProfileService userTasteProfileService;

    @Autowired
    private UserTasteProfileRepository userTasteProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getTasteProfileReturnsEmptyProfileWhenUserHasNoProfile() {
        User user = saveUser("taste-empty@example.com", "taste-empty");

        UserTasteProfileResponseDTO responseDTO = userTasteProfileService.getTasteProfile(user.getId());

        assertThat(responseDTO.getUserId()).isEqualTo(user.getId());
        assertThat(responseDTO.getCategories()).isEmpty();
        assertThat(responseDTO.getFlavorTags()).isEmpty();
        assertThat(responseDTO.getUpdatedAt()).isNull();
    }

    @Test
    void saveTasteProfileNormalizesAndPersistsProfile() {
        User user = saveUser("taste-save@example.com", "taste-save");
        Category whisky = saveCategory("위스키");
        Category beer = saveCategory("맥주");

        UserTasteProfileRequestDTO requestDTO = request(
                List.of(whisky.getId(), beer.getId(), whisky.getId()),
                List.of(" Peaty ", "Sweet", "peaty", " ")
        );

        UserTasteProfileResponseDTO responseDTO = userTasteProfileService.saveTasteProfile(user.getId(), requestDTO);

        assertThat(responseDTO.getUserId()).isEqualTo(user.getId());
        assertThat(responseDTO.getCategories()).containsExactly(whisky.getId(), beer.getId());
        assertThat(responseDTO.getFlavorTags()).containsExactly("peaty", "sweet");
        assertThat(responseDTO.getUpdatedAt()).isNotNull();
        assertThat(userTasteProfileRepository.findByUserId(user.getId())).isPresent();
    }

    @Test
    void saveTasteProfileReplacesExistingProfile() {
        User user = saveUser("taste-update@example.com", "taste-update");
        Category whisky = saveCategory("위스키");
        Category beer = saveCategory("맥주");

        userTasteProfileService.saveTasteProfile(user.getId(), request(List.of(whisky.getId()), List.of("peaty")));
        UserTasteProfileResponseDTO responseDTO = userTasteProfileService.saveTasteProfile(user.getId(), request(List.of(beer.getId()), List.of("fresh")));

        assertThat(responseDTO.getCategories()).containsExactly(beer.getId());
        assertThat(responseDTO.getFlavorTags()).containsExactly("fresh");
        assertThat(userTasteProfileRepository.findAll()).hasSize(1);
    }

    @Test
    void saveTasteProfileRejectsMissingCategory() {
        User user = saveUser("taste-missing-category@example.com", "taste-missing-category");

        assertThatThrownBy(() -> userTasteProfileService.saveTasteProfile(user.getId(), request(List.of(999L), List.of("peaty"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 카테고리");
    }

    @Test
    void saveTasteProfileRejectsEmptyProfile() {
        User user = saveUser("taste-empty-request@example.com", "taste-empty-request");

        assertThatThrownBy(() -> userTasteProfileService.saveTasteProfile(user.getId(), request(List.of(), List.of(" "))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("하나 이상 선택");
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

    private Category saveCategory(String name) {
        return categoryRepository.save(Category.builder()
                .name(name)
                .description(name)
                .build());
    }

    private UserTasteProfileRequestDTO request(List<Long> categories, List<String> flavorTags) {
        UserTasteProfileRequestDTO requestDTO = new UserTasteProfileRequestDTO();
        requestDTO.setCategories(categories);
        requestDTO.setFlavorTags(flavorTags);
        return requestDTO;
    }
}
