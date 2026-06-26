# 알바 관리 · 사장 앱 (PTManagerEmployer)

사장(관리자)용 매장 운영 안드로이드 앱. 매장 현황을 한눈에 보고, 스케줄을 편성하고, 대타 승인·근태·인건비를 관리하는 데 초점을 둔 구성입니다.

> 알바(직원)용은 [PTManagerEmployee](../PTManagerEmployee)를 참고하세요. 같은 데이터를 공유하되 화면은 역할별로 분리되어 있으며, 사장 앱은 **보라(#7048E8)** 테마입니다.

## 화면 구성 (하단 5탭)

| 탭 | 설명 |
|---|---|
| **홈** | 매장 현황 대시보드 — 근무 중·승인 대기·오늘/금주 인건비, 지금 근무 중, 스케줄 짜기 / 공지 작성 |
| **스케줄** | 주간 스케줄 편성, 자동 편성, 발행 |
| **승인** | 대타·요청 승인 (대기 중 / 처리 완료) |
| **통계** | 인건비 통계 — 이번 달 누적·예산, 주차별 추이, 알바별 인건비 |
| **내 정보** | 프로필 수정, 인건비 확인, 멤버·승인 관리, 알림 설정, 로그아웃 |

### 주요 화면 (탭 외)
- **공지 작성** — 제목·내용, 필독 설정, 전체 발송(푸시)
- **근태 현황** — 정상 / 지각 / 예정 출근 상태
- **매장 생성 · 멤버 관리** — 초대코드 발급, 가입 승인

## 기술 스택
- Kotlin, View 기반 XML 레이아웃 (Compose 미사용)
- 하단 탭 네비게이션 (`BottomNavigationView` + Fragment 전환)
- `applicationId` : `com.example.ptmanageremployer`
- minSdk 35 / targetSdk 36, 라이트 전용 테마

## 빌드 & 실행
```bash
./gradlew assembleDebug      # app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug       # 연결된 기기/에뮬레이터에 설치
```
