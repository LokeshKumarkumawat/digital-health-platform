# STAGE 1: Download Dependencies
FROM eclipse-temurin:21-jdk-alpine AS deps
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && \
    ./mvnw dependency:resolve-plugins dependency:go-offline -B --no-transfer-progress

# STAGE 2: Build Application
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /app/mvnw mvnw
COPY --from=deps /app/.mvn .mvn
COPY --from=deps /app/pom.xml pom.xml
COPY src src

RUN ./mvnw package \
    --no-transfer-progress \
    -DskipTests \
    -Dmaven.test.skip=true

# STAGE 3: Extract Spring Boot Layers
FROM eclipse-temurin:21-jre-alpine AS extractor
WORKDIR /app

COPY --from=builder /app/target/digital-health-platform-*.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract --destination extracted

# STAGE 4: Final Production Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S -g 1001 appgroup && \
    adduser -S -u 1001 -G appgroup appuser

RUN apk add --no-cache wget curl

# Create all directories BEFORE switching to non-root user
RUN mkdir -p /app/uploads/profile-pictures && \
    mkdir -p /app/logs/digital-health-platform && \
    mkdir -p /tmp/logs && \
    chown -R appuser:appgroup /app && \
    chown -R appuser:appgroup /tmp/logs

COPY --from=extractor --chown=appuser:appgroup /app/extracted/dependencies/ ./
COPY --from=extractor --chown=appuser:appgroup /app/extracted/spring-boot-loader/ ./
COPY --from=extractor --chown=appuser:appgroup /app/extracted/snapshot-dependencies/ ./
COPY --from=extractor --chown=appuser:appgroup /app/extracted/application/ ./

USER appuser:appgroup

ENV JAVA_OPTS="\
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UseStringDeduplication \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true"

EXPOSE 8080

HEALTHCHECK \
    --interval=30s \
    --timeout=5s \
    --retries=3 \
    --start-period=60s \
    CMD wget -q --spider \
        http://localhost:8080/actuator/health/liveness || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]