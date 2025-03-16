package kr.co.inntavern.dripking.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "security.jwt")
@AllArgsConstructor
public class JwtProperties {
    private String secretKey;
    private long expirationTime;
}
