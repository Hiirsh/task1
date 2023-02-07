FROM openjdk:12-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]

# FROM openjdk:12-jdk-alpine

# WORKDIR /app

# COPY .gradle/ .gradle
# COPY gradlew build.gradle ./
# RUN ./gradlew dependency:resolve

# COPY src .src/

# CMD ["./gradlew", "spring-boot:run"]