# book-plan

> 완독일까지 **"오늘 N쪽 읽기"** 를 계산해주고 응원 알림을 보내주는 독서 목표 관리 앱

책의 총 페이지 수와 목표 완독일을 입력하면, 남은 기간에 맞춰 **오늘 읽어야 할 분량**을 자동으로 계산합니다.
혼자서 또는 가까운 사람들끼리 가볍게 쓰는 것을 1차 목표로 한 사이드 프로젝트입니다.

> ⚠️ **개발 진행 중** — 백엔드 MVP는 배포되어 동작하며, 현재 안드로이드 클라이언트를 개발하고 있습니다.

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

## 로드맵

- [ ] 책 / 목표 삭제 기능
- [ ] FCM 푸시 알림 — 매일 "오늘 N쪽" 응원 알림
- [ ] 알라딘 OPEN API 연동 — 책 검색 / 자동완성
- [ ] 독서 통계 화면
- [ ] 구글 플레이스토어 출시

---

🚧 진행 중인 프로젝트로, 기능과 문서는 계속 업데이트됩니다.
