package kr.co.inntavern.dripking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DevAdminLoaderTest {

    @Test
    void requireTextTrimsConfiguredDevAdminValues() {
        assertThat(DevAdminLoader.requireText(" admin@example.com ", "app.dev-admin.email"))
                .isEqualTo("admin@example.com");
    }

    @Test
    void requireTextRejectsBlankDevAdminValues() {
        assertThatThrownBy(() -> DevAdminLoader.requireText(" ", "app.dev-admin.email"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("app.dev-admin.email");
    }
}
