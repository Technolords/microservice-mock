package net.technolords.micro.command;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import org.apache.camel.Exchange;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StopCommandTest extends RouteTestSupport {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testStopCommand() throws Exception{
        String method = ConfigurationManager.HTTP_GET;
        String uri = "/mock/cmd?stop=now";
        Exchange exchange = this.generateExchange(method, uri);
        LOGGER.info("About to stop, current current context: {} -> started: {}", exchange.getContext().getName(), getMain().isStarted());
//        LOGGER.info("About to stop, current current context: {} -> started: {}", getProducerTemplate().getCamelContext().getName(), getMain().isStarted());
        getProducerTemplate().send("direct:main", exchange);
        Thread.sleep(1000L);
        LOGGER.info("Exchange send, current context: {} -> started: {}", exchange.getContext().getName(), exchange.getContext().getStatus());
//        LOGGER.info("Exchange send, current context: {} -> started: {}", getProducerTemplate().getCamelContext().getName(), exchange.getContext().getStatus());
        Assert.assertEquals(exchange.getContext().getStatus(), ServiceStatus.Stopped);
    }

    private Exchange generateExchange(String method, String uri) throws Exception {
        method = ConfigurationManager.HTTP_GET;
        uri = "/mock/cmd";
//        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Exchange exchange = new DefaultExchange(getProducerTemplate().getCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        exchange.getIn().setHeader("stop", "now");
        return exchange;
    }
}