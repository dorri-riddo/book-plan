# book-plan

> 완독일까지 **"오늘 N쪽 읽기"** 를 계산해주고 응원 알림을 보내주는 독서 목표 관리 앱

책의 총 페이지 수와 목표 완독일을 입력하면, 남은 기간에 맞춰 **오늘 읽어야 할 분량**을 자동으로 계산합니다.
혼자서 또는 가까운 사람들끼리 가볍게 쓰는 것을 1차 목표로 한 사이드 프로젝트입니다.

> ✅ **Google Play 출시 (2026.08.30)** — 내부 테스트 → 비공개 테스트(테스터 12명 · 14일)를 거쳐 프로덕션 배포를 완료했고, 현재 운영 중입니다.
>
> 📱 [Google Play에서 설치하기](https://play.google.com/store/apps/details?id=YOUR_PACKAGE_ID)

## 기술 스택

**Backend**
- Java 17, Spring Boot 3.5.x
- Spring Data JPA, Spring Security + JWT
- PostgreSQL (Neon), 로컬은 H2
- Gradle, Docker(멀티 스테이지), Render 배포
- Springdoc OpenAPI (Swagger UI)

**Android** ([book-plan-android](https://github.com/dorri-riddo/book-plan-android))
- Kotlin, Jetpack Compose
- Retrofit + OkHttp, kotlinx-serialization

## 구현 현황

- **도메인 모델 설계** — `User` · `Book` · `ReadingGoal` · `ReadingProcess` 4개 핵심 엔티티, 도메인 기반 패키지 구조
- **핵심 로직** — 총 페이지와 목표일 기반의 "오늘 읽을 분량" 계산
- **JWT 인증** — 회원가입 / 로그인, 토큰 기반 인증
- **CRUD API** — 4개 도메인 전체 REST API
- **테스트 코드** — JUnit 5 + Mockito 기반 단위 테스트
- **배포** — Docker 이미지 빌드 후 Render 배포, Neon PostgreSQL 연동
- **API 문서** — Swagger UI 자동 문서화
- **안드로이드 앱** — Kotlin / Jetpack Compose 클라이언트, Google Play 출시

## 로드맵

- [x] 구글 플레이스토어 출시 — 2026.08.30 프로덕션 배포
- [ ] FCM 푸시 알림 — 매일 "오늘 N쪽" 응원 알림
- [ ] 알라딘 OPEN API 연동 — 책 검색 / 자동완성

---

기능과 문서는 계속 업데이트됩니다.
