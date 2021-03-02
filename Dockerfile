FROM gradle:jdk15 AS build
WORKDIR /app
COPY . /app
RUN gradle bootJar --no-daemon

FROM openjdk:15-alpine
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","--enable-preview","-jar","/app.jar"]
