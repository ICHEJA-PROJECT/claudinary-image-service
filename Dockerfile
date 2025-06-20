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
# docker run -i --rm -p 8088:8088 quarkus/claudinary-image-service-maven
#
####

FROM amazoncorretto:21-alpine-jdk

# Preparar directorios y permisos
WORKDIR /deployments
USER root
RUN chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments
USER 1001

# Copiar los artefactos generados por Maven
# Estructura de directorios de Maven: target/quarkus-app/
COPY --chown=1001:root target/quarkus-app/lib/ /deployments/lib/
COPY --chown=1001:root target/quarkus-app/*.jar /deployments/
COPY --chown=1001:root target/quarkus-app/app/ /deployments/app/
COPY --chown=1001:root target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8088

# Configurar variables de entorno para Quarkus
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8088"

ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]