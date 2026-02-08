FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE=movieDB.jar
COPY ${JAR_FILE} /app/app.jar

ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/data/database.db

VOLUME ["/data"]
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
