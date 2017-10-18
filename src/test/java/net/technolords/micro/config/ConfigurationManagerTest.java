package net.technolords.micro.config;

import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.test.PathSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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
    private static final String DATA_SET_FOR_TEST_QUERY_PARAMS = "dataSetForExtractParams";
    private static final String DATA_SET_FOR_TEST_QUERY_GROUPS = "dataSetForQueryGroups";
    private static final String TEST_QUERY_GROUP_CONFIG_MANAGER_REQUIRED = "test-queryGroup-configuration";
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

    @BeforeGroups (groups = TEST_QUERY_GROUP_CONFIG_MANAGER_REQUIRED, inheritGroups = true)
    public void initTestQueryGroupConfigManager() throws JAXBException, IOException, SAXException {
        LOGGER.info("About to initialize test queryGroup config manager");
        final String CONFIG_LOCATION = "src/test/resources/config/mock/test-configuration-with-queryParams.xml";
        final String DATA_LOCATION = "src/test/resources/data/response";
        this.configurationManager = new ConfigurationManager(CONFIG_LOCATION, DATA_LOCATION);
        Assert.assertNotNull(this.configurationManager);
    }

    @AfterGroups (groups = TEST_QUERY_GROUP_CONFIG_MANAGER_REQUIRED)
    public void afterTestQueryGroup() {
        LOGGER.info("Testing done with test queryGroup config manager....");
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
                { "/mock/get", defaultGetResponse("get-3-for-ConfigurationManagerTest.json") },
                { "/mock/*/get", defaultGetResponse("get-3-for-ConfigurationManagerTest.json") },
                { "/mock/1/get", defaultGetResponse("get-3-for-ConfigurationManagerTest.json") },
                { "/mock/2/get", defaultGetResponse("get-3-for-ConfigurationManagerTest.json") },
                { "/mock/3/get", defaultGetResponse("get-3-for-ConfigurationManagerTest.json") },
                { "/mock/4/get", defaultGetResponse("get-3-for-ConfigurationManagerTest.json") },
        };
    }

    private static String defaultGetResponse(final String responseFile) throws IOException {
        Path pathToResponseFile = PathSupport.getPathToTestDataForResponseResources();
        Path pathToResource = Paths.get(pathToResponseFile.toString(), responseFile);
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

    @DataProvider(name = DATA_SET_FOR_TEST_QUERY_GROUPS)
    public Object[][] dataSetForTestQueryGroup() throws IOException {
        return new Object[][] {
                { "/mock/get", "key1=value1&key2=value2", defaultGetResponse("sample-get-complex.json") },
                { "/mock/get", "customerNumber=122333", defaultGetResponse("sample-get-122333.json") },
                { "/mock/get", "", defaultGetResponse("sample-get-default.json")}
        };
    }

    @Test (groups = TEST_QUERY_GROUP_CONFIG_MANAGER_REQUIRED, dataProvider = DATA_SET_FOR_TEST_QUERY_GROUPS)
    public void testResponseWithTestQueryGroupConfiguration(final String path, final String params, final String expectedResponse) throws IOException, InterruptedException {
        LOGGER.debug("About to test with path: {}", path);
        ResponseContext responseContext = this.configurationManager.findResponseForGetOperationWithPath(path, params);
        Assert.assertNotNull(responseContext);
        Assert.assertNull(responseContext.getErrorCode());
        Assert.assertTrue(filterWhiteSpace(responseContext.getResponse()).equals(filterWhiteSpace(expectedResponse)));
    }

    @DataProvider(name = DATA_SET_FOR_TEST_QUERY_PARAMS)
    public Object[][] dataSetForExtractParams() {
        return new Object[][] {
                {"key1=11&key2=12", expectedQueryParams("key1=11&key2=12")},
                {"key1=11"        , expectedQueryParams("key1=11")},
                {"a=b=c"          , expectedQueryParams("")},
                {""               , expectedQueryParams("")}
        };
    }

    @Test (groups = DEFAULT_CONFIG_MANAGER_REQUIRED, dataProvider = DATA_SET_FOR_TEST_QUERY_PARAMS)
    public void testExtractQueryParametersFromString(final String parameters, final Map<String, String> expected) {
        Map<String, String> result = this.configurationManager.extractQueryParametersFromString(parameters);
        Assert.assertEquals(expected.size(), result.size());
        for (String key : expected.keySet()) {
            Assert.assertTrue(expected.get(key).equals(result.get(key)));
        }
    }

    private Map<String, String> expectedQueryParams(final String queryParams) {
        Map<String, String> expected = new HashMap<>();
        if (queryParams.isEmpty()) {
            return expected;
        }
        String[] pairs = queryParams.split("&");
        LOGGER.info("About to extract parameters from: {} -> total pairs: {}", queryParams, pairs.length);

        if (pairs.length > 0) {
            for (int i = 0 ; i < pairs.length; i++) {
                String key = pairs[i].substring(0, pairs[i].indexOf("="));
                String value = pairs[i].substring(pairs[i].indexOf("=") + 1, pairs[i].length());
                expected.put(key, value);
                LOGGER.info("Adding key/value: {} -> {}", key, value);
            }
        }
        return expected;
    }

}