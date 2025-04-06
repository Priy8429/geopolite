

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY uploads/ /app/uploads/
COPY target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Optimized JVM settings for containers
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
