ARG VERSION=8u151

FROM openjdk:${VERSION}-jdk as build
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN chmod +x gradlew
RUN ./gradlew shadowJar --console=verbose --no-daemon

FROM openjdk:${VERSION}-jre
COPY --from=build /app/build/libs/ducket-api*all.jar /app/build/ducket-api.jar
CMD ["java", "-Dlog.path=/app/logs", "-Ddata.path=/app/data", "-Decb.path=/tmp/ecb", "-Dio.netty.native.workdir=/tmp", "-jar", "/app/build/ducket-api.jar"]