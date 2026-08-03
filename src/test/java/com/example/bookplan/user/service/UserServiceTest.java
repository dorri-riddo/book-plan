package com.example.bookplan.user.service;

import com.example.bookplan.auth.AuthService;
import com.example.bookplan.auth.dto.LogInResponse;
import com.example.bookplan.user.User;
import com.example.bookplan.user.UserRepository;
import com.example.bookplan.user.UserService;
import com.example.bookplan.user.dto.UserCreateRequest;
import com.example.bookplan.user.exception.DuplicateEmailException;
import com.example.bookplan.user.exception.DuplicateNickNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthService authService;
    @InjectMocks
    UserService service;

    @Test
    @DisplayName("회원가입이 정상적으로 되고 토큰이 발급된다")
    void createUser() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("홍길동")
                .email("test@example.com")
                .nickName("test")
                .password("test1234")
                .deviceId("device-1")
                .build();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User saved = invocation.getArgument(0);
                    ReflectionTestUtils.setField(saved, "id", 1L);
                    return saved;
                });
        when(authService.issueTokens(1L, "device-1", false))
                .thenReturn(LogInResponse.builder()
                        .accessToken("access-token-value")
                        .refreshToken("refresh-token-value")
                        .build());

        LogInResponse response = service.create(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token-value");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-value");
    }

    @Test
    @DisplayName("이메일이 중복되면 409 에러가 발생한다")
    void validateEmail() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("홍길동")
                .email("test@example.com")
                .nickName("test")
                .password("test1234")
                .deviceId("device-1")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("이미 등록된 이메일입니다: test@example.com");

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("닉네임이 중복되면 409 에러가 발생한다")
    void validateNickName() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("홍길동")
                .email("test@example.com")
                .nickName("test")
                .password("test1234")
                .deviceId("device-1")
                .build();

        when(userRepository.existsByNickName("test")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateNickNameException.class)
                .hasMessageContaining("이미 등록된 닉네임입니다: test");

        verifyNoInteractions(authService);
    }
}
