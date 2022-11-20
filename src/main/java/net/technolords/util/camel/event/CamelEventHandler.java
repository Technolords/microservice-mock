package net.technolords.util.camel.event;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CamelEventHandler extends EventNotifierSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelEventHandler.class);

    @Override
    public boolean isEnabled(CamelEvent event) {
        switch (event.getType()) {
            case CamelContextStarting:
            case CamelContextStarted:
                return true;
            case ExchangeCompleted:
            default:
                return false;
        }
    }

    @Override
    public void notify(CamelEvent event) throws Exception {
        LOGGER.debug("Got an event: {}", event);
        if (CamelEvent.Type.CamelContextStarting == event.getType()) {
            // Prior to Camel 3.16.0 routes could be added in this phase, but it no longer will accept this...
            CamelContext camelContext = (CamelContext) event.getSource();
            camelContext.setMessageHistory(false);
            return;
        }
        if (CamelEvent.Type.CamelContextStarted == event.getType()) {
            try {
                // TODO: create routes dynamically
                LOGGER.info("All set... Mock requests should be processed...");
            } catch (Exception e) {
                LOGGER.error("Unexpected error: {} -> aborting!", e.getMessage(), e);
                System.exit(1);
            }
            return;
        }
        LOGGER.info("Unhandled event: {}", event);
    }
}
