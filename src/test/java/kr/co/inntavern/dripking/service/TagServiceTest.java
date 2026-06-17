package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.response.TagResponseDTO;
import kr.co.inntavern.dripking.model.Tag;
import kr.co.inntavern.dripking.model.enumType.TagGroup;
import kr.co.inntavern.dripking.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:tag-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void getAllTagsReturnsOnlyActiveTagsInGroupOrder() {
        saveTag("훈연", TagGroup.TASTING_AROMA, 2, true);
        saveTag("과일", TagGroup.TASTING_AROMA, 1, true);
        saveTag("숨김", TagGroup.TASTING_AROMA, 3, false);
        saveTag("단맛", TagGroup.TASTING_PALATE, 1, true);

        assertThat(tagService.getAllTags(TagGroup.TASTING_AROMA))
                .extracting(TagResponseDTO::getName)
                .containsExactly("과일", "훈연");

        assertThat(tagService.getAllTags(null))
                .extracting(TagResponseDTO::getName)
                .containsExactly("과일", "훈연", "단맛");
    }

    private void saveTag(String name, TagGroup group, int sortOrder, boolean active) {
        tagRepository.save(Tag.builder()
                .name(name)
                .description(name + " description")
                .tagGroup(group)
                .sortOrder(sortOrder)
                .active(active)
                .build());
    }
}
