package net.technolords.micro;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.camel.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.route.RestServiceRoute;

public class MockedRestService extends Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockedRestService.class);
    public static final String PROP_PORT = "port";
    public static final String PROP_CONFIG = "config";
    public static final String PROP_DATA = "data";
    private static final String DEFAULT_PORT = "9090";
    private String port;

    public MockedRestService(String port) {
        this.port = port;
    }

    /**
     * This method is invoked before the service is started, and performs some initialization steps. These
     * steps include:
     *
     * - Creation of beans, as well as adding these to the registry.
     * - Creation of the Camel routes, as well as adding these to the CamelContext.
     *
     * @throws JAXBException
     *  When creation the configuration bean fails.
     * @throws IOException
     *  When creation the configuration bean fails.
     * @throws SAXException
     *  When creation the configuration bean fails.
     */
    @Override
    public void beforeStart() throws JAXBException, IOException, SAXException {
        MockRegistry.registerBeansInRegistry(this);
        super.addRouteBuilder(new RestServiceRoute(this.port, MockRegistry.findConfigurationManager()));
    }

    /**
     * This method is invoked after the service is started, and logs a confirmation message.
     */
    @Override
    public void afterStart() {
        LOGGER.info("Mock service started, use CTRL-C to terminate JVM");
    }

    /**
     * Auxiliary method to start the micro service.
     *
     * @throws Exception
     *  When the micro service fails.
     */
    public void startService() throws Exception {
        super.run();
    }

    /**
     * The main executable.
     *
     * @param args
     *  The arguments.
     *
     * @throws Exception
     *  When the program fails.
     */
    public static void main(String[] args) throws Exception {
        LOGGER.info("About to start the mocked service...");
        String port = DEFAULT_PORT;
        if (System.getProperty(PROP_PORT) != null) {
            LOGGER.debug("Configured Port: {}", System.getProperty(PROP_PORT));
            port = System.getProperty(PROP_PORT);
        }
        if (System.getProperty(PROP_CONFIG) != null) {
            LOGGER.debug("Configured Config: {}", System.getProperty(PROP_CONFIG));
        }
        if (System.getProperty(PROP_DATA) != null) {
            LOGGER.debug("Configured data: {}", System.getProperty(PROP_DATA));
        }
        MockedRestService mockedRestService = new MockedRestService(port);
        mockedRestService.startService();
    }
}
