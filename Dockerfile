FROM arm64v8/openjdk
# FROM amd64/openjdk

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} mamba.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/mamba.jar"]