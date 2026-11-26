# 프로젝트 작업 지침

## 프로젝트
- Java 21과 Spring Boot 3.x를 사용하는 Gradle 기반 서버 프로젝트입니다.
- 실제 Spring Boot 애플리케이션은 `family-album` 아래에 있습니다. 애플리케이션 코드는 `family-album/src/main/java` 아래에, 설정 파일은 `family-album/src/main/resources` 아래에 둡니다.
- 패키지와 클래스 이름은 역할이 분명하게 드러나도록 작성합니다.

## 개발 규칙
- 기존 구조와 Spring 관례를 따르고, 필요한 범위에서만 변경합니다.
- HTTP API는 명시적인 경로와 적절한 상태 코드 및 응답 타입을 사용합니다.
- 코드는 도메인별로 나눕니다. 각 도메인의 엔티티, 저장소, 서비스, 컨트롤러는 `family-album/src/main/java/.../<domain>` 아래에 두고, 화면 템플릿은 `family-album/src/main/resources/templates/<domain>` 아래에 둡니다. 관련 테스트도 `family-album/src/test/java/.../<domain>`에 둡니다. 예: `account`, `invite`, `media`. 여러 도메인에 걸친 통합 테스트는 `web`에 둡니다. 공통 설정과 애플리케이션 시작 클래스만 별도 `config` 및 루트 패키지에 둡니다.
- 도메인 간 참조는 해당 도메인의 서비스 경계를 통하고, 다른 도메인의 저장소를 직접 호출하지 않습니다.
- 비밀 값이나 환경별 설정을 코드에 하드코딩하지 않습니다. 설정은 `application.yml` 또는 환경 변수로 관리합니다.
- 새 기능은 먼저 기대 동작을 테스트로 작성하고, 테스트 실패를 확인한 뒤 구현하는 red-green 순서를 따릅니다. 테스트와 패키징은 `cd family-album && ./gradlew test`, `cd family-album && ./gradlew build`로 확인합니다. 프로젝트 Gradle Wrapper를 사용하고, Wrapper 실행에 네트워크가 필요한 경우 그 제한을 보고합니다.
- 서버는 `cd family-album && ./gradlew bootRun`으로 실행하며 기본 포트는 8080입니다.
- 불필요한 라이브러리나 추상화 계층을 추가하지 않습니다.

## 변경 시
- README와 실행 절차가 바뀌면 함께 갱신합니다.
- 작업을 마칠 때 수행한 검증과 실행하지 못한 검증을 구분해 보고합니다.
