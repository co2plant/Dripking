package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.service.InsufficientCreditException;
import kr.co.inntavern.dripking.service.CourseGenerationGateException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    @Test
    void insufficientCreditUsesPaymentRequiredAndStableErrorCode() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<Map<String, Object>> response = handler.handleInsufficientCreditException(
                new InsufficientCreditException(10, 5)
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        assertThat(response.getBody()).isNotNull();
        Map<?, ?> error = (Map<?, ?>) response.getBody().get("error");
        assertThat(error.get("code")).isEqualTo("INSUFFICIENT_CREDIT");
        assertThat(error.get("message")).isEqualTo("크레딧이 부족합니다.");
        @SuppressWarnings("unchecked")
        Map<String, Object> detail = (Map<String, Object>) error.get("detail");
        assertThat(detail)
                .containsEntry("requiredCredit", 10)
                .containsEntry("currentBalance", 5);
    }

    @Test
    void courseGenerationGateExceptionUsesExceptionStatusAndStableErrorCode() {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<Map<String, Object>> response = handler.handleCourseGenerationGateException(
                CourseGenerationGateException.rateLimitExceeded("anon", 30)
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getBody()).isNotNull();
        Map<?, ?> error = (Map<?, ?>) response.getBody().get("error");
        assertThat(error.get("code")).isEqualTo("RATE_LIMIT_EXCEEDED");
        assertThat(error.get("message")).isEqualTo("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
        @SuppressWarnings("unchecked")
        Map<String, Object> detail = (Map<String, Object>) error.get("detail");
        assertThat(detail)
                .containsEntry("scope", "anon")
                .containsEntry("retryAfterSeconds", 30L);
    }
}
