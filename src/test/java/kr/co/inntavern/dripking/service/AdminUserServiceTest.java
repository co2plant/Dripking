package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.dto.request.AdminUserRoleRequestDTO;
import kr.co.inntavern.dripking.dto.request.AdminUserStatusRequestDTO;
import kr.co.inntavern.dripking.dto.response.AdminUserResponseDTO;
import kr.co.inntavern.dripking.model.Authority;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.AuthorityRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:admin-user-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@ActiveProfiles("test")
@Transactional
class AdminUserServiceTest {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    void getUsersSupportsSearchAndLockedFilter() {
        saveUser("locked@example.com", "locked-member", UserRole.USER, true);
        saveUser("active@example.com", "active-member", UserRole.USER, false);

        assertThat(adminUserService.getUsers(0, 10, "locked", true).getContent())
                .extracting(AdminUserResponseDTO::getEmail)
                .containsExactly("locked@example.com");
        assertThat(adminUserService.getUsers(0, 10, "member", false).getContent())
                .extracting(AdminUserResponseDTO::getEmail)
                .containsExactly("active@example.com");
    }

    @Test
    void updateUserRoleCreatesAuthorityAndReplacesRoles() {
        User admin = saveUser("admin@example.com", "admin", UserRole.ADMIN, false);
        User member = saveUser("member@example.com", "member", UserRole.USER, false);
        AdminUserRoleRequestDTO requestDTO = roleRequest(UserRole.ADMIN);

        AdminUserResponseDTO responseDTO = adminUserService.updateUserRole(member.getId(), admin.getId(), requestDTO);

        assertThat(responseDTO.getRoles()).containsExactly("ROLE_ADMIN");
        assertThat(userRepository.findById(member.getId()).orElseThrow().getRoles())
                .extracting(role -> role.getName().getValue())
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void currentAdminCannotSelfDemoteSelfLockOrSelfDelete() {
        User admin = saveUser("self-admin@example.com", "self-admin", UserRole.ADMIN, false);

        assertThatThrownBy(() -> adminUserService.updateUserRole(admin.getId(), admin.getId(), roleRequest(UserRole.USER)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("관리자 권한");
        assertThatThrownBy(() -> adminUserService.updateUserStatus(admin.getId(), admin.getId(), statusRequest(true)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잠글 수 없습니다");
        assertThatThrownBy(() -> adminUserService.deleteUser(admin.getId(), admin.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제할 수 없습니다");

        User persistedAdmin = userRepository.findById(admin.getId()).orElseThrow();
        assertThat(persistedAdmin.isLocked()).isFalse();
        assertThat(persistedAdmin.getRoles())
                .extracting(role -> role.getName().getValue())
                .containsExactly("ROLE_ADMIN");
    }

    private User saveUser(String email, String nickname, UserRole role, boolean locked) {
        Authority authority = authorityRepository.findByName(role)
                .orElseGet(() -> authorityRepository.save(Authority.builder().name(role).build()));
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setPassword("encoded-password");
        user.setLocked(locked);
        user.setEmailVerified(true);
        user.getRoles().add(authority);
        return userRepository.save(user);
    }

    private AdminUserRoleRequestDTO roleRequest(UserRole role) {
        AdminUserRoleRequestDTO requestDTO = new AdminUserRoleRequestDTO();
        requestDTO.setRole(role);
        return requestDTO;
    }

    private AdminUserStatusRequestDTO statusRequest(boolean locked) {
        AdminUserStatusRequestDTO requestDTO = new AdminUserStatusRequestDTO();
        requestDTO.setLocked(locked);
        return requestDTO;
    }
}
