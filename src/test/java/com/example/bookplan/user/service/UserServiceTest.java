package com.example.bookplan.user.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserService service;

    @Test
    @DisplayName("회원가입이 정상적으로 된다")
    void createUser() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("홍길동")
                .email("test@example.com")
                .nickName("test")
                .password("test1234")
                .build();

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.create(request);

        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("이메일이 중복되면 409 에러가 발생한다")
    void validateEmail() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("홍길동")
                .email("test@example.com")
                .nickName("test")
                .password("test1234")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("이미 등록된 이메일입니다: test@example.com");
    }

    @Test
    @DisplayName("닉네임이 중복되면 409 에러가 발생한다")
    void validateNickName() {
        UserCreateRequest request = UserCreateRequest.builder()
                .name("홍길동")
                .email("test@example.com")
                .nickName("test")
                .password("test1234")
                .build();

        when(userRepository.existsByNickName("test")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateNickNameException.class)
                .hasMessageContaining("이미 등록된 닉네임입니다: test");
    }
}
