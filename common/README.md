# Android 공통 코드

직원·사장 앱이 함께 쓰는 Kotlin 소스·XML 리소스·버전 카탈로그·빌드 설정입니다. 두 앱과 같은 저장소, 같은 커밋에서 관리합니다.

| 경로 | 역할 |
| --- | --- |
| `src/domain/` | 순수 모델·저장소 계약 |
| `src/data/` | API·저장소·인증·푸시 구현 |
| `src/presentation/` | View·ViewModel·표시 함수 |
| `src/AppGraph.kt` | 의존성 주입과 ViewModel 조립 |
| `test/` | 두 앱에서 각각 실행하는 공통 회귀 테스트 |
| `res/` | 공통 레이아웃·스타일·이미지. 앱별 색상·문구를 참조한다. |
| `libs.versions.toml` | 공통 의존성 버전 |
| `android.gradle` | SDK·서명·의존성·공통 소스 생성 설정 |

`common`은 독립 Gradle 라이브러리가 아닌 소스 템플릿입니다. 빌드 시 `com.example.ptmanagercommon`을 각 앱 namespace로 치환해 `<모듈>/build/generated/ptmCommon/main/`과 `test/`·`androidTest/`에 생성합니다. 생성 파일은 수정하거나 커밋하지 않습니다.

각 앱은 자체 application ID·Manifest·APK를 유지합니다. `APP_ROLE`은 앱 namespace에서 결정하며 가입 역할·알림 문구에 사용합니다. 매장 가입·생성 목적지는 앱별 `presentation/view/AppDestination.kt`에서 지정하고 권한은 서버에서 검사합니다.

두 모듈 모두 저장소 루트의 `common/`를 사용합니다. 공통 소스·리소스·빌드 설정 변경 후에는 저장소 루트에서 두 앱을 검증합니다.

```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
```

환경 설정·Firebase·서명·CI는 [저장소 README](../README.md)를 따릅니다. 기본 설정은 루트 `local.properties`에서 읽고, 앱별 `<모듈>/local.properties` 값이 우선합니다. Firebase 설정 파일은 각 모듈 루트에 둡니다.

계층 책임과 ViewModel 사용 범위는 [Android 아키텍처](../README.md#아키텍처)를 따릅니다.
