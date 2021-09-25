FROM openjdk:11-jre-slim

FROM maven:3.6.3-jdk-11 AS MAVEN_BUILD

# Install and setup
COPY setup.sh /root/greenpoleclientcompany/setup.sh
RUN chmod +x /root/greenpoleclientcompany/setup.sh
RUN /root/greenpoleclientcompany/setup.sh

COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package -U -Dmaven.test.skip=true
RUN ls /build/target
RUN cp /build/target/greenpole-client-company-0.0.1.jar /opt/greenpoleclientcompany


WORKDIR /

COPY install.sh /root/greenpoleclientcompany/install.sh
RUN chmod +x /root/greenpoleclientcompany/install.sh
CMD  /root/greenpoleclientcompany/install.sh