package net.technolords.micro;

import java.util.Properties;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.main.Main;
import org.apache.camel.testng.AvailablePortFinder;
import org.apache.camel.testng.CamelTestSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

import net.technolords.micro.camel.listener.MockMainListener;
import net.technolords.micro.camel.route.MockRoute;
import net.technolords.micro.registry.MockRegistry;

public class RouteTestSupport extends CamelTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteTestSupport.class);
    private Main main;
    private ProducerTemplate producerTemplate;
    private String availablePort;

    @BeforeClass
    public void findAvailablePortNumber() throws Exception {
        LOGGER.info("BeforeSuite called...");
        this.availablePort = String.valueOf(AvailablePortFinder.getNextAvailable(10000));
        LOGGER.info("Found port: {}", this.availablePort);
        this.main = new Main();
        MockRegistry.registerPropertiesInRegistry(this.main);
        MockRegistry.registerBeansInRegistryBeforeStart();
        Properties properties = MockRegistry.findProperties();
        properties.put("port", this.availablePort);
        this.main.addMainListener(new MockMainListener());
        this.main.addRouteBuilder(new MockRoute());
        this.main.start();
        LOGGER.info("Main started: {}", this.main.isStarted());
        MockRegistry.registerBeansInRegistryAfterStart();
        this.producerTemplate = this.main.getCamelTemplate();
    }

    public Main getMain() {
        return this.main;
    }

    public ProducerTemplate getProducerTemplate() {
        return this.producerTemplate;
    }

    public String getAvailablePort() {
        return this.availablePort;
    }
}
