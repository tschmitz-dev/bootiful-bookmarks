FROM openjdk:8-jdk-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]
