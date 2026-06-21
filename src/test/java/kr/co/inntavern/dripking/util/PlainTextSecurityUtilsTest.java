package kr.co.inntavern.dripking.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlainTextSecurityUtilsTest {

    @Test
    void normalizesSafePlainText() {
        String normalized = PlainTextSecurityUtils.validateAndNormalize(
                "  막걸리\t향이\r\n좋음  ",
                PlainTextSecurityUtils.REVIEW_CONTENTS
        );

        assertThat(normalized).isEqualTo("막걸리 향이\n좋음");
    }

    @Test
    void rejectsXssPayloads() {
        assertThatThrownBy(() -> PlainTextSecurityUtils.validateAndNormalize(
                "<script>alert(1)</script>",
                PlainTextSecurityUtils.REVIEW_CONTENTS
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XSS 위험 패턴");
    }

    @Test
    void allowsSqlLikePlainText() {
        String normalized = PlainTextSecurityUtils.validateAndNormalize(
                "  ' OR 1=1 -- 는 SQL 예시로 자주 보입니다.  ",
                PlainTextSecurityUtils.REVIEW_CONTENTS
        );

        assertThat(normalized).isEqualTo("' OR 1=1 -- 는 SQL 예시로 자주 보입니다.");
    }

    @Test
    void rejectsTooLongInputBeforeStorage() {
        String tooLong = "a".repeat(PlainTextSecurityUtils.REVIEW_REPORT_REASON.maxLength() + 1);

        assertThatThrownBy(() -> PlainTextSecurityUtils.validateAndNormalize(
                tooLong,
                PlainTextSecurityUtils.REVIEW_REPORT_REASON
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("자 이하");
    }
}
