package net.technolords.micro.registry.consul;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.registration.HealthCheck;
import net.technolords.micro.model.jaxb.registration.Service;
import net.technolords.micro.test.PathSupport;

public class RegistrationPayloadFactoryTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testPayloadGenerationForRegister() throws IOException {
        Path resources = PathSupport.getPathToTestResources();
        Path jsonFile = Paths.get(resources.toString(), "/json/consul-payload-register.json");
        LOGGER.debug("Json file exists: {}", Files.exists(jsonFile));
        String expected = new String(Files.readAllBytes(jsonFile));
        String actual = ConsulPayloadFactory.generatePayloadForRegister(this.createService(), this.createConfigurations());
        Assert.assertEquals(this.filterWhitespace(actual), this.filterWhitespace(expected));
    }

    /**
     * Auiliary method to filter some white space (spaces and carriage return)
     *
     * @param original
     *  The original to be filtered.
     *
     * @return
     *  The filtered original.
     */
    protected String filterWhitespace(String original) {
        return original.replaceAll(" |\n", "");
    }

    /**
     * Create a service like:
     *
     *  <service address="192.168.10.10" port="9090" id="mock-1" name="mock-service" >
     *      <health-check enabled="true" interval="60s" deregister-after="90m"/>
     *  </service>
     *
     * @return
     *  A service
     */
    protected Service createService() {
        Service service = new Service();
        service.setId("mock-1");
        service.setName("mock-service");
        service.setAddress("192.168.10.10");
        service.setPort(9090);
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setEnabled(true);
        healthCheck.setInterval("30s");
        healthCheck.setDeRegisterAfter("90m");
        service.setHealthCheck(healthCheck);
        return service;
    }

    protected List<Configuration> createConfigurations() {
        List<Configuration> configurations = new ArrayList<>();
        // Get
        Configuration configuration = new Configuration();
        configuration.setType("get");
        configuration.setUrl("/mock/get");
        configurations.add(configuration);
        // Post 1
        configuration = new Configuration();
        configuration.setType("post");
        configuration.setUrl("/mock/post1");
        configurations.add(configuration);
        // Post 2
        configuration = new Configuration();
        configuration.setType("post");
        configuration.setUrl("/mock/post2");
        configurations.add(configuration);
        return configurations;
    }

}