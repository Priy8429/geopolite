

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY uploads/ /app/uploads/
COPY target/*.jar app.jar

# Create uploads directory with write permissions
RUN mkdir -p /app/uploads && \
    chmod -R 777 /app/uploads

# Expose the default Spring Boot port
EXPOSE 8080

# Optimized JVM settings for containers
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
