# 1. 빌드(Build) 단계: Gradle을 사용하여 프로젝트를 빌드.
FROM openjdk:17-jdk-slim AS build
WORKDIR /workspace/app

# Gradle 래퍼 파일들을 먼저 복사.
COPY gradlew .
COPY gradle gradle

# build.gradle 파일을 복사.
COPY build.gradle .
# src 폴더 전체를 복사.
COPY src src

# 프로젝트를 빌드하여 Jar 파일을 생성.
# './gradlew build'는 'bootJar'를 포함하므로 별도 실행 필요 없음.
RUN ./gradlew build -x test --no-daemon

# 2. 실행(Run) 단계: 빌드된 Jar 파일만으로 가벼운 이미지를 생성.
FROM openjdk:17-jdk-slim

# 빌드 단계에서 생성된 Jar 파일을 실행 단계로 복사.
# build/libs/lotto-project-0.0.1-SNAPSHOT.jar 와 같은 경로를 잘 확인하세요.
# Artifact 이름이 다르다면 그에 맞게 수정.
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# 컨테이너가 시작될 때 이 명령어를 실행합니다.
ENTRYPOINT ["java","-jar","/app.jar"]