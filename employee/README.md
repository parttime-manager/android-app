# PTManager Employee App

직원이 근무를 확인하고 QR 출퇴근·대타 요청·소통을 처리하는 Android 앱입니다.

## 기술 스택

Kotlin · XML View · Fragment · Retrofit · OkHttp · Gson · Coroutines

앱 ID는 `com.example.ptmanageremployee`입니다. 의존성과 Android SDK 설정은 [app/build.gradle.kts](app/build.gradle.kts)에서 관리합니다.

## 시작하기

### 사전 요구사항

- Android Studio, JDK 21, Android SDK 36.1
- Android 10(API 29) 이상의 기기 또는 에뮬레이터
- [Backend 시작하기](https://github.com/PTManager/Backend#시작하기)에 따라 별도 터미널에서 실행한 서버

### 환경 설정

저장소 루트의 `local.properties`에 API 주소를 설정합니다. 에뮬레이터용 기본값은 다음과 같습니다.

```properties
base.url=http://10.0.2.2:8080/
```

실기기에서는 기기가 접근할 수 있는 서버 주소로 바꿉니다. 릴리스에는 HTTPS 주소가 필요하며, 값을 바꾼 뒤 다시 빌드합니다.
FCM을 사용하려면 `app/google-services.json`을 준비합니다. 이 파일과 `local.properties`는 커밋하지 않습니다.

### 빌드 및 설치

이 앱 저장소의 루트에서 실행합니다.

```bash
./gradlew assembleDebug
./gradlew installDebug
```

`installDebug`에는 연결된 기기나 실행 중인 에뮬레이터가 필요합니다. Windows PowerShell에서는 `.\gradlew`를 사용합니다.

### 릴리스 서명

키스토어·로컬 설정·CI 환경 변수는 공통 [앱 릴리스 서명](https://github.com/PTManager/docs/blob/main/deploy.md#앱-릴리스-서명)을 따릅니다.

## 사용 방법

백엔드 README의 직원 데모 계정으로 로그인합니다. 홈에서 오늘 근무를 확인하고 스케줄·소통·통계·내 정보 탭을 이용합니다.
계정·비밀번호·초대코드는 [Backend 데모 계정](https://github.com/PTManager/Backend#데모-계정)에서 관리합니다.
화면별 기능과 전체 검증 시나리오는 [요구사항](https://github.com/PTManager/docs/blob/main/spec.md)를 참고합니다.

## 테스트

```bash
./gradlew testDebugUnitTest
```

자동 검증 설정은 [CI 워크플로](.github/workflows/ci.yml)를 참고합니다.

## 관련 문서

| 문서 | 내용 |
| --- | --- |
| [프로젝트 문서](https://github.com/PTManager/docs#관련-문서) | 요구사항·설계·작업 현황·배포 안내 |
| [Backend](https://github.com/PTManager/Backend) | 서버 실행·데모 계정 |
| [사장 앱](https://github.com/PTManager/EmployerApp) | 함께 사용하는 앱 |
