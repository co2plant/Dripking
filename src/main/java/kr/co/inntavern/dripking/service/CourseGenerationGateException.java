package kr.co.inntavern.dripking.service;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class CourseGenerationGateException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final Map<String, Object> detail;

    public CourseGenerationGateException(HttpStatus status, String code, String message, Map<String, Object> detail) {
        super(message);
        this.status = status;
        this.code = code;
        this.detail = detail == null ? Map.of() : Map.copyOf(detail);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public static CourseGenerationGateException anonIdRequired() {
        return new CourseGenerationGateException(
                HttpStatus.UNAUTHORIZED,
                "ANON_ID_REQUIRED",
                "비로그인 체험 생성을 확인할 수 없습니다. 새로고침 후 다시 시도해주세요.",
                Map.of()
        );
    }

    public static CourseGenerationGateException anonTrialExhausted(int trialLimit, long usedCount) {
        return new CourseGenerationGateException(
                HttpStatus.UNAUTHORIZED,
                "ANON_TRIAL_EXHAUSTED",
                "비로그인 체험 생성 횟수를 모두 사용했습니다. 로그인 후 계속해주세요.",
                Map.of(
                        "trialLimit", trialLimit,
                        "usedCount", usedCount,
                        "remainingTrial", 0
                )
        );
    }

    public static CourseGenerationGateException captchaRequired() {
        return new CourseGenerationGateException(
                HttpStatus.UNAUTHORIZED,
                "CAPTCHA_REQUIRED",
                "비로그인 코스 생성은 CAPTCHA 확인이 필요합니다.",
                Map.of()
        );
    }

    public static CourseGenerationGateException captchaFailed() {
        return new CourseGenerationGateException(
                HttpStatus.UNAUTHORIZED,
                "CAPTCHA_FAILED",
                "CAPTCHA 확인에 실패했습니다. 다시 시도해주세요.",
                Map.of()
        );
    }

    public static CourseGenerationGateException rateLimitExceeded(String scope, long retryAfterSeconds) {
        return new CourseGenerationGateException(
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED",
                "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.",
                Map.of(
                        "scope", scope,
                        "retryAfterSeconds", retryAfterSeconds
                )
        );
    }

    public static CourseGenerationGateException hardCapReached(String monthlyHardCap, String currentMonthlyCost) {
        return new CourseGenerationGateException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "COURSE_GENERATION_HARDCAP_REACHED",
                "이번 달 생성 비용 한도에 도달해 코스 생성이 일시 중지되었습니다.",
                Map.of(
                        "monthlyHardCap", monthlyHardCap,
                        "currentMonthlyCost", currentMonthlyCost
                )
        );
    }
}
