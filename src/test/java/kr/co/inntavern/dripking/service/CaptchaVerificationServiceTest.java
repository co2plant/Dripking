package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.config.CourseGenerationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CaptchaVerificationServiceTest {
    private CourseGenerationProperties properties;
    private CaptchaVerificationService captchaVerificationService;

    @BeforeEach
    void setUp() {
        properties = new CourseGenerationProperties();
        properties.getCaptcha().setEnabled(true);
        properties.getCaptcha().setDevToken("dev-captcha");
        captchaVerificationService = new CaptchaVerificationService(properties);
    }

    @Test
    void verifyAcceptsConfiguredDevToken() {
        captchaVerificationService.verify("dev-captcha");
    }

    @Test
    void verifyRejectsBlankToken() {
        assertThatThrownBy(() -> captchaVerificationService.verify(" "))
                .isInstanceOfSatisfying(CourseGenerationGateException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(exception.getCode()).isEqualTo("CAPTCHA_REQUIRED");
                });
    }

    @Test
    void verifyRejectsInvalidTokenWhenProviderSecretIsMissing() {
        assertThatThrownBy(() -> captchaVerificationService.verify("wrong-token"))
                .isInstanceOfSatisfying(CourseGenerationGateException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(exception.getCode()).isEqualTo("CAPTCHA_FAILED");
                });
    }
}
