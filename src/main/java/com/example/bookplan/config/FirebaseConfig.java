package com.example.bookplan.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging(
            @Value("${firebase.credentials-base64:}") String credentialsBase64) {

        if (!StringUtils.hasText(credentialsBase64)) {
            log.warn("FIREBASE_CREDENTIALS_BASE64가 비어 있어 푸시 발송을 비활성화합니다.");
            return null;
        }

        try {
            byte[] serviceAccountJson = Base64.getMimeDecoder().decode(credentialsBase64.trim());
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccountJson)))
                    .build();

            FirebaseApp app = FirebaseApp.getApps().isEmpty()
                    ? FirebaseApp.initializeApp(options)
                    : FirebaseApp.getInstance();

            log.info("Firebase 초기화 완료");
            return FirebaseMessaging.getInstance(app);
        } catch (Exception e) {
            log.error("Firebase 초기화에 실패해 푸시 발송을 비활성화합니다.", e);
            return null;
        }
    }
}
