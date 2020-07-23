FROM nimbleplatform/nimble-base
MAINTAINER Salzburg Research <nimble-srfg@salzburgresearch.at>

# copy resource folder
COPY /resources /resources
RUN ls /resources

VOLUME /tmp
ARG finalName
ENV JAR '/'$finalName
ADD $finalName $JAR
RUN touch $JAR
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar", "epcis.jar"]
