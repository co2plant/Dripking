package kr.co.inntavern.dripking;

import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.AuthorityRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Profile("dev")
public class DevAdminLoader {
    static final int DEV_ADMIN_PASSWORD_MIN_LENGTH = 16;
    static final int DEV_ADMIN_PASSWORD_MAX_LENGTH = 32;

    @Bean
    @ConditionalOnProperty(name = "app.dev-admin.enabled", havingValue = "true")
    public CommandLineRunner loadDevAdmin(UserRepository userRepository,
                                          AuthorityRepository authorityRepository,
                                          BCryptPasswordEncoder passwordEncoder,
                                          @Value("${app.dev-admin.email:}") String email,
                                          @Value("${app.dev-admin.password:}") String password,
                                          @Value("${app.dev-admin.nickname:local-admin}") String nickname) {
        return args -> {
            String normalizedEmail = requireText(email, "app.dev-admin.email");
            String normalizedPassword = requireText(password, "app.dev-admin.password");
            if (normalizedPassword.length() < DEV_ADMIN_PASSWORD_MIN_LENGTH
                    || normalizedPassword.length() > DEV_ADMIN_PASSWORD_MAX_LENGTH) {
                throw new IllegalStateException("app.dev-admin.password must be 16 to 32 characters.");
            }

            Authority adminAuthority = authorityRepository.findByName(UserRole.ADMIN)
                    .orElseGet(() -> authorityRepository.save(Authority.builder().name(UserRole.ADMIN).build()));

            User admin = userRepository.findByEmail(normalizedEmail).orElseGet(User::new);
            admin.setEmail(normalizedEmail);
            admin.setPassword(passwordEncoder.encode(normalizedPassword));
            admin.setNickname(requireText(nickname, "app.dev-admin.nickname"));
            admin.setEmailVerified(true);
            admin.setLocked(false);
            if (admin.getCreatedAt() == null) {
                admin.setCreatedAt(new Date());
            }
            admin.setRoles(new HashSet<>(Set.of(adminAuthority)));

            userRepository.save(admin);
        };
    }

    static String requireText(String value, String propertyName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(propertyName + " is required when app.dev-admin.enabled=true.");
        }
        return value.trim();
    }
}
