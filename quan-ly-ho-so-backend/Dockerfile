# ===== Giai đoạn 1: Build file .jar bằng Maven =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Tải dependency trước để tận dụng cache của Docker
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ===== Giai đoạn 2: Chạy ứng dụng (image nhỏ gọn) =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Thư mục lưu file upload và chỉ mục Lucene (được mount volume trong docker-compose)
RUN mkdir -p /app/uploads /app/data/lucene/projects

ENV TZ=Asia/Ho_Chi_Minh
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
