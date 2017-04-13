package net.technolords.micro.command;

import org.apache.camel.Exchange;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;

public class StopCommandTest extends RouteTestSupport {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test (description = "Test result of stop command")
    public void testStopCommand() throws Exception{
        String method = ConfigurationManager.HTTP_GET;
        String uri = "/mock/cmd";
        Exchange exchange = this.generateExchange(method, uri);
        LOGGER.info("About to stop, current current context: {} -> started: {}", super.getProducerTemplate().getCamelContext().getName(), getMain().isStarted());
        super.getProducerTemplate().send("direct:main", exchange);
        Thread.sleep(1000L);
        LOGGER.info("Exchange send, current context: {} -> started: {}", super.getProducerTemplate().getCamelContext().getName(), exchange.getContext().getStatus());
        Assert.assertEquals(exchange.getContext().getStatus(), ServiceStatus.Stopped);
    }

    private Exchange generateExchange(final String method, final String uri) throws Exception {
        LOGGER.info("Producer template: {}", super.getProducerTemplate());
        Exchange exchange = new DefaultExchange(super.getProducerTemplate().getCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        exchange.getIn().setHeader("stop", "now");
        return exchange;
    }
}