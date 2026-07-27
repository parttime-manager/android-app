# PTManager Android

## 소개

직원 앱과 사장 앱을 함께 개발하는 Android 저장소입니다. Android Studio에서 이 디렉터리를 열면 두 앱을 선택해 실행할 수 있습니다.

| 경로 / Gradle 모듈 | 역할 | Application ID |
| --- | --- | --- |
| `employee/` / `:employee` | 직원용 앱 | `com.example.ptmanageremployee` |
| `employer/` / `:employer` | 사장용 앱 | `com.example.ptmanageremployer` |
| `common/` | 공통 화면·API·인증·리소스·테스트·빌드 설정 | 각 앱 패키지로 생성 |

두 앱은 별도 APK로 설치됩니다. 공통 코드는 같은 커밋의 `common/`에서 관리합니다. Backend는 별도 저장소로 유지합니다.

## 기술 스택

Kotlin · XML View · Retrofit · OkHttp · Coroutines

공통 의존성은 [버전 카탈로그](common/libs.versions.toml), 빌드 설정은 [android.gradle](common/android.gradle)에서 관리합니다.

### 아키텍처

Android 앱은 MVVM·클린 아키텍처를 사용합니다. 의존성 방향·적용 범위·검증 한계는 [구현 계획](https://github.com/parttime-manager/.github/blob/main/plan.md#아키텍처)에서 관리합니다.

## 시작하기

JDK 21, Android SDK 36.1, Build Tools 36.0.0을 준비합니다. Android 10(API 29) 이상의 기기 또는 에뮬레이터를 사용합니다.

1. [Backend](https://github.com/parttime-manager/backend#시작하기)를 실행합니다.
2. 필요하면 저장소 루트에 `local.properties`를 만듭니다. Android Studio는 여기에 `sdk.dir`도 기록합니다.

```properties
base.url=http://10.0.2.2:8080/
```

에뮬레이터는 위 기본 주소를 사용합니다. 실기기는 접근 가능한 서버 주소를 지정하고 끝에 `/`를 붙입니다. 앱별 설정이 필요하면 `employee/local.properties` 또는 `employer/local.properties`에서 루트 설정을 덮어쓸 수 있습니다.

```bash
# 두 앱 빌드
./gradlew assembleDebug

# 개별 앱 빌드·설치
./gradlew :employee:assembleDebug :employee:installDebug
./gradlew :employer:assembleDebug :employer:installDebug
```

설치에는 연결된 기기 또는 실행 중인 에뮬레이터가 필요합니다. Windows PowerShell에서는 `.\gradlew`를 사용합니다. APK는 `<모듈>/build/outputs/apk/debug/`에 생성됩니다.

### Firebase와 릴리스

FCM을 사용하려면 앱별 Firebase 설정을 `employee/google-services.json`, `employer/google-services.json`에 둡니다. 해당 파일이 있는 앱에만 Google Services 플러그인을 적용합니다.

릴리스 API 주소는 HTTPS여야 합니다. 로컬 서명 설정은 루트 또는 앱별 `local.properties`에 지정합니다. 키스토어 경로는 절대 경로를 권장합니다.

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

`local.properties`, Firebase 설정, 서명 키는 Git에서 제외합니다.

서명 릴리스 APK는 위 명령으로 수동 빌드합니다. 기존 배포 앱을 업데이트할 때는 기존 서명 키를 사용해야 합니다. FCM 파일은 커밋하지 않으며, FCM이 필요한 릴리스는 앱별 Firebase 파일을 준비해야 합니다.

## 사용 방법

[백엔드 데모 계정](https://github.com/parttime-manager/backend#데모-계정)으로 직원·사장 앱에 로그인합니다. 사장이 근무를 편성·발행하면 직원 앱에서 확인하고, QR 출퇴근·대타 요청과 승인을 두 앱에서 진행합니다. 시연 순서와 기대 결과는 [기능 완료 기준](https://github.com/parttime-manager/.github/blob/main/spec.md#기능-검증)을 따릅니다.

## 테스트

저장소 루트에서 두 앱의 단위 테스트와 Lint를 실행합니다.

```bash
./gradlew testDebugUnitTest lintDebug
```

### CI

[CI](.github/workflows/ci.yml)는 한 작업에서 직원·사장 앱의 Debug 빌드·단위 테스트·Lint·시크릿 커밋 검사를 실행합니다. 공통 코드 변경도 두 앱에서 검증됩니다.

## 관련 문서

[디자인 기준](design.md)은 표면·색상·간격·탐색 표현과 시각 검증 기준을 관리합니다.

| 문서 | 내용 |
| --- | --- |
| [공통 코드](common/README.md) | 공통 소스·리소스·테스트와 코드 생성 방식 |
| [프로젝트 문서](https://github.com/parttime-manager/.github#관련-문서) | 요구사항·설계·API 계약·배포 안내 |
