package com.example.bookplan.notification;

public record DailyPushPayload(String fcmToken, int totalPages, int goalCount) {
}
