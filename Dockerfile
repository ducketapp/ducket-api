ARG VERSION=8u151

FROM openjdk:${VERSION}-jdk as build
RUN mkdir /app
RUN ls -la
COPY . /app
RUN ls -la /app
WORKDIR /app
RUN chmod +x gradlew
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:${VERSION}-jre
COPY --from=build /app/build/libs/ducket-api*all.jar /bin/runner/
CMD ["java", "-jar", "/bin/runner/ducket-api.jar", "-Dio.netty.native.workdir=/tmp", "-Dlog.dir=/var/log/ducket"]