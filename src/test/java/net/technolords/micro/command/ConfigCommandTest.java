package net.technolords.micro.command;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.registry.MockRegistry;
import net.technolords.micro.test.PathSupport;

public class ConfigCommandTest extends RouteTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCommandTest.class);
    private static final String DATA_SET_FOR_TEST_CONFIGURATIONS = "dataSetForTestConfigurations";
    private Path pathToMockConfigurationsFolder;

    @BeforeTest (description = "Initialize XMLUnit")
    public void initializeXMLUnit() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
    }

    @BeforeTest (description = "Initialize configuration folder")
    public void initializeConfigurationFolder() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("src").append(File.separator);
        buffer.append("test").append(File.separator);
        buffer.append("resources").append(File.separator);
        buffer.append("config");
        this.pathToMockConfigurationsFolder = FileSystems.getDefault().getPath(buffer.toString());
    }

    @Test
    public void testInitialization() {
        LOGGER.info("About to test existence of folder: {}", this.pathToMockConfigurationsFolder);
        Assert.assertTrue(Files.exists(this.pathToMockConfigurationsFolder), "Folder is expected to exist");
    }

    @Test
    public void testConfigCommandWithDefault() throws Exception {
        LOGGER.info("About to send Config command, total routes: {}", getProducerTemplate().getCamelContext().getRoutes().size());
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Assert.assertNotNull(configurationManager);
        Exchange response = getProducerTemplate().request("jetty:http://localhost:" + getAvailablePort() + "/mock/cmd", exchange -> {
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
            exchange.getIn().setHeader("config", "current");
        });
        String actualConfig = response.getOut().getBody(String.class);
        LOGGER.debug("Got actual: {}", actualConfig);
        Path pathToConfigFile = Paths.get(PathSupport.getPathToTestConfigForMockResources().toString(), "config-1-for-ConfigCommandTest.xml");
        Assert.assertTrue(Files.exists(pathToConfigFile));
        String expectedConfig = new String(Files.readAllBytes(pathToConfigFile));
        LOGGER.debug("Got expected: {}", expectedConfig);
        assertXMLEqual(expectedConfig, actualConfig);
    }

    @DataProvider(name = DATA_SET_FOR_TEST_CONFIGURATIONS)
    public Object[][] dataSetConfigs(){
        return new Object[][] {
                { "config-2-for-ConfigCommandTest.xml" },
        };
    }

    @Test(dataProvider = DATA_SET_FOR_TEST_CONFIGURATIONS)
    public void testWithLoadConfigurations(final String testConfigFile) throws Exception {
        LOGGER.debug("About to send Config command, total routes: {}", getProducerTemplate().getCamelContext().getRoutes().size());
        // Validate presence of config file
        Path pathToConfigFile = Paths.get(PathSupport.getPathToTestConfigForMockResources().toString(), testConfigFile);
        Assert.assertTrue(Files.exists(pathToConfigFile));
        // Prepare new configuration
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();
        configurations.getConfigurations().clear();
        configurations.getConfigurations().addAll(this.loadTestConfiguration(pathToConfigFile).getConfigurations());
        // Request and assert
        Exchange response = getProducerTemplate().request("jetty:http://localhost:" + getAvailablePort() + "/mock/cmd", exchange -> {
            exchange.getIn().setHeader(Exchange.HTTP_METHOD, ConfigurationManager.HTTP_GET);
            exchange.getIn().setHeader("config", "current");
        });
        String actualConfig = response.getOut().getBody(String.class);
        String expectedConfig = new String(Files.readAllBytes(pathToConfigFile));
        assertXMLEqual(expectedConfig, actualConfig);
    }

    private Configurations loadTestConfiguration(Path pathToConfigFile) throws Exception {
        InputStream inputStream = Files.newInputStream(pathToConfigFile);
        Unmarshaller unmarshaller = JAXBContext.newInstance(Configurations.class).createUnmarshaller();
        Configurations configurations = (Configurations)unmarshaller.unmarshal(inputStream);
        LOGGER.info("Loaded Configurations = {}", configurations.getConfigurations().size());
        return configurations;
    }
}