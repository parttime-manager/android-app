# PTManager Android

직원 앱과 사장 앱을 함께 개발하는 Android 저장소입니다. Android Studio에서 이 디렉터리를 열면 두 앱을 선택해 실행할 수 있습니다.

| 경로 / Gradle 모듈 | 역할 | Application ID |
| --- | --- | --- |
| `employee/` / `:employee` | 직원용 앱 | `com.example.ptmanageremployee` |
| `employer/` / `:employer` | 사장용 앱 | `com.example.ptmanageremployer` |
| `common/` | 공통 화면·API·인증·리소스·테스트·빌드 설정 | 각 앱 패키지로 생성 |

두 앱은 별도 APK로 설치됩니다. 공통 코드는 같은 커밋의 `common/`에서 관리합니다. Backend는 별도 저장소로 유지합니다.

## 아키텍처

MVVM과 클린 아키텍처의 의존성 경계를 사용합니다.

| 경로 | 책임 |
| --- | --- |
| `domain/model`, `domain/repository` | Android·Retrofit에 의존하지 않는 모델과 저장소 계약 |
| `data` | HTTP·파일 업로드·세션 저장소 구현과 JSON 변환 |
| `presentation/view` | 화면 표시·입력·탐색 |
| `presentation/viewmodel` | 화면 수명에 연결된 요청과 상태 |
| `AppGraph` | 저장소 구현 주입과 ViewModel 생성 |

화면의 서버 요청은 ViewModel을 거쳐 도메인 저장소 계약으로 전달합니다. ViewModel은 View·Context·ViewBinding을 보관하지 않으며, `ViewModelProvider`가 화면 재생성 시 인스턴스를 유지합니다. 단순 요청은 `AppViewModel`이 Kotlin 인터페이스 위임으로 저장소에 전달하고, 별도 상태가 필요한 흐름은 전용 ViewModel로 분리합니다. 기존 화면의 일회성 렌더링은 suspend 반환값을 사용하며 모든 화면을 StateFlow 관찰 방식으로 전환한 것은 아닙니다.

`common/test/CleanArchitectureTest`는 공통 소스의 도메인·ViewModel 의존성과 `Network.api` 직접 호출을 검사합니다. 역할별 앱 소스는 이 검사의 탐색 범위에 포함되지 않습니다. 앱 ID·저장소 키·API 계약은 유지합니다.

직원·사장 공통 알림 화면은 `NotificationsViewModel`의 StateFlow를 관찰합니다. 조회·전체 읽음 처리를 화면에서 분리하며, 화면 재생성 시 조회 결과를 유지합니다. 공통 소스의 패키지 치환 방식도 유지합니다.

상세 의존성 방향·적용 범위는 [아키텍처 설계](https://github.com/parttime-manager/.github/blob/main/plan.md#아키텍처)를 따릅니다.

## 시작하기

JDK 21, Android SDK 36.1, Build Tools 36.0.0을 준비합니다. Android 10(API 29) 이상의 기기 또는 에뮬레이터를 사용합니다.

1. [Backend](https://github.com/parttime-manager/backend#시작하기)를 실행합니다.
2. 필요하면 저장소 루트에 `local.properties`를 만듭니다. Android Studio는 여기에 `sdk.dir`도 기록합니다.

```properties
base.url=http://10.0.2.2:8080/
```

에뮬레이터는 위 기본 주소를 사용합니다. 실기기는 접근 가능한 서버 주소를 지정하고 끝에 `/`를 붙입니다. 앱별 설정이 필요하면 `employee/local.properties` 또는 `employer/local.properties`에서 루트 설정을 덮어쓸 수 있습니다.

```bash
# 두 앱 빌드·검증
./gradlew assembleDebug testDebugUnitTest lintDebug

# 개별 앱 빌드·설치
./gradlew :employee:assembleDebug :employee:installDebug
./gradlew :employer:assembleDebug :employer:installDebug
```

설치에는 연결된 기기 또는 실행 중인 에뮬레이터가 필요합니다. Windows PowerShell에서는 `.\gradlew`를 사용합니다. APK는 `<모듈>/build/outputs/apk/debug/`에 생성됩니다.

## Firebase와 릴리스

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

## CI

[CI](.github/workflows/ci.yml)는 직원·사장 앱을 각각 빌드하고 단위 테스트와 Lint를 실행합니다. 디버그 APK를 앱별 아티팩트로 업로드합니다. 공통 코드 변경도 두 앱에서 검증됩니다.

`v*` 태그에서는 서명 릴리스 APK도 생성합니다. 통합 저장소에 다음 설정이 필요합니다.

- Repository variable `BASE_URL`: `/`로 끝나는 운영 HTTPS API 주소
- Repository secrets `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`

이 설정은 두 앱에 공통으로 적용합니다. 기존 배포 앱을 업데이트할 때는 기존 서명 키를 사용해야 합니다. FCM 파일은 커밋하지 않으며, CI에서 FCM이 필요한 배포는 별도로 파일을 주입해야 합니다.

## 관련 문서

| 문서 | 내용 |
| --- | --- |
| [공통 코드](common/README.md) | 공통 소스·리소스·테스트와 코드 생성 방식 |
| [프로젝트 문서](https://github.com/parttime-manager/.github#관련-문서) | 요구사항·설계·API 계약·배포 안내 |
