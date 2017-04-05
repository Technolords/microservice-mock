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

import net.technolords.micro.model.ResponseContext;

/**
 * This test is designed with two groups, where each group represents a specific
 * configuration of the ConfigurationManager:
 *
 * - default-configuration
 * - test-configuration
 */
public class ConfigurationManagerTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String DATA_SET_FOR_DEFAULT_CONFIG = "dataSetForDefaultConfig";
    private static final String DATA_SET_FOR_TEST_CONFIG = "dataSetForTestConfig";
    private static final String DEFAULT_CONFIG_MANAGER_REQUIRED = "default-configuration";
    private static final String TEST_CONFIG_MANAGER_REQUIRED = "test-configuration";
    private ConfigurationManager configurationManager;

    @BeforeGroups (groups = DEFAULT_CONFIG_MANAGER_REQUIRED)
    public void initDefaultConfigManager() throws JAXBException, IOException, SAXException {
        LOGGER.info("About to initialize default config manager");
        this.configurationManager = new ConfigurationManager(null, null);
        Assert.assertNotNull(this.configurationManager);
    }

    @BeforeGroups (groups = TEST_CONFIG_MANAGER_REQUIRED, inheritGroups = true)
    public void initTestConfigManager() throws JAXBException, IOException, SAXException {
        LOGGER.info("About to initialize test config manager");
        final String CONFIG_LOCATION = "src/test/resources/config/mock/config-for-ConfigurationManagerTest.xml";
        final String DATA_LOCATION = "src/test/resources/data/response";
        this.configurationManager = new ConfigurationManager(CONFIG_LOCATION, DATA_LOCATION);
        Assert.assertNotNull(this.configurationManager);
    }

    @DataProvider (name = DATA_SET_FOR_DEFAULT_CONFIG)
    public Object[][] dataSetForDefaultConfiguration() {
        return new Object[][] {
                { "/mock/get", defaultGetResponse() },
                { "/mock/*/get", defaultGetResponse() },
                { "/mock/1/get", defaultGetResponse() },
                { "/mock/2/get", defaultGetResponse() },
                { "/mock/3/get", defaultGetResponse() },
                { "/mock/4/get", defaultGetResponse() },
        };
    }

    private static String defaultGetResponse() {
        return "{\n" +
                "  \"label\" : \"get-request\",\n" +
                "  \"data\" : \"this is fun\"\n" +
                "}";
    }

    @Test (groups = DEFAULT_CONFIG_MANAGER_REQUIRED, dataProvider = DATA_SET_FOR_DEFAULT_CONFIG)
    public void testResponseWithDefaultConfiguration(final String path, final String expectedResponse) throws IOException, InterruptedException {
        LOGGER.debug("About to test with path: {}", path);
        ResponseContext responseContext = this.configurationManager.findResponseForGetOperationWithPath(path);
        Assert.assertNotNull(responseContext);
        Assert.assertNull(responseContext.getErrorCode());
        Assert.assertTrue(responseContext.getResponse().equals(expectedResponse));
    }

    @DataProvider(name = DATA_SET_FOR_TEST_CONFIG)
    public Object[][] dataSetForTestConfiguration() {
        return new Object[][] {
            { "/mock/get", "response/1" },
            { "/mock/*/get", "response/2" },
            { "/mock/1/get", "response/1" },
            { "/mock/2/get", "response/2" },
            { "/mock/3/get", "response/2" },
            { "/mock/4/get", "response/2" },
        };
    }

    @Test (groups = TEST_CONFIG_MANAGER_REQUIRED, dataProvider = DATA_SET_FOR_TEST_CONFIG)
    public void testResponseWithTestConfiguration(final String path, final String expectedResponse) throws IOException, InterruptedException {
        LOGGER.debug("About to test with path: {}", path);
        ResponseContext responseContext = this.configurationManager.findResponseForGetOperationWithPath(path);
        Assert.assertNotNull(responseContext);
        Assert.assertNull(responseContext.getErrorCode());
        Assert.assertTrue(responseContext.getResponse().equals(expectedResponse));
    }

}