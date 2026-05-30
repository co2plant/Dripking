package kr.co.inntavern.dripking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.co.inntavern.dripking.dto.response.JwtTokenResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private static final String SECRET = "test-only-change-me-test-only-change-me-test-only-change-me";

    @Test
    void issuedAccessTokenUsesConfiguredExpirationTimeAndAuthoritiesClaim() {
        long expirationTime = 12_000L;
        JwtUtils jwtUtils = jwtUtils(expirationTime);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "member@example.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        JwtTokenResponseDTO tokenResponseDTO = jwtUtils.issueJwtToken(authentication);
        Claims claims = parse(tokenResponseDTO.getAccessToken());

        assertThat(claims.getSubject()).isEqualTo("member@example.com");
        assertThat(claims.get("auth")).isEqualTo("ROLE_USER");
        assertThat(claims.getExpiration().getTime() - claims.getIssuedAt().getTime()).isEqualTo(expirationTime);
        assertThat(jwtUtils.getAuthentication(tokenResponseDTO.getAccessToken()).getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void issuedRefreshTokenUsesSevenTimesConfiguredExpirationTime() {
        long expirationTime = 12_000L;
        JwtUtils jwtUtils = jwtUtils(expirationTime);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "member@example.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        JwtTokenResponseDTO tokenResponseDTO = jwtUtils.issueJwtToken(authentication);
        Claims claims = parse(tokenResponseDTO.getRefreshToken());

        assertThat(claims.getExpiration().getTime() - claims.getIssuedAt().getTime()).isEqualTo(expirationTime * 7);
    }

    private JwtUtils jwtUtils(long expirationTime) {
        JwtUtils jwtUtils = new JwtUtils(new JwtProperties(SECRET, expirationTime));
        jwtUtils.init();
        return jwtUtils;
    }

    private Claims parse(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
