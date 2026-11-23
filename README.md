# Spring 학습 workspace

Spring 학습 문서는 `docs/spring`에 있고, 실행 가능한 Spring Boot 애플리케이션은 `playground`에 있습니다.

루트 프로젝트 자체도 `playground`를 Gradle 하위 모듈로 포함하므로 IntelliJ에서 이 폴더를 열어도 됩니다. 앱만 별도로 열고 싶다면 다음 Gradle 파일을 선택합니다.

```text
/Users/charleskk/Desktop/spring/playground/build.gradle.kts
```

터미널 실행:

```bash
cd playground
./gradlew bootRun
```

루트에서 실행할 때:

```bash
./gradlew :playground:bootRun
```

테스트와 패키징:

```bash
cd playground
./gradlew test
./gradlew build
```
