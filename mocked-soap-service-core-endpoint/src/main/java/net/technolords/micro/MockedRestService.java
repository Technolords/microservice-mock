package net.technolords.micro;

import javax.xml.ws.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.api.MockedRestAPI;

/**
 * Created by Technolords on 2016-Jun-24.
 */
public class MockedRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockedRestService.class);

    public static void main(String[] args) {
        LOGGER.info("About to start endpoint...");
        Endpoint.publish("http://0.0.0.0:9090/", new MockedRestAPI());
        LOGGER.info("Endpoint stopped...");
    }

    // http://localhost:9090/mock?wsdl works!
}
