# select parent image
FROM maven:3.8.1-jdk-11

COPY ./ ./

# add jar to container
#ADD DSUB_XDW_v1-1.0-SNAPSHOT.jar DSUB_XDW_v1-1.0-SNAPSHOT.jar
#ADD MANIFEST.MF MANIFEST.MF

# package our application code
RUN mvn clean package

# set the startup command to execute the jar
#CMD java - jar DSUB_XDW_v1-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "targer/demo-0.0.1-SNAPSHOT.jar"]


