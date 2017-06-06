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

public class ConfigurationSelectorTest extends RouteTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSelectorTest.class);
    private static final String DATA_SET_FOR_TEST_CONFIGURATION_SELECTION = "dataSetForTestConfigurationSelection";
    private ConfigurationSelector configurationSelector = new ConfigurationSelector();
    SimpleResource simpleResource = new SimpleResource();

    @DataProvider(name = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public Object[][] dataSetConfigs(){
        return new Object[][] {
                { "/mock/get",          getConfigs(),       expectedConfig("/mock/get", "GET", "configManagerTest/getResponse1.txt")},
                { "/mock/100/get",      getConfigs(),       expectedConfig("/mock/*/get", "GET", "configManagerTest/getResponse2.txt")},
                { "/mock/1/get",        getConfigs(),       expectedConfig("/mock/1/get", "GET", "configManagerTest/getResponse1.txt")},
                { "/mock/1/get/0/data", getConfigs(),       expectedConfig("/mock/*/get/*/data", "GET", "mock/sample-get.json")},
                { "/mock/post",         postConfigs(),      expectedConfig("/mock/*/get/*/data", "POST", "mock/sample-post4.txt")},
                { "angry/kid",         postConfigs(),      null}
        };
    }

    @Test(dataProvider = DATA_SET_FOR_TEST_CONFIGURATION_SELECTION)
    public void testConfigurationSelection(String url, Map<String ,Configuration> testConfigs, Configuration expectedConfiguration) throws IOException, JAXBException, SAXException, InterruptedException {
        for(String key : testConfigs.keySet()) {
            Configuration actualConfiguration = configurationSelector.findMatchingConfiguration(url, testConfigs);
            Assert.assertEquals(actualConfiguration,expectedConfiguration);
        }
    }

    private Map<String,Configuration> getConfigs() {
        Map<String, Configuration> getConfigurations = new HashMap<>();
        getConfigurations.put("/mock/get", expectedConfig("/mock/get", "GET", "configManagerTest/getResponse1.txt"));
        getConfigurations.put("/mock/*/get", expectedConfig("/mock/*/get", "GET", "configManagerTest/getResponse2.txt"));
        getConfigurations.put("/mock/1/get", expectedConfig("/mock/1/get", "GET", "configManagerTest/getResponse1.txt"));
        getConfigurations.put("/mock/*/get/*/data", expectedConfig("/mock/*/get/*/data", "GET", "mock/sample-get.json"));
        return getConfigurations;
    }

    private Map<String, Configuration> postConfigs() {
        Map<String, Configuration> postConfigurations = new HashMap<>();
        postConfigurations.put("/mock/post", expectedConfig("/mock/*/get/*/data", "POST", "mock/sample-post4.txt"));
        return postConfigurations;
    }

    private Configuration expectedConfig(String url, String type, String resource) {
        Configuration createdConfig = new Configuration();
        createdConfig.setUrl(url);
        createdConfig.setType(type);
        simpleResource.setResource(resource);
        createdConfig.setSimpleResource(simpleResource);
        return createdConfig;
    }
}