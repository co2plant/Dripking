package kr.co.inntavern.dripking.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "jwt")
@AllArgsConstructor
public class JwtProperties {
    private String secret;
    private long expirationTime;
}
