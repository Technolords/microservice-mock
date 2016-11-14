package net.technolords.micro.config;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import net.technolords.micro.domain.ResponseContext;

public class ConfigurationManagerTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATASET_FOR_GET_OPERATIONS = "dataSetForGetOperations";
    private static final String DEFAULT_CONFIG_MANAGER_REQUIRED = "defaultConfigurationManagerRequired";
    private static final String TEST_CONFIG_MANAGER_REQUIRED = "testConfigurationManagerRequired";
    private ConfigurationManager configurationManager;

    @BeforeGroups(groups = DEFAULT_CONFIG_MANAGER_REQUIRED)
    public void initDefaultConfigManager() throws JAXBException, IOException, SAXException {
        LOGGER.info("About to init simple config manager");
        this.configurationManager = new ConfigurationManager(null, null);
    }

    @BeforeGroups (groups = TEST_CONFIG_MANAGER_REQUIRED, inheritGroups = true)
    public void initTestConfigManager() throws JAXBException, IOException, SAXException {
        final String CONFIG_LOCATION = "src/test/resources/data/mockConfigurations/test-configuration.xml";
        final String DATA_LOCATION = "src/test/resources/data/mockResponses";
        this.configurationManager = new ConfigurationManager(CONFIG_LOCATION, DATA_LOCATION);
    }

    @Test (groups = DEFAULT_CONFIG_MANAGER_REQUIRED)
    public void testInitializationOfConfiguration() throws JAXBException, IOException, SAXException {
        Assert.assertNotNull(this.configurationManager, "Expected the configuration manager to be initialized...");
        LOGGER.debug("Test of initialization of config completed, no errors...");
    }

    @DataProvider(name = DATASET_FOR_GET_OPERATIONS)
    public Object[][] dataSetMock() {
        return new Object[][] {
            { "/mock/get", "response/1" },
            { "/mock/*/get", "response/2" },
            { "/mock/1/get", "response/1" },
            { "/mock/2/get", "response/2" },
            { "/mock/3/get", "response/2" },
            { "/mock/4/get", "response/2" },
        };
    }

    @Test (groups = TEST_CONFIG_MANAGER_REQUIRED, dataProvider = DATASET_FOR_GET_OPERATIONS)
    public void testResponseForGetOperations(final String path, final String expectedResponse) throws IOException, InterruptedException {
        LOGGER.debug("About to test with path: {}", path);
        ResponseContext responseContext = this.configurationManager.findResponseForGetOperationWithPath(path);
        Assert.assertNotNull(responseContext);
        Assert.assertNull(responseContext.getErrorCode());
        Assert.assertTrue(responseContext.getResponse().equals(expectedResponse));
    }

}