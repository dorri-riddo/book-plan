package com.example.bookplan.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyPushScheduler {

    private final DailyPushService service;

    @Scheduled(cron = "${notification.daily-cron}", zone = "Asia/Seoul")
    public void sendDailyReminders() {
        try {
            service.sendDailyReminders();
        } catch (Exception e) {
            log.error("매일 알림 발송 중 오류", e);
        }
    }
}
