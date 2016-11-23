package net.technolords.micro.camel;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.ShutdownStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.registry.MockRegistry;

public class RestServiceRoute extends RouteBuilder {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String ROUTE_ID_JETTY = "RouteJetty";
    public static final String ROUTE_ID_MAIN = "RouteMain";
    private static final String DIRECT_MAIN = "direct:main";
    private static final String JETTY_MAIN = "jetty:http://";
    private static final String JETTY_BINDING_ADDRESS = "0.0.0.0";
    private static final String JETTY_BINDING_PATH = "/";
    private static final String QUESTION_SIGN = "?";
    private static final String AND_SIGN = "&";
    private static final String EQUAL_SIGN = "=";
    private static final String TRUE_VALUE = "true";
    private String port = null;
    private Processor responseProcessor = null;

    public RestServiceRoute() {
        this.port = MockRegistry.findConfiguredPort();
        this.responseProcessor = new ResponseProcessor(MockRegistry.findConfigurationManager());
        LOGGER.info("Using port: " + this.port);
    }

    /**
     * Generates a Camel route, that listens from any HTTP request made (GET or POST) regardless
     * of the path. The response resolution is delegated towards the response processor.
     */
    @Override
    public void configure() throws Exception {
        this.updateShutdownStrategy();

        onException(Exception.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
            .transform(simple("An error occurred: ${exception.message}"));

        from(generateJettyEndpoint())
            .routeId(ROUTE_ID_JETTY)
            .id(ROUTE_ID_JETTY)
            .log(LoggingLevel.DEBUG, LOGGER, "Received request...")
            .setExchangePattern(ExchangePattern.InOut)
            .to(DIRECT_MAIN);

        from(DIRECT_MAIN)
            .routeId(ROUTE_ID_MAIN)
            .id(ROUTE_ID_MAIN)
            .log(LoggingLevel.DEBUG, LOGGER, "Current headers: ${headers}")
            .process(this.responseProcessor);
    }

    /**
     * Auxiliary method which configures the shut down strategy associated with the CamelContext. As result
     * it will not wait for any in-flight messages to finish longer than 1 second to finish.
     */
    private void updateShutdownStrategy() {
        ShutdownStrategy shutdownStrategy = super.getContext().getShutdownStrategy();
        shutdownStrategy.setTimeUnit(TimeUnit.SECONDS);
        shutdownStrategy.setTimeout(1L);
        shutdownStrategy.setShutdownNowOnTimeout(true);
        shutdownStrategy.setSuppressLoggingOnTimeout(true);
    }

    /**
     * Generates a Camel Jetty endpoint. Note that there is a (servlet) filter wired, but not by Camel. It seems
     * to be conflicting in the order of execution (as Camel itself also binds on /* as the filter does). As result
     * the filter is NOT the last one in the chain. For documentation purposes, the old way:
     *
     *      jetty:http://0.0.0.0:9090/?matchOnUriPrefix=true&enableJmx=true&handlers=metrics&filtersRef=infoFilter
     * and the associated code snippet:
     *      buffer.append(AND_SIGN).append("filtersRef").append(EQUAL_SIGN).append(InfoFilter.FILTER_ID);
     *
     * @return
     *  A Camel Jetty endpoint
     */
    protected String generateJettyEndpoint() {
        StringBuilder buffer = new StringBuilder();
        // jetty:http://0.0.0.0:9090/?matchOnUriPrefix=true&enableJmx=true&handlers=metrics
        buffer.append(JETTY_MAIN).append(JETTY_BINDING_ADDRESS).append(":").append(this.port);
        buffer.append(JETTY_BINDING_PATH);
        buffer.append(QUESTION_SIGN).append("matchOnUriPrefix").append(EQUAL_SIGN).append(TRUE_VALUE);
        buffer.append(AND_SIGN).append("enableJmx").append(EQUAL_SIGN).append(TRUE_VALUE);
        buffer.append(AND_SIGN).append("handlers").append(EQUAL_SIGN).append("metrics");
        return buffer.toString();
    }

}
