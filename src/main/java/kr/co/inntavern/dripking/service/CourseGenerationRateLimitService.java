package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.config.CourseGenerationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseGenerationRateLimitService {
    private final CourseGenerationProperties properties;
    private final Clock clock;
    private final Map<String, WindowCounter> counters = new HashMap<>();

    @Autowired
    public CourseGenerationRateLimitService(CourseGenerationProperties properties) {
        this(properties, Clock.systemUTC());
    }

    CourseGenerationRateLimitService(CourseGenerationProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public synchronized void checkGenerate(Long userId, String anonId, String clientIp) {
        CourseGenerationProperties.RateLimit rateLimit = properties.getRateLimit();
        if (!rateLimit.isEnabled()) {
            return;
        }

        long nowEpochSecond = Instant.now(clock).getEpochSecond();
        int windowSeconds = Math.max(1, rateLimit.getWindowSeconds());
        List<LimitEntry> entries = new ArrayList<>();
        entries.add(new LimitEntry("global", "global", rateLimit.getGlobalLimit()));
        entries.add(new LimitEntry("ip:%s".formatted(normalizeKeyPart(clientIp, "unknown")), "ip", rateLimit.getIpLimit()));
        if (userId != null) {
            entries.add(new LimitEntry("user:%d".formatted(userId), "user", rateLimit.getAuthenticatedLimit()));
        } else {
            entries.add(new LimitEntry("anon:%s".formatted(normalizeKeyPart(anonId, "missing")), "anon", rateLimit.getGuestLimit()));
        }

        for (LimitEntry entry : entries) {
            if (entry.limit() <= 0) {
                continue;
            }
            WindowCounter counter = currentCounter(entry.key(), nowEpochSecond, windowSeconds);
            if (counter.count() >= entry.limit()) {
                long retryAfterSeconds = Math.max(1, windowSeconds - (nowEpochSecond - counter.windowStartEpochSecond()));
                throw CourseGenerationGateException.rateLimitExceeded(entry.scope(), retryAfterSeconds);
            }
        }

        for (LimitEntry entry : entries) {
            if (entry.limit() <= 0) {
                continue;
            }
            WindowCounter counter = currentCounter(entry.key(), nowEpochSecond, windowSeconds);
            counters.put(entry.key(), new WindowCounter(counter.windowStartEpochSecond(), counter.count() + 1));
        }
    }

    private WindowCounter currentCounter(String key, long nowEpochSecond, int windowSeconds) {
        WindowCounter counter = counters.get(key);
        if (counter == null || nowEpochSecond - counter.windowStartEpochSecond() >= windowSeconds) {
            return new WindowCounter(nowEpochSecond, 0);
        }
        return counter;
    }

    private String normalizeKeyPart(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private record LimitEntry(String key, String scope, int limit) {
    }

    private record WindowCounter(long windowStartEpochSecond, int count) {
    }
}
