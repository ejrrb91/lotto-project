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

# 빌드 후, 불필요한 -plain.jar 파일을 먼저 삭제하고, 남은 .jar 파일의 이름을 app.jar로 변경합니다.
# 파일 이름이 달라져도 항상 app.jar로 통일됨.
RUN ./gradlew build -x test --no-daemon && \
    rm /workspace/app/build/libs/*-plain.jar && \
    mv /workspace/app/build/libs/*.jar /workspace/app/build/libs/app.jar

# 2. 실행(Run) 단계: 빌드된 Jar 파일만으로 가벼운 이미지를 생성.
FROM openjdk:17-jdk-slim

# 빌드 단계에서 생성된 Jar 파일을 실행 단계로 복사.
COPY --from=build /workspace/app/build/libs/app.jar app.jar

# 컨테이너가 시작될 때 이 명령어를 실행합니다.
ENTRYPOINT ["java","-jar","/app.jar"]