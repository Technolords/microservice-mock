package net.technolords.micro.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.registry.ServiceRegistrationManager;

public class EurekaRenewalProcessor implements Processor {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private ServiceRegistrationManager serviceRegistrationManager;

    @Override
    public void process(Exchange exchange) throws Exception {
        if (this.serviceRegistrationManager == null) {
            this.serviceRegistrationManager = MockRegistry.findRegistrationManager();
        }
        LOGGER.info("About to renew with {}", this.serviceRegistrationManager);
        this.serviceRegistrationManager.registerForAllEureka();
    }
}
