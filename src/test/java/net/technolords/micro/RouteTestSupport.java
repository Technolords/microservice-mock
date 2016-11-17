package net.technolords.micro;

import net.technolords.micro.camel.RestServiceRoute;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;
import org.apache.camel.testng.CamelTestSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;


public class RouteTestSupport extends CamelTestSupport{
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteTestSupport.class);
    private Main main = new Main();
    private ProducerTemplate producerTemplate;

    @BeforeTest
    public void setUpToStartServer() throws Exception {
        main = new Main();
        MockRegistry.registerPropertiesInRegistry(main);
        MockRegistry.registerBeansInRegistryBeforeStart();
        main.addRouteBuilder(new RestServiceRoute());
        LOGGER.info("Added Route, main started: {}", main.isStarted());
        main.start();
        MockRegistry.registerBeansInRegistryAfterStart();
        producerTemplate = main.getCamelTemplate();
    }

}
