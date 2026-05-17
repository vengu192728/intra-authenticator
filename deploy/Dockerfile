# Build: docker build -f deploy/Dockerfile -t intra-authenticator .
# Run:   docker run -d -p 9091:9091 -v ./config:/config:ro intra-authenticator

FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/target/intra-authenticator-*.jar /app/app.jar

ENV AUTH_CREDENTIALS_FILE=/config/credentials.txt
EXPOSE 9091

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD curl -fsS http://127.0.0.1:9091/api/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
