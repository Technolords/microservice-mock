package net.technolords.micro.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.model.jaxb.registration.Registration;
import net.technolords.micro.model.jaxb.registration.ServiceRegistration;
import net.technolords.micro.test.PathSupport;

public class ConfigurationsTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATA_SET_FOR_CONFIG_FILES = "dataSetForConfigFiles";

    @DataProvider(name = DATA_SET_FOR_CONFIG_FILES)
    public Object[][] dataSetForDefaultConfiguration() throws IOException {
        return new Object[][]{
                { "config-for-registrations.xml", "192.168.10.14", 8500 },
        };
    }

    @Test (dataProvider = DATA_SET_FOR_CONFIG_FILES)
    public void testConfigurations(final String configuration, final String consulAddress, final int consulPort) throws JAXBException, IOException, SAXException {
        LOGGER.info("About to test with file: {}", configuration);
        Path pathToDirectory = PathSupport.getTestConfigResourcesForRegistration();
        Path pathToConfigurationFile = Paths.get(pathToDirectory.toString(), configuration);
        Assert.assertTrue(Files.exists(pathToConfigurationFile));
        ConfigurationManager configurationManager = new ConfigurationManager(pathToConfigurationFile.toString(), null);
        Configurations configurations = configurationManager.getConfigurations();
        Assert.assertNotNull(configurations);
        ServiceRegistration serviceRegistration = configurations.getServiceRegistration();
        Assert.assertNotNull(serviceRegistration);
        List<Registration> registrations = serviceRegistration.getRegistrations();
        Assert.assertNotNull(registrations);
        Assert.assertTrue(registrations.size() > 0);
        Registration registration= registrations.get(0);
        Assert.assertEquals(registration.getAddress(), consulAddress);
        Assert.assertEquals(registration.getPort(), consulPort);
    }

}
