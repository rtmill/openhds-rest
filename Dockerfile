FROM java:8
MAINTAINER munk <munk@protonmail.com>
ADD . /service
COPY build/libs/openhds-rest-0.0.1-SNAPSHOT.jar /service/openhds-rest.jar
WORKDIR /service
CMD ["java", "-jar", "openhds-rest.jar"]
