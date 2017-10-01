package net.technolords.micro.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.test.PathSupport;

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

    @AfterGroups (groups = DEFAULT_CONFIG_MANAGER_REQUIRED)
    public void afterDefaultGroup() {
        LOGGER.info("Testing done with default config manager....");
    }

    @BeforeGroups (groups = TEST_CONFIG_MANAGER_REQUIRED, inheritGroups = true)
    public void initTestConfigManager() throws JAXBException, IOException, SAXException {
        LOGGER.info("About to initialize test config manager");
        final String CONFIG_LOCATION = "src/test/resources/config/mock/config-for-ConfigurationManagerTest.xml";
        final String DATA_LOCATION = "src/test/resources/data/response";
        this.configurationManager = new ConfigurationManager(CONFIG_LOCATION, DATA_LOCATION);
        Assert.assertNotNull(this.configurationManager);
    }

    @AfterGroups (groups = TEST_CONFIG_MANAGER_REQUIRED)
    public void afterTestGroup() {
        LOGGER.info("Testing done with test config manager....");
    }

    /**
     * Auxiliary method to declare a data set to support testing of a configuration. An entry is specified
     * with two elements, each meaning:
     *
     *  [0] : The HTTP uri
     *  [1] : The expected message
     *
     * @return
     *  The data set.
     */
    @DataProvider (name = DATA_SET_FOR_DEFAULT_CONFIG)
    public Object[][] dataSetForDefaultConfiguration() throws IOException {
        return new Object[][] {
                { "/mock/get", defaultGetResponse() },
                { "/mock/*/get", defaultGetResponse() },
                { "/mock/1/get", defaultGetResponse() },
                { "/mock/2/get", defaultGetResponse() },
                { "/mock/3/get", defaultGetResponse() },
                { "/mock/4/get", defaultGetResponse() },
        };
    }

    private static String defaultGetResponse() throws IOException {
        Path pathToResponseFile = PathSupport.getPathToTestDataForResponseResources();
        Path pathToResource = Paths.get(pathToResponseFile.toString(), "get-3-for-ConfigurationManagerTest.json");
        Assert.assertTrue(Files.exists(pathToResource));
        return new String(Files.readAllBytes(pathToResource));
    }

    @Test (groups = DEFAULT_CONFIG_MANAGER_REQUIRED, dataProvider = DATA_SET_FOR_DEFAULT_CONFIG)
    public void testResponseWithDefaultConfiguration(final String path, final String expectedResponse) throws IOException, InterruptedException {
        LOGGER.debug("About to test with path: {}", path);
        this.assertOnResponseContext(path, expectedResponse);
    }

    /**
     * Auxiliary method to declare a data set to support testing of a configuration. An entry is specified
     * with two elements, each meaning:
     *
     *  [0] : The HTT uri
     *  [1] : The expected message
     *
     * @return
     *  The data set.
     */
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
        this.assertOnResponseContext(path, expectedResponse);
    }

    private void assertOnResponseContext(final String path, final String expectedResponse) throws IOException, InterruptedException {
        ResponseContext responseContext = this.configurationManager.findResponseForGetOperationWithPath(path, "");
        Assert.assertNotNull(responseContext);
        Assert.assertNull(responseContext.getErrorCode());
        Assert.assertTrue(filterWhiteSpace(responseContext.getResponse()).equals(filterWhiteSpace(expectedResponse)));
    }

    public String filterWhiteSpace(String input) {
        return input.replaceAll("\\s","");
    }

    // TODO: add new test with query groups

    @Test (groups = DEFAULT_CONFIG_MANAGER_REQUIRED)
    public void testExtractQueryParametersFromString() {
//        final String parameters = "key1=11&key2=12";
//        final String parameters = "key1=11";  // TODO: use data provider for all
        final String parameters = "a=b=c";
        Map<String, String> expected = new HashMap<>();
//        expected.put("key1", "11");
//        expected.put("key2", "12");
        Map<String, String> result = this.configurationManager.extractQueryParametersFromString(parameters);
        Assert.assertEquals(expected.size(), result.size());
        for (String key : expected.keySet()) {
            Assert.assertTrue(expected.get(key).equals(result.get(key)));
        }
    }

}