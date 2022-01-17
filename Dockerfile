ARG VERSION=8u151

FROM openjdk:${VERSION}-jdk as build
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:${VERSION}-jre
COPY --from=build /app/build/libs/ducket-api*all.jar /bin/runner/ducket-api.jar
WORKDIR /bin/runner
CMD ["java", "-jar", "ducket-api.jar", "-config=resources/application.conf"]