package net.technolords.micro.camel.lifecycle;

import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.management.DefaultManagementLifecycleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainLifecycleStrategy extends DefaultManagementLifecycleStrategy {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public MainLifecycleStrategy(CamelContext camelContext) {
        super(camelContext);
    }

    @Override
    public void onContextStart(CamelContext camelContext) throws VetoCamelContextStartException {
        LOGGER.info("onContextStart started...");
        // TODO: register (if configured)
        super.onContextStart(camelContext);
    }

    @Override
    public void onContextStop(CamelContext context) {
        LOGGER.info("onContextStop started...");
        // TODO: deregister (if configured)
        super.onContextStop(getCamelContext());
    }
}
