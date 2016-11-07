package net.technolords.micro.route;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jetty.DefaultJettyHttpBinding;
import org.apache.camel.component.jetty.JettyHttpComponent;
import org.apache.camel.component.jetty.JettyHttpEndpoint;
import org.apache.camel.component.jetty9.JettyHttpComponent9;
import org.apache.camel.component.jetty9.JettyHttpEndpoint9;
import org.apache.camel.spi.ShutdownStrategy;
import org.eclipse.jetty.server.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.filter.InfoFilter;
import net.technolords.micro.processor.ResponseProcessor;
import net.technolords.micro.registry.MockRegistry;

public class RestServiceRoute extends RouteBuilder {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
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
//        from(generateCustomEndpoint())
            .log(LoggingLevel.DEBUG, LOGGER, "Received request...")
            .setExchangePattern(ExchangePattern.InOut)
            .to(DIRECT_MAIN);

        from(DIRECT_MAIN)
            .log(LoggingLevel.DEBUG, LOGGER, "Current headers: ${headers}")
            .process(this.responseProcessor);
    }

    private void updateShutdownStrategy() {
        ShutdownStrategy shutdownStrategy = super.getContext().getShutdownStrategy();
        shutdownStrategy.setTimeUnit(TimeUnit.SECONDS);
        shutdownStrategy.setTimeout(1L);
        shutdownStrategy.setShutdownNowOnTimeout(true);
        shutdownStrategy.setSuppressLoggingOnTimeout(true);
    }

    /*
    https://github.com/apache/camel/blob/master/components/camel-jetty9/src/main/java/org/apache/camel/component/jetty9/JettyHttpComponent9.java

    protected JettyHttpEndpoint createEndpoint(URI endpointUri, URI httpUri) throws URISyntaxException {
        return new JettyHttpEndpoint9(this, endpointUri.toString(), httpUri);
    }

    httpUri = The url of the HTTP endpoint to call.

     */
    protected Endpoint generateCustomEndpoint() throws URISyntaxException {
        JettyHttpComponent jettyHttpComponent = new JettyHttpComponent9();
//        jettyHttpComponent.setEnableJmx(true);
//        jettyHttpComponent.setJettyHttpBinding(new DefaultJettyHttpBinding());
        final String uri = "jetty:http://0.0.0.0:9090/?matchOnUriPrefix=true&enableJmx=true&handlers=metrics&filtersRef=infoFilter";
        final URI httpUri = new URI("http://0.0.0.0:9090/");
        JettyHttpEndpoint jettyHttpEndpoint = new JettyHttpEndpoint9(jettyHttpComponent, uri, httpUri);
        jettyHttpEndpoint.setMatchOnUriPrefix(true);
//        jettyHttpEndpoint.setEnableJmx(true);
        jettyHttpEndpoint.setHandlers(initHandlers());
        jettyHttpEndpoint.setFilters(initFilters());
        return jettyHttpEndpoint;
    }

    private List<Handler> initHandlers() {
        List<Handler> handlers = new ArrayList<>();
        handlers.add(MockRegistry.findStatisticsHandler());
        return handlers;
    }

    private List<Filter> initFilters() {
        List<Filter> filters = new ArrayList<>();
        filters.add(MockRegistry.findInfoFilter());
        return filters;
    }

    /**
     * Generates a Camel Jetty endpoint.
     *
     * @return
     *  A Camel Jetty endpoint
     */
    protected String generateJettyEndpoint() {
        StringBuilder buffer = new StringBuilder();
        // jetty:http://0.0.0.0:9090/?matchOnUriPrefix=true&enableJmx=true&handlers=metrics&filtersRef=infoFilter
        buffer.append(JETTY_MAIN).append(JETTY_BINDING_ADDRESS).append(":").append(this.port);
        buffer.append(JETTY_BINDING_PATH);
        buffer.append(QUESTION_SIGN).append("matchOnUriPrefix").append(EQUAL_SIGN).append(TRUE_VALUE);
        buffer.append(AND_SIGN).append("enableJmx").append(EQUAL_SIGN).append(TRUE_VALUE);
        buffer.append(AND_SIGN).append("handlers").append(EQUAL_SIGN).append("metrics");
        buffer.append(AND_SIGN).append("filtersRef").append(EQUAL_SIGN).append(InfoFilter.FILTER_ID);
        return buffer.toString();
    }

}
