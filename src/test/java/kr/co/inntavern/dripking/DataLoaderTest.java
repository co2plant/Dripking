package kr.co.inntavern.dripking;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataLoaderTest {

    @Test
    void launchCategorySeedNamesMatchMvpScope() {
        assertThat(DataLoader.LAUNCH_CATEGORY_NAMES).containsExactly(
                "위스키",
                "럼",
                "보드카",
                "진",
                "데킬라",
                "브랜디",
                "리큐르",
                "맥주",
                "사케",
                "전통주"
        );
    }

    @Test
    void launchCountrySeedNamesMatchMvpScope() {
        assertThat(DataLoader.LAUNCH_COUNTRY_NAMES).isEqualTo(List.of("일본", "한국", "미국"));
    }

    @Test
    void requireTextTrimsConfiguredDevAdminValues() {
        assertThat(DataLoader.requireText(" admin@example.com ", "app.dev-admin.email"))
                .isEqualTo("admin@example.com");
    }

    @Test
    void requireTextRejectsBlankDevAdminValues() {
        assertThatThrownBy(() -> DataLoader.requireText(" ", "app.dev-admin.email"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("app.dev-admin.email");
    }
}
