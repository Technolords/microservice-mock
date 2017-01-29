package net.technolords.micro;

import net.technolords.micro.camel.RestServiceRoute;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.Main;
import org.apache.camel.testng.AvailablePortFinder;
import org.apache.camel.testng.CamelTestSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.util.Properties;

public class RouteTestSupport extends CamelTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteTestSupport.class);
    private Main main = new Main();
    private ProducerTemplate producerTemplate;
    private String availablePort;

    @BeforeSuite
    public void findAvailablePortNumber() {
        LOGGER.info("BeforeSuite called...");
        availablePort = String.valueOf(AvailablePortFinder.getNextAvailable(10000));
        LOGGER.info("Found port: {}", availablePort);
    }

    @BeforeTest
    public void setUpToStartServer() throws Exception {
        main = new Main();
        MockRegistry.registerPropertiesInRegistry(main);
        MockRegistry.registerBeansInRegistryBeforeStart();
        Properties properties = MockRegistry.findProperties();
        properties.put("port", availablePort);
        main.addRouteBuilder(new RestServiceRoute());
        LOGGER.info("Added Route, main started: {}", main.isStarted());
        main.start();
        MockRegistry.registerBeansInRegistryAfterStart();
        producerTemplate = main.getCamelTemplate();
    }

    public Main getMain() {
        return main;
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public String getAvailablePort() {
        return availablePort;
    }
}
