package com.example.bookplan.auth;

import com.example.bookplan.auth.dto.LogInRequest;
import com.example.bookplan.auth.dto.LogInResponse;
import com.example.bookplan.auth.exception.NotFoundEmailException;
import com.example.bookplan.auth.exception.WrongPasswordException;
import com.example.bookplan.auth.jwt.JwtTokenProvider;
import com.example.bookplan.user.User;
import com.example.bookplan.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LogInResponse logIn(LogInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundEmailException(request.getEmail()));

        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (passwordMatch == false) {
            throw new WrongPasswordException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        LogInResponse response = LogInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return response;
    }
}
