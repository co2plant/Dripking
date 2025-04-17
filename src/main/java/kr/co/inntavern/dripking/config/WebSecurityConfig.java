package kr.co.inntavern.dripking.config;

import kr.co.inntavern.dripking.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, customUserDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sessionManagementConfigurer
                        -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> registry
                        // --- 공개 접근 가능 API ---
                        .requestMatchers(HttpMethod.GET,
                                "/api/reviews/**", // 리뷰 조회
                                "/api/reviews",
                                "/api/countries", // 국가 조회
                                "/api/countries/**",
                                "/api/categories", // 카테고리 조회
                                "/api/categories/**",
                                "/api/destinations/**", // 여행지 조회
                                "/api/destinations",
                                "/api/alcohols/**", // 주류 조회
                                "/api/alcohols",
                                "/api/distilleries/**", // 증류소 조회
                                "/api/distilleries"
                        ).permitAll()
                        .requestMatchers(
                                "/api/user/signin", // 로그인
                                "/api/user/signup" // 회원가입
                        ).permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll() // H2 콘솔

                        // --- USER 역할 필요 API ---
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasAuthority("ROLE_USER") // 리뷰 작성
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").hasAuthority("ROLE_USER") // 리뷰 수정 (특정 리뷰 ID)
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAuthority("ROLE_USER") // 리뷰 삭제 (특정 리뷰 ID)
                        .requestMatchers("/api/plans", "/api/plans/**").hasAuthority("ROLE_USER") // 플랜 관련
                        .requestMatchers("/api/trips", "/api/trips/**").hasAuthority("ROLE_USER") // 여행 관련
                        .requestMatchers("/api/user/status").hasAuthority("ROLE_USER") // 사용자 상태 확인

                        // --- ADMIN 역할 필요 API (데이터 관리) ---
                        // POST (생성)
                        .requestMatchers(HttpMethod.POST, "/api/countries", "/api/categories", "/api/destinations", "/api/alcohols", "/api/distilleries").hasAuthority("ROLE_ADMIN")
                        // PUT (수정)
                        .requestMatchers(HttpMethod.PUT, "/api/countries/**", "/api/categories/**", "/api/destinations/**", "/api/alcohols/**", "/api/distilleries/**").hasAuthority("ROLE_ADMIN")
                        // DELETE (삭제)
                        .requestMatchers(HttpMethod.DELETE, "/api/countries/**", "/api/categories/**", "/api/destinations/**", "/api/alcohols/**", "/api/distilleries/**").hasAuthority("ROLE_ADMIN")

                        // --- 나머지 요청은 인증 필요 ---
                        .anyRequest().authenticated() // 위에서 정의되지 않은 모든 요청은 인증 필요
                ).headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)); // H2 콘솔 프레임 허용

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://dripking-front.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
