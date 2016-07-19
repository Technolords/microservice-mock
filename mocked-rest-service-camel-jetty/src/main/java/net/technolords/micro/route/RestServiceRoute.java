package net.technolords.micro.route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Technolords on 2016-Jun-23.
 */
public class RestServiceRoute extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceRoute.class);
    private static final String DIRECT_MAIN = "direct:main";
//    private static final String REST_PATH = "/mapng/traxis-structure-service/v1/pl/pl/vodStructure";
    private static final String REST_PATH = "/mock";
    private static final String PATH_TO_MOCKED_RESPONSE = "mock/remote-traxis-structure-service.json";
    private String port;
    private String cachedResponse;

    public RestServiceRoute(String myPort) {
        this.port = myPort;
        LOGGER.info("Using port: " + this.port);
    }

    @Override
    public void configure() throws Exception {
        from("jetty:http://" + this.generateAddress())
            .log(LoggingLevel.DEBUG, LOGGER, "Received request...")
            .setExchangePattern(ExchangePattern.InOut)
            .to(DIRECT_MAIN);

        from(DIRECT_MAIN)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
            .setHeader("Content-Type", constant("application/json"))
            .setBody(constant(this.generateResponse()));
//            .setBody(constant("Demo"));
    }

    protected String generateAddress() {
        StringBuilder buffer = new StringBuilder();
        // 0.0.0.0:9090
        buffer.append("0.0.0.0").append(":").append(this.port);
        // /mapng/traxis-structure-service/v1/pl/pl/vodStructure
        buffer.append(REST_PATH);
        // Enable jmx support
        buffer.append("?enableJmx=true");
        return buffer.toString();
    }

    protected String generateResponse() throws IOException {
        if (this.cachedResponse == null) {
            InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_TO_MOCKED_RESPONSE);
            LOGGER.info("Path to mocked file exists: " + fileStream.available());
            this.cachedResponse = new BufferedReader(new InputStreamReader(fileStream)).lines().collect(Collectors.joining("\n"));
        }
        return this.cachedResponse;
    }

}
