# Spring 학습 workspace

Spring 학습 문서는 `docs/spring`에 있고, 실행 가능한 Spring Boot 애플리케이션은 `family-album`에 있습니다.

루트 프로젝트 자체도 `family-album`을 Gradle 하위 모듈로 포함하므로 IntelliJ에서 이 폴더를 열어도 됩니다. 앱만 별도로 열고 싶다면 다음 Gradle 파일을 선택합니다.

```text
/Users/charleskk/Desktop/spring/family-album/build.gradle.kts
```

터미널 실행:

```bash
cd family-album
./gradlew bootRun
```

루트에서 실행할 때:

```bash
./gradlew :family-album:bootRun
```

테스트와 패키징:

```bash
cd family-album
./gradlew test
./gradlew build
```

## Render 무료 배포

저장소 루트의 `render.yaml`은 `family-album/Dockerfile`을 사용하도록 설정되어 있습니다.

1. GitHub에 변경사항을 push합니다.
2. Render Dashboard에서 **New → Blueprint**를 선택합니다.
3. `family-album` 저장소를 연결합니다.
4. 생성 과정에서 `ADMIN_EMAIL`과 `ADMIN_PASSWORD`를 입력합니다.
5. 배포가 끝나면 Render가 제공하는 HTTPS 주소로 접속합니다.

무료 Web Service는 유휴 상태에서 절전될 수 있고 재시작될 수 있습니다. 현재 H2 DB와 `uploads/`는 컨테이너 내부에 저장되므로 재배포나 재시작 시 사라질 수 있습니다. 사진을 보존해야 하는 단계에서는 PostgreSQL과 Object Storage를 별도로 연결해야 합니다.
