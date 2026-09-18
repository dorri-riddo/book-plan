package com.example.bookplan.notification;

import com.example.bookplan.auth.Token;
import com.example.bookplan.auth.TokenRepository;
import com.example.bookplan.notification.dto.DeviceRegisterRequest;
import com.example.bookplan.notification.exception.NotFoundDeviceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final TokenRepository repository;

    public void registerDevice(Long userId, DeviceRegisterRequest request) {
        repository.releaseDeviceFromOtherUsers(request.getDeviceId(), userId);

        Token token = repository.findByUserIdAndDeviceId(userId, request.getDeviceId())
                .orElseThrow(() -> new NotFoundDeviceException(request.getDeviceId()));

        token.updatePushRegistration(request.getFcmToken(), request.getNotificationEnabled());
    }
}
