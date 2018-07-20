package net.technolords.micro.camel.lifecycle;

import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.management.DefaultManagementLifecycleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.registry.MockRegistry;

public class MainLifecycleStrategy extends DefaultManagementLifecycleStrategy {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public MainLifecycleStrategy(CamelContext camelContext) {
        super(camelContext);
    }

    @Override
    public void onContextStart(CamelContext camelContext) throws VetoCamelContextStartException {
        LOGGER.info("onContextStart started...");
        MockRegistry.findRegistrationManager().registerService();
        super.onContextStart(camelContext);
    }

    @Override
    public void onContextStop(CamelContext context) {
        LOGGER.info("onContextStop started...");
        MockRegistry.findRegistrationManager().deregisterService();
        super.onContextStop(getCamelContext());
    }
}
