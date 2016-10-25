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

    /**
     * Default constructor which registers the start up properties.
     */
    public MockedRestService() {
        MockRegistry.registerPropertiesInRegistry(this);
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
        MockRegistry.registerBeansInRegistry();
        super.addRouteBuilder(new RestServiceRoute());
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
        MockedRestService mockedRestService = new MockedRestService();
        mockedRestService.startService();
    }
}
