FROM openjdk:8

ARG JAR_FILE

RUN mkdir -p /etc/mock

ADD target/${JAR_FILE} /etc/mock/mock.jar

ADD docker/run-mock.sh /etc/mock

RUN chmod +x /etc/mock/run-mock.sh

EXPOSE 9090

VOLUME ["/etc/mock/config", "/var/mock/data"]

WORKDIR "/etc/mock/"

ENTRYPOINT ["./run-mock.sh"]