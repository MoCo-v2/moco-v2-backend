FROM openjdk:17-alpine
COPY build/libs/*.jar moco-0.0.1-SNAPSHOT.jar
EXPOSE 6080
CMD ["java","-jar","./moco-0.0.1-SNAPSHOT.jar"]
