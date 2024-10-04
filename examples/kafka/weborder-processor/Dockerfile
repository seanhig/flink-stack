#FROM openjdk:8-jdk-alpine
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]


# The weborder-model-1.0.0.jar is a shared jar, locally installed to 
# build the project
FROM maven:3.6-openjdk-17-slim AS build
COPY weborder-model-1.0.0.jar /usr/src/dep/
WORKDIR /usr/src/dep
RUN mvn org.apache.maven.plugins:maven-install-plugin:3.1.3:install-file  \
  -Dfile=/usr/src/dep/weborder-model-1.0.0.jar \
  -DgroupId=io.idstudios.flink.models \
  -DartifactId=weborder-model \
  -Dversion=1.0.0 \
  -Dpackaging=jar 

COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn package
RUN mvn dependency:copy-dependencies -DoutputDirectory=jars

# Step : final docker image
FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /usr/src/app/jars /opt/target
COPY --from=build /usr/src/app/target /opt/target

WORKDIR /opt/target
EXPOSE 8080
CMD ["java", "-jar", "weborder-processor-1.0.0.jar"]