package com.example.bookplan.notification;

import com.example.bookplan.auth.Token;
import com.example.bookplan.auth.TokenRepository;
import com.example.bookplan.readingGoal.ReadingGoalService;
import com.example.bookplan.readingGoal.dto.ReadingGoalCalculatePagesPerDayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyPushService {

    private final TokenRepository tokenRepository;
    private final ReadingGoalService readingGoalService;
    private final FcmSender fcmSender;

    public void sendDailyReminders() {
        if (!fcmSender.isEnabled()) {
            log.warn("Firebase가 초기화되지 않아 매일 알림을 건너뜁니다.");
            return;
        }

        List<Token> targets = tokenRepository.findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue();
        if (targets.isEmpty()) {
            log.info("매일 알림 대상 기기가 없습니다.");
            return;
        }

        List<DailyPushPayload> payloads = buildPayloads(targets);
        if (payloads.isEmpty()) {
            log.info("오늘 읽을 분량이 남은 사용자가 없습니다. (대상 기기 {}대)", targets.size());
            return;
        }

        List<String> invalidTokens = fcmSender.sendDailyTargets(payloads);
        if (!invalidTokens.isEmpty()) {
            tokenRepository.clearFcmTokens(invalidTokens);
        }
    }

    private List<DailyPushPayload> buildPayloads(List<Token> targets) {
        Map<Long, List<Token>> devicesByUser = targets.stream()
                .collect(Collectors.groupingBy(Token::getUserId));

        List<DailyPushPayload> payloads = new ArrayList<>();
        for (Map.Entry<Long, List<Token>> entry : devicesByUser.entrySet()) {
            try {
                List<ReadingGoalCalculatePagesPerDayResponse> goals =
                        readingGoalService.calculatePagesPerDay(entry.getKey());

                int totalPages = goals.stream()
                        .mapToInt(ReadingGoalCalculatePagesPerDayResponse::getTodayRemainingPages)
                        .sum();

                if (totalPages <= 0) {
                    continue;
                }

                for (Token device : entry.getValue()) {
                    payloads.add(new DailyPushPayload(device.getFcmToken(), totalPages, goals.size()));
                }
            } catch (Exception e) {
                log.error("사용자 {}의 분량 계산에 실패해 건너뜁니다.", entry.getKey(), e);
            }
        }
        return payloads;
    }
}
