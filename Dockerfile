# syntax=docker/dockerfile:1.4
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
COPY src src
RUN chmod +x mvnw
# Build sem BuildKit (compatível com ambientes sem suporte a --mount)
RUN ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS=""
# Copia qualquer JAR gerado pelo build (evita quebrar quando a versão do artefato muda)
COPY --from=builder /workspace/target/*.jar /app/app.jar
# Copia script de espera
COPY scripts/wait-for-postgres.sh /app/wait-for-postgres.sh
RUN chmod +x /app/wait-for-postgres.sh
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "/app/wait-for-postgres.sh ${DATABASE_HOST:-postgres} ${DATABASE_PORT:-5432} && java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar /app/app.jar"]
