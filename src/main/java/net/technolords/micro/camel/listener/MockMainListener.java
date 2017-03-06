package net.technolords.micro.camel.listener;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.spi.ShutdownStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockMainListener extends MainListenerSupport {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void configure(CamelContext camelContext) {
        LOGGER.debug("Configure called...");
        this.updateShutdownStrategy(camelContext);
    }

    /**
     * Auxiliary method to configure the shutdown strategy associated with the CamelContext. As result
     * it will not wait longer than 1 second for any in-flight messages to complete execution.
     *
     * @param camelContext
     *  The Camel context associated with the shutdown strategy.
     */
    private void updateShutdownStrategy(CamelContext camelContext) {
        LOGGER.debug("Updating shutdown strategy for camel context: {}", camelContext.getName());
        ShutdownStrategy shutdownStrategy = camelContext.getShutdownStrategy();
        shutdownStrategy.setTimeUnit(TimeUnit.SECONDS);
        shutdownStrategy.setTimeout(1L);
        shutdownStrategy.setShutdownNowOnTimeout(true);
        shutdownStrategy.setSuppressLoggingOnTimeout(true);
    }

}
