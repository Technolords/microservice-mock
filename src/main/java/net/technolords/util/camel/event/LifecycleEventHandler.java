package net.technolords.util.camel.event;

import net.technolords.util.service.ConfigurationManager;
import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.support.LifecycleStrategySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LifecycleEventHandler extends LifecycleStrategySupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleEventHandler.class);
    private final ConfigurationManager configurationManager;

    public LifecycleEventHandler(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @Override
    public void onContextStarting(CamelContext camelContext) throws VetoCamelContextStartException {
        LOGGER.info("onContextStart called...");
        this.configurationManager.initializeConfiguration(camelContext);
        super.onContextStarting(camelContext);
    }
}
