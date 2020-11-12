FROM openjdk:8-jdk-alpine
LABEL maintener="jl.protois.perso@gmail.com"
EXPOSE 8082
ARG JAR_FILE=build/libs/TourGuide-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} TourGuide.jar
ENTRYPOINT ["java","-jar","/TourGuide.jar"]