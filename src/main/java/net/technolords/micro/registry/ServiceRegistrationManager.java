package net.technolords.micro.registry;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.model.jaxb.registration.Registration;
import net.technolords.micro.model.jaxb.registration.ServiceRegistration;
import net.technolords.micro.registry.consul.ConsulRequestFactory;
import net.technolords.micro.registry.eureka.EurekaRequestFactory;

public class ServiceRegistrationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private HttpClient httpClient = HttpClientBuilder.create().build();

    public boolean renewalRequired() {
        LOGGER.info("Checking whether a renewal route should be created...");
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();
        ServiceRegistration serviceRegistration = configurations.getServiceRegistration();
        if (serviceRegistration != null) {
            for (Registration registration : serviceRegistration.getRegistrations()) {
                if (Registration.Registrar.EUREKA == registration.getRegistrar()) {
                    return true;
                }
            }
        }
        return false;
    }

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
                        this.registerForConsul(registration, configurations);
                        break;
                    case EUREKA:
                        this.registerForEureka(registration, configurations);
                        break;
                    default:
                        LOGGER.info("Unsupported registrar: {} -> ignored...", registration.getRegistrar().toString());
                }
            }
        } else {
            LOGGER.info("... No, not needed...");
        }
    }

    protected void registerForConsul(Registration registration, Configurations configurations) {
        try {
            HttpEntityEnclosingRequestBase request = ConsulRequestFactory.createRegisterRequest(registration, configurations.getConfigurations());
            LOGGER.debug("Request path: {}", request.getURI().toString());
            LOGGER.debug("Request body: {}", EntityUtils.toString(request.getEntity()));
            HttpResponse httpResponse = this.httpClient.execute(request);
            LOGGER.info("... with success to {} -> {}", request.getURI().toString(), httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            LOGGER.error("Failed to register for Registration", e);
        }
    }

    /**
     * This is called from the renewal processor.
     */
    public void registerForAllEureka() {
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();
        ServiceRegistration serviceRegistration = configurations.getServiceRegistration();
        if (serviceRegistration != null) {
            for (Registration registration : serviceRegistration.getRegistrations()) {
                switch (registration.getRegistrar()) {
                    case EUREKA:
                        this.registerForEureka(registration, configurations);
                    default:
                }
            }
        }
    }

    protected void registerForEureka(Registration registration, Configurations configurations) {
        try {
            HttpEntityEnclosingRequestBase request = EurekaRequestFactory.createRegisterRequest(registration, configurations.getConfigurations());
            LOGGER.debug("Request path: {}", request.getURI().toString());
            LOGGER.debug("Request body: {}", EntityUtils.toString(request.getEntity()));
            HttpResponse httpResponse = this.httpClient.execute(request);
            LOGGER.info("... with success to {} -> {}", request.getURI().toString(), httpResponse.getStatusLine().getStatusCode());
        } catch (IOException e) {
            LOGGER.error("Failed to register for Registration", e);
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
                        this.deRegisterForConsul(registration);
                        break;
                    case EUREKA:
                        this.deRegisterForEureka(registration, configurations);
                        break;
                    default:
                        LOGGER.info("Unsupported registrar: {} -> ignored...", registration.getRegistrar().toString());
                }
            }
        } else {
            LOGGER.info("... No, not needed...");
        }
    }

    protected void deRegisterForConsul(Registration registration) {
        try {
            HttpEntityEnclosingRequestBase request = ConsulRequestFactory.createDeregisterRequest(registration);
            HttpResponse httpResponse = this.httpClient.execute(request);
            LOGGER.info("... with success to {} -> {}", request.getURI().toString(), httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            LOGGER.error("Failed to deregister for Registration", e);
        }
    }

    protected void deRegisterForEureka(Registration registration, Configurations configurations) {
        try {
            HttpRequestBase request = EurekaRequestFactory.createDeRegisterRequest(registration, configurations.getConfigurations());
            HttpResponse httpResponse = this.httpClient.execute(request);
            LOGGER.info("... with success to {} -> {}", request.getURI().toString(), httpResponse.getStatusLine().getStatusCode());
        } catch (Exception e) {
            LOGGER.error("Failed to deregister for Registration", e);
        }
    }

}
