package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.User;
import kr.co.inntavern.dripking.dto.request.UserProfileUpdateRequestDTO;
import kr.co.inntavern.dripking.repository.AuthorityRepository;
import kr.co.inntavern.dripking.repository.UserRepository;
import kr.co.inntavern.dripking.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String EMAIL = "member@example.com";
    private static final String CURRENT_PASSWORD = "currentPassword16";

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AuthorityRepository authorityRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder, customUserDetailsService, authorityRepository);
    }

    @Test
    void changePasswordRejectsWrongCurrentPassword() {
        User user = userWithPassword(CURRENT_PASSWORD);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.changePassword(EMAIL, "wrongPassword16", "newPasswordValue16"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 비밀번호가 일치하지 않습니다.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfileRejectsDuplicateNickname() {
        User user = userWithPassword(CURRENT_PASSWORD);
        user.setNickname("oldNick");
        UserProfileUpdateRequestDTO requestDTO = profileUpdateRequest("newNick");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(EMAIL, requestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateProfileAllowsSameNicknameAndTrimsValue() {
        User user = userWithPassword(CURRENT_PASSWORD);
        user.setNickname("oldNick");
        UserProfileUpdateRequestDTO requestDTO = profileUpdateRequest(" oldNick ");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateProfile(EMAIL, requestDTO);

        assertThat(updatedUser.getNickname()).isEqualTo("oldNick");
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsSamePassword() {
        User user = userWithPassword(CURRENT_PASSWORD);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.changePassword(EMAIL, CURRENT_PASSWORD, CURRENT_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새 비밀번호는 현재 비밀번호와 달라야 합니다.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePasswordAcceptsMvpLengthPolicyWithoutCompositionRule() {
        User user = userWithPassword(CURRENT_PASSWORD);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        userService.changePassword(EMAIL, CURRENT_PASSWORD, "plainPasswordValue16");

        assertThat(passwordEncoder.matches("plainPasswordValue16", user.getPassword())).isTrue();
        verify(userRepository).save(user);
    }

    private User userWithPassword(String password) {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    private UserProfileUpdateRequestDTO profileUpdateRequest(String nickname) {
        UserProfileUpdateRequestDTO requestDTO = new UserProfileUpdateRequestDTO();
        requestDTO.setNickname(nickname);
        return requestDTO;
    }
}
