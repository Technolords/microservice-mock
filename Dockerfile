FROM openjdk:8

ARG MOCK_VERSION
ARG JAR_FILE

LABEL mock.version=${MOCK_VERSION}

RUN mkdir -p /etc/mock

ADD target/${JAR_FILE} /etc/mock/mock.jar

EXPOSE 9090

HEALTHCHECK --interval=1m --timeout=10s \
    CMD curl --fail http://localhost:9090/mock/cmd?config=current || exit 1

VOLUME ["/var/mock"]

WORKDIR "/etc/mock/"

ENTRYPOINT ["sh","-c", "java ${JAVA_OPTS} -jar /etc/mock/mock.jar"]