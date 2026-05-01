package com.example.bookplan.auth.service;

import com.example.bookplan.auth.AuthService;
import com.example.bookplan.auth.dto.LogInRequest;
import com.example.bookplan.auth.dto.LogInResponse;
import com.example.bookplan.auth.exception.NotFoundEmailException;
import com.example.bookplan.auth.exception.WrongPasswordException;
import com.example.bookplan.auth.jwt.JwtTokenProvider;
import com.example.bookplan.user.User;
import com.example.bookplan.user.UserRepository;
import com.example.bookplan.user.dto.UserCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    AuthService service;

    @Test
    @DisplayName("로그인이 정상적으로 된다")
    void logIn() {
        LogInRequest request = LogInRequest.builder()
                .email("test@example.com")
                .password("test1234")
                .build();

        User mockUser = createUserWithId(1L, "test@example.com", "encoded-password");

        given(userRepository.findByEmail("test@example.com"))
                .willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("test1234", "encoded-password"))
                .willReturn(true);
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access-token-value");
        given(jwtTokenProvider.createRefreshToken(1L)).willReturn("refresh-token-value");

        LogInResponse response = service.logIn(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token-value");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-value");
    }

    @Test
    @DisplayName("존재하지 않는 이메일이면 404 에러가 발생한다")
    void validateEmail() {
        LogInRequest request = LogInRequest.builder()
                .email("test@example.com")
                .password("test1234")
                .build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.logIn(request))
                .isInstanceOf(NotFoundEmailException.class)
                .hasMessageContaining("존재하지 않는 이메일입니다: test@example.com");

    }

    @Test
    @DisplayName("잘못된 비밀번호면 401 에러가 발생한다")
    void validatePassword() {
        LogInRequest request = LogInRequest.builder()
                .email("test@example.com")
                .password("test1234")
                .build();

        User mockUser = createUserWithId(1L, "test@example.com", "encoded-password");

        given(userRepository.findByEmail("test@example.com"))
                .willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("test1234", "encoded-password"))
                .willReturn(false);

        assertThatThrownBy(() -> service.logIn(request))
                .isInstanceOf(WrongPasswordException.class)
                .hasMessageContaining("잘못된 비밀번호입니다");

    }

    private User createUserWithId(Long id, String email, String encodedPassword) {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("tester")
                .email(email)
                .nickName("tester")
                .password("raw-password")
                .build();
        User user = User.from(request, encodedPassword);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
