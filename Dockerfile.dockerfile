FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/kkkarwash-backend-1.0.0.jar app.jar

EXPOSE 8080

ENV DATABASE_URL=jdbc:mysql://mysql:3306/kkkarwash
ENV DATABASE_USER=kkkuser
ENV DATABASE_PASSWORD=kkkpass123
ENV JWT_SECRET=supersecretjwtkey1234567890
ENV JWT_EXPIRATION=86400000

ENTRYPOINT ["java", "-jar", "app.jar"]