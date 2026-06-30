package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.config.CourseGenerationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseGenerationRateLimitServiceTest {
    private CourseGenerationProperties properties;
    private CourseGenerationRateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        properties = new CourseGenerationProperties();
        properties.getRateLimit().setEnabled(true);
        properties.getRateLimit().setWindowSeconds(60);
        properties.getRateLimit().setGuestLimit(2);
        properties.getRateLimit().setIpLimit(0);
        properties.getRateLimit().setGlobalLimit(0);
        rateLimitService = new CourseGenerationRateLimitService(properties);
    }

    @Test
    void checkGenerateRejectsWhenGuestWindowLimitIsExceeded() {
        rateLimitService.checkGenerate(null, "anon-1", "127.0.0.1");
        rateLimitService.checkGenerate(null, "anon-1", "127.0.0.1");

        assertThatThrownBy(() -> rateLimitService.checkGenerate(null, "anon-1", "127.0.0.1"))
                .isInstanceOfSatisfying(CourseGenerationGateException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
                    assertThat(exception.getCode()).isEqualTo("RATE_LIMIT_EXCEEDED");
                    assertThat(exception.getDetail()).containsEntry("scope", "anon");
                });
    }
}
