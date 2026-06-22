package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.TastingNoteRequestDTO;
import kr.co.inntavern.dripking.dto.response.TastingNoteResponseDTO;
import kr.co.inntavern.dripking.model.Alcohol;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.AlcoholRepository;
import kr.co.inntavern.dripking.repository.TastingNoteRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:tasting-note-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class TastingNoteServiceTest {

    @Autowired
    private TastingNoteService tastingNoteService;

    @Autowired
    private TastingNoteRepository tastingNoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlcoholRepository alcoholRepository;

    @Test
    void createTastingNoteNormalizesAndPersistsNote() {
        User user = saveUser("tasting-create@example.com", "tasting-create");
        Alcohol alcohol = saveAlcohol("이강주");

        TastingNoteResponseDTO responseDTO = tastingNoteService.createTastingNote(
                user.getId(),
                request(alcohol.getId(), "  ", " 전주 여행 ", " 전 ", " 배 향이 좋았다 ")
        );

        assertThat(responseDTO.getUserId()).isEqualTo(user.getId());
        assertThat(responseDTO.getAlcoholId()).isEqualTo(alcohol.getId());
        assertThat(responseDTO.getAlcoholName()).isEqualTo("이강주");
        assertThat(responseDTO.getTastedAt()).isEqualTo(LocalDate.of(2026, 6, 22));
        assertThat(responseDTO.getPlace().getName()).isEqualTo("전주 여행");
        assertThat(responseDTO.getPlace().getLat()).isEqualByComparingTo("35.8151000");
        assertThat(responseDTO.getOverall()).isEqualTo(4);
        assertThat(responseDTO.getAromaTags()).containsExactly("배", "꿀");
        assertThat(responseDTO.getPairing()).isEqualTo("전");
        assertThat(responseDTO.getMemo()).isEqualTo("배 향이 좋았다");
        assertThat(tastingNoteRepository.findById(responseDTO.getId())).isPresent();
    }

    @Test
    void listTastingNotesReturnsOnlyOwnerNotes() {
        User owner = saveUser("tasting-owner@example.com", "tasting-owner");
        User other = saveUser("tasting-other@example.com", "tasting-other");

        TastingNoteResponseDTO ownerNote = tastingNoteService.createTastingNote(
                owner.getId(),
                request(null, "Owner bottle", "서울", null, "owner memo")
        );
        tastingNoteService.createTastingNote(
                other.getId(),
                request(null, "Other bottle", "부산", null, "other memo")
        );

        List<TastingNoteResponseDTO> notes = tastingNoteService.getTastingNotes(
                owner.getId(),
                null,
                null,
                0,
                20,
                "recent"
        ).getContent();

        assertThat(notes).extracting(TastingNoteResponseDTO::getId).containsExactly(ownerNote.getId());
    }

    @Test
    void updateTastingNoteRejectsNonOwner() {
        User owner = saveUser("tasting-owner-update@example.com", "tasting-owner-update");
        User other = saveUser("tasting-other-update@example.com", "tasting-other-update");
        TastingNoteResponseDTO note = tastingNoteService.createTastingNote(
                owner.getId(),
                request(null, "Owner bottle", "서울", null, "owner memo")
        );

        assertThatThrownBy(() -> tastingNoteService.updateTastingNote(
                other.getId(),
                note.getId(),
                request(null, "Other edit", "부산", null, "hijack")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("테이스팅 기록을 찾을 수 없습니다");
    }

    @Test
    void createTastingNoteRejectsXssMemo() {
        User user = saveUser("tasting-xss@example.com", "tasting-xss");
        TastingNoteRequestDTO requestDTO = request(null, "XSS bottle", "서울", null, "<script>alert(1)</script>");

        assertThatThrownBy(() -> tastingNoteService.createTastingNote(user.getId(), requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XSS 위험 패턴");
    }

    @Test
    void createTastingNoteRejectsInvalidCoordinates() {
        User user = saveUser("tasting-coordinate@example.com", "tasting-coordinate");
        TastingNoteRequestDTO requestDTO = request(null, "Coordinate bottle", "서울", null, "memo");
        requestDTO.getPlace().setLat(BigDecimal.valueOf(100));

        assertThatThrownBy(() -> tastingNoteService.createTastingNote(user.getId(), requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("위도");
    }

    @Test
    void deleteTastingNoteRemovesOwnedNote() {
        User user = saveUser("tasting-delete@example.com", "tasting-delete");
        TastingNoteResponseDTO note = tastingNoteService.createTastingNote(
                user.getId(),
                request(null, "Delete bottle", "서울", null, "memo")
        );

        tastingNoteService.deleteTastingNote(user.getId(), note.getId());

        assertThat(tastingNoteRepository.findById(note.getId())).isEmpty();
    }

    private TastingNoteRequestDTO request(Long alcoholId,
                                          String alcoholName,
                                          String placeName,
                                          String pairing,
                                          String memo) {
        TastingNoteRequestDTO requestDTO = new TastingNoteRequestDTO();
        requestDTO.setAlcoholId(alcoholId);
        requestDTO.setAlcoholName(alcoholName);
        requestDTO.setTastedAt(LocalDate.of(2026, 6, 22));

        TastingNoteRequestDTO.PlaceDTO placeDTO = new TastingNoteRequestDTO.PlaceDTO();
        placeDTO.setName(placeName);
        placeDTO.setLat(BigDecimal.valueOf(35.8151000));
        placeDTO.setLng(BigDecimal.valueOf(127.1534000));
        requestDTO.setPlace(placeDTO);

        TastingNoteRequestDTO.RatingsDTO ratingsDTO = new TastingNoteRequestDTO.RatingsDTO();
        ratingsDTO.setAppearance(4);
        ratingsDTO.setAroma(5);
        ratingsDTO.setPalate(4);
        ratingsDTO.setFinish(4);
        ratingsDTO.setOverall(4);
        requestDTO.setRatings(ratingsDTO);

        TastingNoteRequestDTO.TagsDTO tagsDTO = new TastingNoteRequestDTO.TagsDTO();
        tagsDTO.setAroma(List.of(" 배 ", "꿀", "배"));
        tagsDTO.setPalate(List.of("달콤함"));
        tagsDTO.setFinish(List.of("깔끔함"));
        requestDTO.setTags(tagsDTO);

        requestDTO.setPairing(pairing);
        requestDTO.setMemo(memo);
        return requestDTO;
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

    private Alcohol saveAlcohol(String name) {
        Alcohol alcohol = Alcohol.builder()
                .name(name)
                .description(name)
                .build();
        return alcoholRepository.save(alcohol);
    }
}
