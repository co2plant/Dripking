package kr.co.inntavern.dripking.controller;

import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:user-controller-auth-flow-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret-key=test-only-change-me-test-only-change-me-test-only-change-me",
        "security.jwt.expiration-time=86400000"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerAuthFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void signupIgnoresRequestedAdminRoleAndSigninStatusUsesBearerToken() throws Exception {
        String signupBody = """
                {
                  "email": "auth-flow@example.com",
                  "password": "passwordValue001",
                  "confirmPassword": "passwordValue001",
                  "nickname": "auth-flow",
                  "agreeToTerms": true,
                  "userRole": "ADMIN"
                }
                """;

        mockMvc.perform(post("/api/user/signup")
                        .contentType("application/json")
                        .content(signupBody))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("auth-flow@example.com").orElseThrow();
        assertThat(user.getRoles())
                .extracting(authority -> authority.getName())
                .containsExactly(UserRole.USER);

        String signinBody = """
                {
                  "email": "auth-flow@example.com",
                  "password": "passwordValue001"
                }
                """;

        MvcResult signinResult = mockMvc.perform(post("/api/user/signin")
                        .contentType("application/json")
                        .content(signinBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Authorization"))
                .andReturn();

        String authorization = signinResult.getResponse().getHeader("Authorization");
        assertThat(authorization).startsWith("Bearer ");

        mockMvc.perform(get("/api/user/status")
                        .header("Authorization", authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("auth-flow@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("auth-flow"))
                .andExpect(jsonPath("$.data.roles", hasItem("ROLE_USER")));
    }

    @Test
    void signupValidationReturnsStableFieldErrors() throws Exception {
        String signupBody = """
                {
                  "email": "not-an-email",
                  "password": "short",
                  "confirmPassword": "different",
                  "nickname": "",
                  "agreeToTerms": false
                }
                """;

        mockMvc.perform(post("/api/user/signup")
                        .contentType("application/json")
                        .content(signupBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists())
                .andExpect(jsonPath("$.fieldErrors.confirmPassword").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.fieldErrors.nickname").exists())
                .andExpect(jsonPath("$.fieldErrors.agreeToTerms").exists());
    }
}
