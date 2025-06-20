####
# Dockerfile para aplicación Quarkus con Maven
#
# Antes de construir la imagen ejecuta:
# ./mvnw package
#
# Luego construye la imagen con:
# docker build -f Dockerfile -t quarkus/claudinary-image-service-maven .
#
# Ejecuta el contenedor con:
# docker run -i --rm -p 8080:8080 quarkus/claudinary-image-service-maven
#
####

# Etapa 1: Build - Construir la aplicación con Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -Dquarkus.package.type=fast-jar -DskipTests

# Etapa 2: Final - Crear la imagen de producción
FROM amazoncorretto:21-alpine-jdk
WORKDIR /deployments

# Copiar solo los artefactos necesarios desde la etapa de build
COPY --from=build /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build /app/target/quarkus-app/*.jar /deployments/
COPY --from=build /app/target/quarkus-app/app/ /deployments/app/
COPY --from=build /app/target/quarkus-app/quarkus/ /deployments/quarkus/

USER 1001
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]