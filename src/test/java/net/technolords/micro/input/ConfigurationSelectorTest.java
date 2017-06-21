package net.technolords.micro.input;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.model.jaxb.Configuration;
import net.technolords.micro.model.jaxb.resource.SimpleResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigurationSelectorTest extends RouteTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSelectorTest.class);
    private static final String DATA_SET_FOR_TEST_CONFIGURATION_SELECTION = "dataSetForTestConfigurationSelection";
    private ConfigurationSelector configurationSelector = new ConfigurationSelector();
//    SimpleResource simpleResource = new SimpleResource();

    @DataProvider(name = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public Object[][] dataSetConfigs(){
        return new Object[][] {
                { "/mock/get",          getConfigs(),       expectedConfig("/mock/get", "GET", "configManagerTest/getResponse1.txt", null, null, null)},
                { "/mock/100/get",      getConfigs(),       expectedConfig("/mock/*/get", "GET", "configManagerTest/getResponse2.txt", null, null, null)},
                { "/mock/1/get",        getConfigs(),       expectedConfig("/mock/1/get", "GET", "configManagerTest/getResponse1.txt", null, null, null)},
                { "/mock/1/get/0/data", getConfigs(),       expectedConfig("/mock/*/get/*/data", "GET", "mock/sample-get.json", null, null, null)},
                { "/mock/post",         postConfigs1(),     expectedConfig("/mock/post", "POST", "mock/sample-post4.txt", null, "text/plain", null)},
                { "/mock/post",         postConfigs2(),     expectedConfig("/mock/post", "POST", "mock/sample-post3.json", "206", null, null)},
                { "/angry/kid",         postConfigs1(),     null},
        };
    }

    @Test(dataProvider = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public void testConfigurationSelection(final String url, final Map<String ,Configuration> testConfigs, final Configuration expectedConfiguration) throws IOException, JAXBException, SAXException, InterruptedException {
        for(String key : testConfigs.keySet()) {
            Configuration actualConfiguration = configurationSelector.findMatchingConfiguration(url, testConfigs);
            Assert.assertTrue(Objects.equals(actualConfiguration, expectedConfiguration));
        }
    }

    private Map<String,Configuration> getConfigs() {
        Map<String, Configuration> getConfigurations = new HashMap<>();
        getConfigurations.put("/mock/get", expectedConfig("/mock/get", "GET", "configManagerTest/getResponse1.txt", null, null, null));
        getConfigurations.put("/mock/*/get", expectedConfig("/mock/*/get", "GET", "configManagerTest/getResponse2.txt", null, null, null));
        getConfigurations.put("/mock/1/get", expectedConfig("/mock/1/get", "GET", "configManagerTest/getResponse1.txt", null, null, null));
        getConfigurations.put("/mock/*/get/*/data", expectedConfig("/mock/*/get/*/data", "GET", "mock/sample-get.json", null, null, null));
        return getConfigurations;
    }

    private Map<String, Configuration> postConfigs1() {
        Map<String, Configuration> postConfigurations = new HashMap<>();
        postConfigurations.put("/mock/post", expectedConfig("/mock/post", "POST", "mock/sample-post4.txt", null, "text/plain", null));
        return postConfigurations;
    }

    private Map<String, Configuration> postConfigs2() {
        Map<String, Configuration> postConfigurations = new HashMap<>();
        postConfigurations.put("/mock/post", expectedConfig("/mock/post", "POST", "mock/sample-post3.json", "206", null, null));
        return postConfigurations;
    }

    private Configuration expectedConfig(final String url, final String type, final String resource, final String errorcode, final String contentType, final String cachedData) {
        SimpleResource simpleResource = new SimpleResource();
        Configuration createdConfig = new Configuration();
        createdConfig.setUrl(url);
        createdConfig.setType(type);
        simpleResource.setResource(resource);
        simpleResource.setErrorCode(errorcode);
        simpleResource.setContentType(contentType);
        simpleResource.setCachedData(cachedData);
        createdConfig.setSimpleResource(simpleResource);
        return createdConfig;
    }
}