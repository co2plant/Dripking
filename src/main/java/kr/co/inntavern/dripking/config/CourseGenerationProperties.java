package kr.co.inntavern.dripking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.course-generation")
public class CourseGenerationProperties {
    private Captcha captcha = new Captcha();
    private Trial trial = new Trial();
    private RateLimit rateLimit = new RateLimit();
    private Cost cost = new Cost();

    @Getter
    @Setter
    public static class Captcha {
        private boolean enabled = true;
        private String secret = "";
        private String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";
        private String devToken = "";
    }

    @Getter
    @Setter
    public static class Trial {
        private int guestLimit = 2;
    }

    @Getter
    @Setter
    public static class RateLimit {
        private boolean enabled = true;
        private int windowSeconds = 60;
        private int authenticatedLimit = 20;
        private int guestLimit = 5;
        private int ipLimit = 30;
        private int globalLimit = 100;
    }

    @Getter
    @Setter
    public static class Cost {
        private boolean hardCapEnabled = true;
        private BigDecimal estimatedCostPerGeneration = BigDecimal.ZERO;
        private BigDecimal monthlyHardCap = BigDecimal.valueOf(5000);
    }
}
