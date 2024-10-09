FROM openjdk:17-alpine
VOLUME /tmp
ADD ./servicio-unidades.jar servicio-unidades.jar
ENTRYPOINT ["java","-jar","/servicio-unidades.jar"]
