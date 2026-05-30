package kr.co.inntavern.dripking.config;

import kr.co.inntavern.dripking.model.Country;
import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.CountryRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:web-security-config-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void legacyDashboardEndpointsRequireAdminAuthority() throws Exception {
        mockMvc.perform(get("/api/dashboard/destinations"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/dashboard/destinations")
                        .with(user("member").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/dashboard/destinations?page=0&size=1")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void cityWritesRequireAdminAuthorityWhileReadsStayPublic() throws Exception {
        Country country = countryRepository.save(Country.builder()
                .name("Test Country")
                .description("Test country")
                .build());

        mockMvc.perform(get("/api/cities/country/{countryId}", country.getId()))
                .andExpect(status().isOk());

        String requestBody = """
                {
                  "name": "Test City",
                  "description": "Test city",
                  "countryId": %d
                }
                """.formatted(country.getId());

        mockMvc.perform(post("/api/cities")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/cities")
                        .with(user("member").authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/cities")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @CsvSource({
            "POST,/api/countries",
            "PUT,/api/countries/1",
            "DELETE,/api/countries/1",
            "POST,/api/cities",
            "PUT,/api/cities/1",
            "DELETE,/api/cities/1",
            "POST,/api/categories",
            "PUT,/api/categories/1",
            "DELETE,/api/categories/1",
            "POST,/api/destinations",
            "PUT,/api/destinations/1",
            "DELETE,/api/destinations/1",
            "POST,/api/alcohols",
            "PUT,/api/alcohols/1",
            "DELETE,/api/alcohols/1",
            "POST,/api/distilleries",
            "PUT,/api/distilleries/1",
            "DELETE,/api/distilleries/1",
            "GET,/api/admin/users",
            "PATCH,/api/admin/users/1/role",
            "PATCH,/api/admin/users/1/status",
            "DELETE,/api/admin/users/1",
            "POST,/api/admin/review-reports/1/resolve",
            "POST,/api/admin/reviews/1/hide",
            "DELETE,/api/admin/reviews/1",
            "POST,/api/admin/content-images"
    })
    void adminCatalogAndAdminNamespaceEndpointsRejectNonAdmins(String method, String path) throws Exception {
        mockMvc.perform(request(method, path))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(request(method, path)
                        .with(user("member").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void userProfileWritesRequireUserAuthority() throws Exception {
        User user = saveUser("profile-security@example.com", "profile-security", "currentPassword16");
        CustomUserDetails userDetails = userDetails(user);

        String requestBody = """
                {
                  "nickname": "profile-security-updated"
                }
                """;

        mockMvc.perform(patch("/api/user/profile")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(patch("/api/user/profile")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/user/profile")
                        .with(user(userDetails))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void userStatusAllowsAdminAuthorityForFrontendRouteGuard() throws Exception {
        User admin = saveUser("admin-status-security@example.com", "admin-status-security", "currentPassword16");
        CustomUserDetails adminDetails = userDetails(admin, "ROLE_ADMIN");

        mockMvc.perform(get("/api/user/status"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/user/status")
                        .with(user(adminDetails)))
                .andExpect(status().isOk());
    }

    @Test
    void userPasswordWritesRequireUserAuthority() throws Exception {
        User user = saveUser("password-security@example.com", "password-security", "currentPassword16");
        CustomUserDetails userDetails = userDetails(user);

        String requestBody = """
                {
                  "currentPassword": "currentPassword16",
                  "newPassword": "newPasswordValue16",
                  "confirmPassword": "newPasswordValue16"
                }
                """;

        mockMvc.perform(post("/api/user/changePassword")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/user/changePassword")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/user/changePassword")
                        .with(user(userDetails))
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    private User saveUser(String email, String nickname, String password) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setPassword(passwordEncoder.encode(password));
        user.setLocked(false);
        return userRepository.save(user);
    }

    private CustomUserDetails userDetails(User user) {
        return userDetails(user, "ROLE_USER");
    }

    private CustomUserDetails userDetails(User user, String authority) {
        return new CustomUserDetails(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(authority)),
                true
        );
    }

    private MockHttpServletRequestBuilder request(String method, String path) {
        String requestBody = "{}";
        return switch (method) {
            case "GET" -> get(path);
            case "POST" -> post(path).contentType("application/json").content(requestBody);
            case "PUT" -> put(path).contentType("application/json").content(requestBody);
            case "PATCH" -> patch(path).contentType("application/json").content(requestBody);
            case "DELETE" -> delete(path);
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }
}
