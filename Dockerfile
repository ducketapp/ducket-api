ARG VERSION=8u151

FROM openjdk:${VERSION}-jdk as build
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN chmod +x gradlew
RUN ./gradlew shadowJar --console=verbose --no-daemon

FROM openjdk:${VERSION}-jre
COPY --from=build /app/build/libs/ducketapp-api*all.jar /app/build/ducketapp-api.jar
CMD ["java", "-config=resources/application.conf", "-Dlog.path=/app/logs", "-Ddb.pullRates=true", "-Dio.netty.native.workdir=/tmp", "-jar", "/app/build/ducketapp-api.jar"]