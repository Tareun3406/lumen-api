FROM openjdk:21
ARG JAR_FILE=build/libs/lumen-calc-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ./lumen-api.jar
EXPOSE 8080/tcp
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./lumen-api.jar"]