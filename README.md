# PTManager Android

## 소개

직원·사장 앱(별도 APK)을 함께 개발하는 Android 저장소입니다.

| 경로 / 모듈 | 역할 | Application ID |
| --- | --- | --- |
| `employee/` (`:employee`) | 직원용 앱 | `com.example.ptmanageremployee` |
| `employer/` (`:employer`) | 사장용 앱 | `com.example.ptmanageremployer` |
| `common/` | 공통 화면·API·인증·리소스·테스트·빌드 설정 | 각 앱 패키지로 생성 |

## 기술 스택

Kotlin · XML View · Retrofit · OkHttp · Coroutines ([libs.versions.toml](common/libs.versions.toml), [android.gradle](common/android.gradle))

### 아키텍처

MVVM·클린 아키텍처를 사용하며 경계와 검증 범위는 [기술 구조](https://github.com/parttime-manager/.github/blob/main/03-tech/01-architecture.md#아키텍처와-경계)를 따릅니다.

## 시작하기

JDK 21, Android SDK 36.1(Build Tools 36.0.0), Android 10(API 29)+ 기기·에뮬레이터와 실행 중인 [Backend](https://github.com/parttime-manager/backend#시작하기)가 필요합니다. 루트(또는 모듈별) `local.properties`에 서버 주소를 설정합니다(기본 에뮬레이터 주소, 실기기는 접근 가능한 주소와 끝 `/` 지정):

```properties
base.url=http://10.0.2.2:8080/
```

```bash
./gradlew assembleDebug
./gradlew :employee:assembleDebug :employee:installDebug
./gradlew :employer:assembleDebug :employer:installDebug
```

APK는 `<모듈>/build/outputs/apk/debug/`에 생성됩니다(Windows는 `.\gradlew`).

### Firebase와 릴리스

앱별 `employee/google-services.json`·`employer/google-services.json`이 있을 때만 Firebase 플러그인을 적용합니다. 릴리스는 HTTPS `base.url`과 서명 설정이 필요합니다(`local.properties`·Firebase·서명 키는 Git 제외):

```properties
base.url=https://api.example.com/
keystore.path=/absolute/path/release.jks
keystore.password=YOUR_PASSWORD
key.alias=YOUR_ALIAS
key.password=YOUR_PASSWORD
```

```bash
./gradlew :employee:assembleRelease :employer:assembleRelease
```

## 사용 방법

[백엔드 데모 계정](https://github.com/parttime-manager/backend#데모-계정)으로 로그인해 [기능 완료 기준](https://github.com/parttime-manager/.github/blob/main/01-func/01-requirements.md#기능-검증) 순서로 근무 편성·QR 출퇴근·대타 요청/승인을 시연합니다.

## 테스트

```bash
./gradlew testDebugUnitTest lintDebug
```

[CI](.github/workflows/ci.yml)는 두 앱의 Debug 빌드·단위 테스트·Lint·시크릿 검사를 실행합니다.

## 관련 문서

- [디자인 기준](https://github.com/parttime-manager/.github/blob/main/02-uxui/01-design.md) · [공통 코드](common/README.md) · [프로젝트 문서](https://github.com/parttime-manager/.github#관련-문서)
