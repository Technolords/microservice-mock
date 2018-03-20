FROM openjdk:8

ARG MOCK_VERSION
ARG JAR_FILE

LABEL mock.version=${MOCK_VERSION}

RUN mkdir -p /etc/mock

ADD target/${JAR_FILE} /etc/mock/mock.jar

ADD docker/run-mock.sh /etc/mock

RUN chmod +x /etc/mock/run-mock.sh

EXPOSE 9090

HEALTHCHECK --interval=1m --timeout=10s \
    CMD curl --fail http://localhost:9090/mock/cmd?config=current || exit 1

VOLUME ["/etc/mock/config", "/var/mock/data"]

WORKDIR "/etc/mock/"

ENTRYPOINT ["./run-mock.sh"]