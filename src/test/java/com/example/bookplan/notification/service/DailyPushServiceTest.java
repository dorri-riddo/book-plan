package com.example.bookplan.notification.service;

import com.example.bookplan.auth.Token;
import com.example.bookplan.auth.TokenRepository;
import com.example.bookplan.notification.DailyPushPayload;
import com.example.bookplan.notification.DailyPushService;
import com.example.bookplan.notification.FcmSender;
import com.example.bookplan.readingGoal.ReadingGoalService;
import com.example.bookplan.readingGoal.dto.ReadingGoalCalculatePagesPerDayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyPushServiceTest {

    @Mock
    TokenRepository tokenRepository;
    @Mock
    ReadingGoalService readingGoalService;
    @Mock
    FcmSender fcmSender;
    @InjectMocks
    DailyPushService service;

    @BeforeEach
    void setUp() {
        lenient().when(fcmSender.isEnabled()).thenReturn(true);
        lenient().when(fcmSender.sendDailyTargets(anyList())).thenReturn(List.of());
    }

    @Test
    @DisplayName("Firebase가 꺼져 있으면 조회조차 하지 않는다")
    void skipsWhenFirebaseDisabled() {
        when(fcmSender.isEnabled()).thenReturn(false);

        service.sendDailyReminders();

        verifyNoInteractions(tokenRepository, readingGoalService);
    }

    @Test
    @DisplayName("보낼 기기가 없으면 발송하지 않는다")
    void skipsWhenNoDevices() {
        when(tokenRepository.findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue())
                .thenReturn(List.of());

        service.sendDailyReminders();

        verify(fcmSender, never()).sendDailyTargets(anyList());
    }

    @Test
    @DisplayName("오늘 읽을 분량이 남은 사용자에게만 보낸다")
    void sendsOnlyToUsersWithRemainingPages() {
        Token reader = device(1L, "device-1", "fcm-1");
        Token finished = device(2L, "device-2", "fcm-2");
        when(tokenRepository.findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue())
                .thenReturn(List.of(reader, finished));
        List<ReadingGoalCalculatePagesPerDayResponse> readerGoals = List.of(goal(13), goal(7));
        List<ReadingGoalCalculatePagesPerDayResponse> finishedGoals = List.of(goal(0));
        when(readingGoalService.calculatePagesPerDay(1L)).thenReturn(readerGoals);
        when(readingGoalService.calculatePagesPerDay(2L)).thenReturn(finishedGoals);

        service.sendDailyReminders();

        List<DailyPushPayload> payloads = capturePayloads();
        assertThat(payloads).hasSize(1);
        assertThat(payloads.get(0).fcmToken()).isEqualTo("fcm-1");
        assertThat(payloads.get(0).totalPages()).isEqualTo(20);
        assertThat(payloads.get(0).goalCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("한 사용자가 기기를 여러 대 쓰면 기기마다 보낸다")
    void sendsToEveryDeviceOfTheSameUser() {
        when(tokenRepository.findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue())
                .thenReturn(List.of(device(1L, "phone", "fcm-phone"), device(1L, "tablet", "fcm-tablet")));
        List<ReadingGoalCalculatePagesPerDayResponse> goals = List.of(goal(30));
        when(readingGoalService.calculatePagesPerDay(1L)).thenReturn(goals);

        service.sendDailyReminders();

        assertThat(capturePayloads())
                .extracting(DailyPushPayload::fcmToken)
                .containsExactlyInAnyOrder("fcm-phone", "fcm-tablet");
        verify(readingGoalService, times(1)).calculatePagesPerDay(1L);
    }

    @Test
    @DisplayName("더는 배달되지 않는 토큰은 비운다")
    void clearsUndeliverableTokens() {
        when(tokenRepository.findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue())
                .thenReturn(List.of(device(1L, "device-1", "fcm-dead")));
        List<ReadingGoalCalculatePagesPerDayResponse> goals = List.of(goal(10));
        when(readingGoalService.calculatePagesPerDay(1L)).thenReturn(goals);
        when(fcmSender.sendDailyTargets(anyList())).thenReturn(List.of("fcm-dead"));

        service.sendDailyReminders();

        verify(tokenRepository).clearFcmTokens(List.of("fcm-dead"));
    }

    @Test
    @DisplayName("모두 배달되면 토큰을 건드리지 않는다")
    void keepsTokensWhenAllDelivered() {
        when(tokenRepository.findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue())
                .thenReturn(List.of(device(1L, "device-1", "fcm-1")));
        List<ReadingGoalCalculatePagesPerDayResponse> goals = List.of(goal(10));
        when(readingGoalService.calculatePagesPerDay(1L)).thenReturn(goals);

        service.sendDailyReminders();

        verify(tokenRepository, never()).clearFcmTokens(anyList());
    }

    @Test
    @DisplayName("한 사용자의 계산이 실패해도 나머지 사용자에게는 보낸다")
    void oneBrokenUserDoesNotBlockTheRest() {
        when(tokenRepository.findAllByFcmTokenIsNotNullAndNotificationEnabledIsTrue())
                .thenReturn(List.of(device(1L, "device-1", "fcm-1"), device(2L, "device-2", "fcm-2")));
        List<ReadingGoalCalculatePagesPerDayResponse> healthyGoals = List.of(goal(5));
        when(readingGoalService.calculatePagesPerDay(1L)).thenThrow(new NullPointerException("책이 지워진 목표"));
        when(readingGoalService.calculatePagesPerDay(2L)).thenReturn(healthyGoals);

        service.sendDailyReminders();

        assertThat(capturePayloads())
                .extracting(DailyPushPayload::fcmToken)
                .containsExactly("fcm-2");
    }

    @SuppressWarnings("unchecked")
    private List<DailyPushPayload> capturePayloads() {
        ArgumentCaptor<List<DailyPushPayload>> captor = ArgumentCaptor.forClass(List.class);
        verify(fcmSender).sendDailyTargets(captor.capture());
        return captor.getValue();
    }

    private Token device(Long userId, String deviceId, String fcmToken) {
        Token token = new Token(userId, deviceId, "access", "refresh", Instant.now(), true);
        token.updatePushRegistration(fcmToken, true);
        return token;
    }

    private ReadingGoalCalculatePagesPerDayResponse goal(int todayRemainingPages) {
        ReadingGoalCalculatePagesPerDayResponse response =
                mock(ReadingGoalCalculatePagesPerDayResponse.class);
        when(response.getTodayRemainingPages()).thenReturn(todayRemainingPages);
        return response;
    }
}
