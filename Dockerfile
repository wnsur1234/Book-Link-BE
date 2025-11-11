# JDK 17, 가벼운 alpine 기반
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# build/libs/*.jar 을 컨테이너에 복사
COPY build/libs/*.jar app.jar

# JVM 메모리 옵션
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]