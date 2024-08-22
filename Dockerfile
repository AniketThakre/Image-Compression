FROM openjdk:21-jdk
VOLUME /tmp
COPY target/imageCompression-0.0.1-SNAPSHOT.jar imageCompression.jar
ENTRYPOINT ["java","-jar","/imageCompression.jar"]