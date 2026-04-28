# ── Stage 1: Build ──────────────────────────────────────
# Uses a full Maven + JDK image just to compile
FROM maven:3.9-eclipse-temurin-25-alpine AS builder

WORKDIR /app

# Copy pom.xml first — Docker caches this layer
# so dependencies are only re-downloaded when pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ────────────────────────────────────
# Throws away Maven and JDK — only keeps the JAR
# Alpine = tiny Linux (~5MB base), eclipse-temurin = official Java
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create a non-root user — security best practice
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copy only the JAR from the builder stage
COPY --from=builder /app/target/course-domain-0.0.1-SNAPSHOT.jar app.jar

# Document that the app listens on 8080
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]