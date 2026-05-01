package com.example.bookplan.user;

import com.example.bookplan.user.dto.UserCreateRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickName;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    protected User() {}

    private User(String name, String email, String nickName, String password) {
        this.name = name;
        this.email = email;
        this.nickName = nickName;
        this.password = password;
    }

    public static User from(UserCreateRequest request, String encodedPassword) {
        return new User(
                request.getName(),
                request.getEmail(),
                request.getNickName(),
                encodedPassword
        );
    }
}
