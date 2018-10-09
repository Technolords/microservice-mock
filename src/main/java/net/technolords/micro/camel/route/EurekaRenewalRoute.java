package net.technolords.micro.camel.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.registry.MockRegistry;

public class EurekaRenewalRoute extends RouteBuilder {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String ROUTE_ID_RENEWAL = "RouteRenewal";
    private Processor renewalProcessor = null;

    public EurekaRenewalRoute() {
        this.renewalProcessor = MockRegistry.findEurekaRenewalProcessor();
    }

    @Override
    public void configure() throws Exception {

        from(this.generateTimedEndpoint())
            .routeId(ROUTE_ID_RENEWAL)
            .id(ROUTE_ID_RENEWAL)
            .log(LoggingLevel.TRACE, LOGGER, "Got timed event...")
            .process(this.renewalProcessor);
    }

    /**
     * Note the fixed 30s rate. This is required. See also:
     *
     * https://github.com/spring-cloud/spring-cloud-netflix/issues/373
     *
     * @return
     */
    protected String generateTimedEndpoint() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("timer");
        buffer.append("://");
        buffer.append(ROUTE_ID_RENEWAL);
        buffer.append("?fixedRate=true");
        buffer.append("&delay=1000");
        buffer.append("&period=30s");
        return buffer.toString();
    }
}
