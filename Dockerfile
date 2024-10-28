# Use a Java base image
FROM openjdk:21
# Set the working directory to /app
WORKDIR /app

# Copy the Spring Boot application JAR file into the Docker image
COPY target/MafisSyStemInfo-0.0.1-SNAPSHOT.jar /app/MafisSyStemInfo-0.0.1-SNAPSHOT.jar

# Run the Spring Boot application when the container starts
CMD ["java", "-jar", "MafisSyStemInfo-0.0.1-SNAPSHOT.jar"]