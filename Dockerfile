# Stage 1: Build the application with JDK
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy only the files needed for dependency resolution (optimizes caching)
COPY pom.xml .
COPY src ./src

# Download dependencies and build the app (cache dependencies in a separate layer)
RUN mvn dependency:go-offline -B
RUN mvn clean package -DskipTests

# Stage 2: Runtime with JRE (smaller image)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the Spring Boot JAR file into the container
# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run as non-root user for security
# RUN useradd -m appuser && chown -R appuser /app
# USER appuser

# Expose the default Spring Boot port
EXPOSE 8080

# Optimized JVM settings for containers
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
