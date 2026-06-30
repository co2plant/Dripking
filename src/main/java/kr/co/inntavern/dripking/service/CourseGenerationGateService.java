package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.config.CourseGenerationProperties;
import kr.co.inntavern.dripking.dto.request.CourseGenerateRequestDTO;
import kr.co.inntavern.dripking.model.GenerationLog;
import kr.co.inntavern.dripking.repository.GenerationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
public class CourseGenerationGateService {
    private final GenerationLogRepository generationLogRepository;
    private final CaptchaVerificationService captchaVerificationService;
    private final CourseGenerationRateLimitService rateLimitService;
    private final CourseGenerationProperties properties;

    public CourseGenerationGateService(GenerationLogRepository generationLogRepository,
                                       CaptchaVerificationService captchaVerificationService,
                                       CourseGenerationRateLimitService rateLimitService,
                                       CourseGenerationProperties properties) {
        this.generationLogRepository = generationLogRepository;
        this.captchaVerificationService = captchaVerificationService;
        this.rateLimitService = rateLimitService;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public GateContext beforeGenerate(Long userId, String clientIp, CourseGenerateRequestDTO requestDTO) {
        BigDecimal estimatedCost = normalizedCost(properties.getCost().getEstimatedCostPerGeneration());
        String anonId = null;
        int trialLimit = Math.max(0, properties.getTrial().getGuestLimit());
        int usedTrialCount = 0;

        if (userId == null) {
            anonId = normalizeAnonId(requestDTO == null ? null : requestDTO.getAnonId());
            if (anonId == null) {
                throw CourseGenerationGateException.anonIdRequired();
            }
            long persistedCount = generationLogRepository.countByAnonId(anonId);
            if (persistedCount >= trialLimit) {
                throw CourseGenerationGateException.anonTrialExhausted(trialLimit, persistedCount);
            }
            usedTrialCount = Math.toIntExact(persistedCount);
            captchaVerificationService.verify(requestDTO.getCaptchaToken());
        }

        rateLimitService.checkGenerate(userId, anonId, clientIp);
        checkMonthlyHardCap(estimatedCost);
        return new GateContext(anonId, estimatedCost, trialLimit, usedTrialCount);
    }

    @Transactional
    public GuestTrialStatus recordGuestGeneration(GateContext gateContext, String courseId, String inputHash) {
        if (gateContext == null || !gateContext.isGuest()) {
            return null;
        }

        GenerationLog generationLog = new GenerationLog();
        generationLog.setAnonId(gateContext.anonId());
        generationLog.setInputHash(inputHash);
        generationLog.setCourseId(courseId);
        generationLog.setTokensIn(0);
        generationLog.setTokensOut(0);
        generationLog.setEstCost(gateContext.estimatedCost());
        generationLog.setSavedToTrip(false);
        generationLogRepository.save(generationLog);

        int usedAfterSuccess = gateContext.usedTrialCount() + 1;
        int remaining = Math.max(0, gateContext.trialLimit() - usedAfterSuccess);
        return new GuestTrialStatus(gateContext.trialLimit(), usedAfterSuccess, remaining);
    }

    private void checkMonthlyHardCap(BigDecimal estimatedCost) {
        CourseGenerationProperties.Cost costProperties = properties.getCost();
        if (!costProperties.isHardCapEnabled()) {
            return;
        }

        BigDecimal monthlyHardCap = normalizedCost(costProperties.getMonthlyHardCap());
        if (monthlyHardCap.signum() <= 0) {
            return;
        }

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startAt = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endAt = currentMonth.plusMonths(1).atDay(1).atStartOfDay();
        BigDecimal currentCost = normalizedCost(generationLogRepository.sumEstCostBetween(startAt, endAt));
        if (currentCost.compareTo(monthlyHardCap) >= 0
                || currentCost.add(estimatedCost).compareTo(monthlyHardCap) > 0) {
            throw CourseGenerationGateException.hardCapReached(
                    monthlyHardCap.toPlainString(),
                    currentCost.toPlainString()
            );
        }
    }

    private BigDecimal normalizedCost(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.max(BigDecimal.ZERO);
    }

    private String normalizeAnonId(String anonId) {
        if (anonId == null || anonId.isBlank()) {
            return null;
        }
        String trimmed = anonId.trim();
        if (trimmed.length() > 64) {
            return trimmed.substring(0, 64);
        }
        return trimmed;
    }

    public record GateContext(String anonId, BigDecimal estimatedCost, int trialLimit, int usedTrialCount) {
        public boolean isGuest() {
            return anonId != null;
        }

        public static GateContext authenticated(BigDecimal estimatedCost) {
            return new GateContext(null, estimatedCost == null ? BigDecimal.ZERO : estimatedCost, 0, 0);
        }
    }

    public record GuestTrialStatus(int trialLimit, int usedCount, int remainingCount) {
    }
}
