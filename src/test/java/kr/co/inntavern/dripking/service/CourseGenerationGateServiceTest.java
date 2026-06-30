package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.config.CourseGenerationProperties;
import kr.co.inntavern.dripking.dto.request.CourseGenerateRequestDTO;
import kr.co.inntavern.dripking.model.GenerationLog;
import kr.co.inntavern.dripking.repository.GenerationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseGenerationGateServiceTest {
    @Mock
    private GenerationLogRepository generationLogRepository;

    @Mock
    private CaptchaVerificationService captchaVerificationService;

    @Mock
    private CourseGenerationRateLimitService rateLimitService;

    private CourseGenerationProperties properties;
    private CourseGenerationGateService gateService;

    @BeforeEach
    void setUp() {
        properties = new CourseGenerationProperties();
        properties.getTrial().setGuestLimit(2);
        properties.getCost().setHardCapEnabled(true);
        properties.getCost().setEstimatedCostPerGeneration(new BigDecimal("1.25"));
        properties.getCost().setMonthlyHardCap(new BigDecimal("5000"));
        gateService = new CourseGenerationGateService(
                generationLogRepository,
                captchaVerificationService,
                rateLimitService,
                properties
        );
    }

    @Test
    void beforeGenerateAllowsGuestWhenTrialAndCaptchaPass() {
        CourseGenerateRequestDTO requestDTO = request("anon-1", "captcha-token");
        when(generationLogRepository.countByAnonId("anon-1")).thenReturn(1L);
        when(generationLogRepository.sumEstCostBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("100"));

        CourseGenerationGateService.GateContext gateContext =
                gateService.beforeGenerate(null, "127.0.0.1", requestDTO);

        assertThat(gateContext.anonId()).isEqualTo("anon-1");
        assertThat(gateContext.trialLimit()).isEqualTo(2);
        assertThat(gateContext.usedTrialCount()).isEqualTo(1);
        assertThat(gateContext.estimatedCost()).isEqualByComparingTo("1.25");
        verify(captchaVerificationService).verify("captcha-token");
        verify(rateLimitService).checkGenerate(null, "anon-1", "127.0.0.1");
    }

    @Test
    void beforeGenerateRejectsGuestWhenTrialIsExhausted() {
        CourseGenerateRequestDTO requestDTO = request("anon-1", "captcha-token");
        when(generationLogRepository.countByAnonId("anon-1")).thenReturn(2L);

        assertThatThrownBy(() -> gateService.beforeGenerate(null, "127.0.0.1", requestDTO))
                .isInstanceOfSatisfying(CourseGenerationGateException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(exception.getCode()).isEqualTo("ANON_TRIAL_EXHAUSTED");
                    assertThat(exception.getDetail()).containsEntry("remainingTrial", 0);
                });
    }

    @Test
    void beforeGenerateRejectsWhenMonthlyHardCapWouldBeExceeded() {
        CourseGenerateRequestDTO requestDTO = request("anon-1", "captcha-token");
        properties.getCost().setMonthlyHardCap(new BigDecimal("101"));
        when(generationLogRepository.countByAnonId("anon-1")).thenReturn(0L);
        when(generationLogRepository.sumEstCostBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("100"));

        assertThatThrownBy(() -> gateService.beforeGenerate(null, "127.0.0.1", requestDTO))
                .isInstanceOfSatisfying(CourseGenerationGateException.class, exception -> {
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                    assertThat(exception.getCode()).isEqualTo("COURSE_GENERATION_HARDCAP_REACHED");
                });
    }

    @Test
    void recordGuestGenerationPersistsGenerationLogAndReturnsTrialStatus() {
        CourseGenerationGateService.GateContext gateContext =
                new CourseGenerationGateService.GateContext("anon-1", new BigDecimal("1.25"), 2, 1);

        CourseGenerationGateService.GuestTrialStatus status =
                gateService.recordGuestGeneration(gateContext, "draft-course", "input-hash");

        assertThat(status.trialLimit()).isEqualTo(2);
        assertThat(status.usedCount()).isEqualTo(2);
        assertThat(status.remainingCount()).isZero();

        ArgumentCaptor<GenerationLog> captor = ArgumentCaptor.forClass(GenerationLog.class);
        verify(generationLogRepository).save(captor.capture());
        GenerationLog generationLog = captor.getValue();
        assertThat(generationLog.getAnonId()).isEqualTo("anon-1");
        assertThat(generationLog.getCourseId()).isEqualTo("draft-course");
        assertThat(generationLog.getInputHash()).isEqualTo("input-hash");
        assertThat(generationLog.getEstCost()).isEqualByComparingTo("1.25");
        assertThat(generationLog.isSavedToTrip()).isFalse();
    }

    private CourseGenerateRequestDTO request(String anonId, String captchaToken) {
        CourseGenerateRequestDTO requestDTO = new CourseGenerateRequestDTO();
        requestDTO.setAnonId(anonId);
        requestDTO.setCaptchaToken(captchaToken);
        return requestDTO;
    }
}
