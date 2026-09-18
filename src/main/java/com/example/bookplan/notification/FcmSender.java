package com.example.bookplan.notification;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FcmSender {
    private static final int BATCH_SIZE = 500;
    private static final long TTL_MILLIS = Duration.ofHours(6).toMillis();
    private static final String COLLAPSE_KEY = "daily_target";
    private final FirebaseMessaging messaging;

    public FcmSender(ObjectProvider<FirebaseMessaging> messagingProvider) {
        this.messaging = messagingProvider.getIfAvailable();
    }

    public boolean isEnabled() {
        return messaging != null;
    }

    public List<String> sendDailyTargets(List<DailyPushPayload> payloads) {
        if (!isEnabled() || payloads.isEmpty()) {
            return List.of();
        }

        List<String> invalidTokens = new ArrayList<>();
        for (int start = 0; start < payloads.size(); start += BATCH_SIZE) {
            List<DailyPushPayload> chunk =
                    payloads.subList(start, Math.min(start + BATCH_SIZE, payloads.size()));
            invalidTokens.addAll(sendChunk(chunk));
        }
        return invalidTokens;
    }

    private List<String> sendChunk(List<DailyPushPayload> chunk) {
        List<Message> messages = chunk.stream().map(this::toMessage).toList();

        BatchResponse batchResponse;
        try {
            batchResponse = messaging.sendEach(messages);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 발송 실패 ({}건)", chunk.size(), e);
            return List.of();
        }

        List<String> invalidTokens = new ArrayList<>();
        List<SendResponse> responses = batchResponse.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            SendResponse response = responses.get(i);
            if (response.isSuccessful()) {
                continue;
            }

            String fcmToken = chunk.get(i).fcmToken();
            MessagingErrorCode errorCode = response.getException().getMessagingErrorCode();
            if (errorCode == MessagingErrorCode.UNREGISTERED
                    || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                invalidTokens.add(fcmToken);
            } else {
                log.warn("FCM 발송 실패 (errorCode={})", errorCode);
            }
        }

        if (invalidTokens.size() == chunk.size() && chunk.size() > 1) {
            log.error("묶음 전체가 실패했습니다. ({}건)",
                    chunk.size());
            return List.of();
        }

        log.info("FCM 발송: 성공 {}건, 실패 {}건, 정리 대상 토큰 {}개",
                batchResponse.getSuccessCount(), batchResponse.getFailureCount(), invalidTokens.size());
        return invalidTokens;
    }

    private Message toMessage(DailyPushPayload payload) {
        return Message.builder()
                .setToken(payload.fcmToken())
                .putData("type", "daily_target")
                .putData("totalPages", String.valueOf(payload.totalPages()))
                .putData("goalCount", String.valueOf(payload.goalCount()))
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setTtl(TTL_MILLIS)
                        .setCollapseKey(COLLAPSE_KEY)
                        .build())
                .build();
    }
}
