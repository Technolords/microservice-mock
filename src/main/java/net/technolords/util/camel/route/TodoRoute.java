package net.technolords.util.camel.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TodoRoute extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoRoute.class);

    @Override
    public void configure() throws Exception {
        LOGGER.info("About to create a route...");
        from("direct:todo")
                .routeId("todo")
                .id("todo")
                .log(LoggingLevel.DEBUG, LOGGER, "Got exchange: $exchange")
                .to("mock:todo");
    }
}
