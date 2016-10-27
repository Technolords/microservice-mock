package net.technolords.micro.command;

import net.technolords.micro.config.ConfigurationManager;
import org.apache.camel.Exchange;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class StopCommandTest{
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testStopCommand() throws IOException{
        String method = ConfigurationManager.HTTP_GET;
        String uri = "/mock/cmd?stop=now";
        Exchange exchange = this.generateExchange(method, uri);

        Assert.assertEquals(exchange.getContext().getStatus(), ServiceStatus.Stopped);
    }

    private Exchange generateExchange(String method, String uri) throws IOException {
        method = ConfigurationManager.HTTP_GET;
        uri = "/mock/cmd?stop=now";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader(Exchange.HTTP_METHOD, method);
        exchange.getIn().setHeader(Exchange.HTTP_URI, uri);
        return exchange;
    }

}