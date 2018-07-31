package net.technolords.micro.registry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.model.jaxb.registration.Registration;
import net.technolords.micro.model.jaxb.registration.ServiceRegistration;
import net.technolords.micro.registry.consul.ConsulRequestFactory;

public class ServiceRegistrationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private HttpClient httpClient = HttpClientBuilder.create().build();

    /**
     * Register the mock as micro service with a service registration (so it will be available for
     * service discovery).
     *
     * Note that this is being called from the Camel's lifecycle management, i.e. on start.
     */
    public void registerService() {
        LOGGER.info("Checking whether the service needs to be registered...");
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();
        ServiceRegistration serviceRegistration = configurations.getServiceRegistration();
        if (serviceRegistration != null) {
            LOGGER.info("... Yes, about to perform some registrations...");
            for (Registration registration : serviceRegistration.getRegistrations()) {
                switch (registration.getRegistrar()) {
                    case CONSUL:
                        try {
                            HttpEntityEnclosingRequestBase request = ConsulRequestFactory.createRegisterRequest(registration, configurations.getConfigurations());
                            HttpResponse httpResponse = this.httpClient.execute(request);
                            LOGGER.info("... with success to {} -> {}", request.getURI().toString(), httpResponse.getStatusLine().getStatusCode());
                        } catch (Exception e) {
                            LOGGER.error("Failed to register for Registration", e);
                        }
                        break;
                    case EUREKA:
                        break;
                    default:
                        LOGGER.info("Unsupported registrar: {} -> ignored...", registration.getRegistrar().toString());
                }
            }
        } else {
            LOGGER.info("... No, not needed...");
        }
    }

    /**
     * De-register the mock as micro service with a service registration.
     *
     * Note that this is being called from the Camel's lifecycle management, i.e. on stop.
     */
    public void deregisterService() {
        LOGGER.info("Checking whether the service needs to be de-registered...");
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();
        ServiceRegistration serviceRegistration = configurations.getServiceRegistration();
        if (serviceRegistration != null) {
            LOGGER.info("... Yes, about to perform some de-registrations...");
            for (Registration registration : serviceRegistration.getRegistrations()) {
                switch (registration.getRegistrar()) {
                    case CONSUL:
                        try {
                            HttpEntityEnclosingRequestBase request = ConsulRequestFactory.createDeregisterRequest(registration);
                            HttpResponse httpResponse = this.httpClient.execute(request);
                            LOGGER.info("... with success to {} -> {}", request.getURI().toString(), httpResponse.getStatusLine().getStatusCode());
                        } catch (Exception e) {
                            LOGGER.error("Failed to deregister for Registration", e);
                        }
                        break;
                    case EUREKA:
                        break;
                    default:
                        LOGGER.info("Unsupported registrar: {} -> ignored...", registration.getRegistrar().toString());
                }
            }
        } else {
            LOGGER.info("... No, not needed...");
        }
    }

}
