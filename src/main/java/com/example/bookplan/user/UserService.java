package com.example.bookplan.user;

import com.example.bookplan.user.dto.UserCreateRequest;
import com.example.bookplan.user.exception.DuplicateEmailException;
import com.example.bookplan.user.exception.DuplicateNickNameException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public User create(UserCreateRequest request) {
        validateUniqueness(request);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = repository.save(User.from(request, encodedPassword));

        return user;
    }

    private void validateUniqueness(UserCreateRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        if (repository.existsByNickName(request.getNickName())) {
            throw new DuplicateNickNameException(request.getNickName());
        }
    }
}
