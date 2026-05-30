package kr.co.inntavern.dripking.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthTokenFilterTest {

    private JwtUtils jwtUtils;
    private CustomUserDetailsService customUserDetailsService;
    private AuthTokenFilter authTokenFilter;

    @BeforeEach
    void setUp() {
        jwtUtils = mock(JwtUtils.class);
        customUserDetailsService = mock(CustomUserDetailsService.class);
        authTokenFilter = new AuthTokenFilter(jwtUtils, customUserDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validTokenForUnlockedUserSetsAuthentication() throws Exception {
        MockHttpServletRequest request = requestWithBearerToken("valid-token");
        when(jwtUtils.validateJwtToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-token")).thenReturn("member@example.com");
        when(customUserDetailsService.loadUserByUsername("member@example.com"))
                .thenReturn(User.withUsername("member@example.com")
                        .password("encoded-password")
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .build());

        authTokenFilter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("member@example.com");
    }

    @Test
    void validTokenForLockedUserDoesNotSetAuthentication() throws Exception {
        MockHttpServletRequest request = requestWithBearerToken("valid-token");
        when(jwtUtils.validateJwtToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-token")).thenReturn("locked@example.com");
        when(customUserDetailsService.loadUserByUsername("locked@example.com"))
                .thenReturn(User.withUsername("locked@example.com")
                        .password("encoded-password")
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .accountLocked(true)
                        .build());

        authTokenFilter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void invalidTokenIsTreatedAsSignedOut() throws Exception {
        MockHttpServletRequest request = requestWithBearerToken("expired-token");
        when(jwtUtils.validateJwtToken("expired-token")).thenThrow(new RuntimeException("JWT token is expired"));

        authTokenFilter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private MockHttpServletRequest requestWithBearerToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
