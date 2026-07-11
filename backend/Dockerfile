# ---- 依存キャッシュ層（pom だけ先に解決し、ソース変更で依存を再取得しない） ----
FROM maven:3.9-eclipse-temurin-21 AS deps
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline

# ---- 開発用（docker compose で使用。ソースは bind mount、devtools で自動再起動） ----
FROM maven:3.9-eclipse-temurin-21 AS dev
WORKDIR /app
COPY --from=deps /root/.m2 /root/.m2
EXPOSE 8080
CMD ["./mvnw", "spring-boot:run"]

# ---- ビルド層（本番 jar を生成） ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY --from=deps /root/.m2 /root/.m2
COPY . .
RUN ./mvnw -B clean package -DskipTests

# ---- 本番実行層（JRE のみ・軽量） ----
FROM eclipse-temurin:21-jre AS prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
