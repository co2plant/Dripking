package kr.co.inntavern.dripking.security;

import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void buildUsesPersistedRolesInsteadOfAdminEmailShortcut() {
        User user = new User();
        user.setEmail("admin");
        user.setPassword("encoded-password");
        user.setNickname("Admin Email Without Admin Role");
        user.getRoles().add(Authority.builder().name(UserRole.USER).build());

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER")
                .doesNotContain("ROLE_ADMIN");
    }

    @Test
    void buildGrantsAdminOnlyWhenPersistedRoleIsAdmin() {
        User user = new User();
        user.setEmail("trusted-admin@example.com");
        user.setPassword("encoded-password");
        user.setNickname("Trusted Admin");
        user.getRoles().add(Authority.builder().name(UserRole.ADMIN).build());

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void buildMapsLockedUserToLockedSecurityAccount() {
        User user = new User();
        user.setEmail("locked@example.com");
        user.setPassword("encoded-password");
        user.setNickname("Locked User");
        user.setLocked(true);
        user.getRoles().add(Authority.builder().name(UserRole.USER).build());

        CustomUserDetails userDetails = CustomUserDetails.build(user);

        assertThat(userDetails.isAccountNonLocked()).isFalse();
    }
}
