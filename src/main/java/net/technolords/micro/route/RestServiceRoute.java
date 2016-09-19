package net.technolords.micro.route;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.processor.ResponseProcessor;

/**
 * Created by Technolords on 2016-Jun-23.
 */
public class RestServiceRoute extends RouteBuilder {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DIRECT_MAIN = "direct:main";
    private static final String JETTY_MAIN = "jetty:http://";
    private static final String JETTY_BINDING_ADDRESS = "0.0.0.0";
    private static final String JETTY_BINDING_PATH = "/";
    private static final String JETTY_OPTIONS = "?matchOnUriPrefix=true&enableJmx=true";
    private String port = null;
    private Processor responseProcessor = null;

    public RestServiceRoute(String myPort, ConfigurationManager configurationManager) {
        this.port = myPort;
        this.responseProcessor = new ResponseProcessor(configurationManager);
        LOGGER.info("Using port: " + this.port);
    }

    /**
     * Generates a Camel route, that listens from any HTTP request made (GET or POST) regardless
     * of the path. The response resolution is delegated towards the response processor.
     */
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
            .transform(simple("An error occurred: ${exception.message}"));

        from(generateJettyEndpoint())
            .log(LoggingLevel.DEBUG, LOGGER, "Received request...")
            .setExchangePattern(ExchangePattern.InOut)
            .to(DIRECT_MAIN);

        from(DIRECT_MAIN)
            .log(LoggingLevel.DEBUG, LOGGER, "Current headers: ${headers}")
            .process(this.responseProcessor);
    }

    /**
     * Generates a Camel Jetty endpoint.
     *
     * @return
     *  A Camel Jetty endpoint
     */
    protected String generateJettyEndpoint() {
        StringBuilder buffer = new StringBuilder();
        // jetty:http://0.0.0.0:9090/?matchOnUriPrefix=true&enableJmx=true
        buffer.append(JETTY_MAIN).append(JETTY_BINDING_ADDRESS).append(":").append(this.port);
        buffer.append(JETTY_BINDING_PATH).append(JETTY_OPTIONS);
        return buffer.toString();
    }

}
