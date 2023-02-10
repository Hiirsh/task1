FROM amazoncorretto:19 as BUILDER

ENV APP_HOME=/app/
WORKDIR $APP_HOME

COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
RUN chmod +x gradlew

# RUN ./gradlew build || return 0 
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build

FROM amazoncorretto:19 as RUNNER

ENV ARTIFACT_NAME=app.jar
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY --from=BUILDER $APP_HOME/build/libs/$ARTIFACT_NAME .
EXPOSE 8080
CMD ["java","-jar",$ARTIFACT_NAME]