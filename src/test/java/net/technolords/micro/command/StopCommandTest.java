package net.technolords.micro.command;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.route.RestServiceRoute;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.main.Main;
import org.apache.camel.testng.CamelTestSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class StopCommandTest extends CamelTestSupport {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Main main;
    private ProducerTemplate producerTemplate;

    @BeforeTest
    public void setUp() throws Exception {
        main = new Main();
        MockRegistry.registerPropertiesInRegistry(main);
        MockRegistry.registerBeansInRegistryBeforeStart();
        main.addRouteBuilder(new RestServiceRoute());
        LOGGER.info("Added Route, main started: {}", main.isStarted());
        main.start();
        producerTemplate = main.getCamelTemplate();
    }

    @Test
    public void testStopCommand() throws Exception{
        String method = ConfigurationManager.HTTP_GET;
        String uri = "/mock/cmd?stop=now";
        Exchange exchange = this.generateExchange(method, uri);
        LOGGER.info("About to stop, current current context: {} -> started: {}", producerTemplate.getCamelContext().getName(), main.isStarted());
        producerTemplate.send("direct:main", exchange);
        Thread.sleep(1000L);
        LOGGER.info("Exchange send, current context: {} -> started: {}", producerTemplate.getCamelContext().getName(), exchange.getContext().getStatus());
        Assert.assertEquals(exchange.getContext().getStatus(), ServiceStatus.Stopped);
    }

    private Exchange generateExchange(String method, String uri) throws Exception {
        method = ConfigurationManager.HTTP_GET;
        uri = "/mock/cmd";
        Exchange exchange = new DefaultExchange(producerTemplate.getCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        exchange.getIn().setHeader("stop", "now");
        return exchange;
    }
}