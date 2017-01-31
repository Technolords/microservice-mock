package net.technolords.micro.command;

import net.technolords.micro.RouteTestSupport;
import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.domain.jaxb.Configurations;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.Exchange;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class ConfigCommandTest extends RouteTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCommandTest.class);
    private static final String DATASET_FOR_TEST_CONFIGURATIONS = "dataSetForTestConfigurations";
    private Path pathToMockConfigurationsFolder;

    @BeforeTest
    public void setup() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        StringBuilder buffer = new StringBuilder();
        buffer.append("src").append(File.separator).append("test").append(File.separator);
        buffer.append("resources").append(File.separator);
        buffer.append("data").append(File.separator);
        buffer.append("mockConfigurations");
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
        Path pathToConfigFile = Paths.get(this.pathToMockConfigurationsFolder.toAbsolutePath().toString(), "default-configuration.xml");
        Assert.assertTrue(Files.exists(pathToConfigFile));
        String expectedConfig = new String(Files.readAllBytes(pathToConfigFile));
        assertXMLEqual(expectedConfig, actualConfig);
    }

    @DataProvider(name = DATASET_FOR_TEST_CONFIGURATIONS)
    public Object[][] dataSetConfigs(){
        return new Object[][] {
                {"configCommand-test-configuration.xml"}
        };
    }

    @Test(dataProvider = DATASET_FOR_TEST_CONFIGURATIONS)
    public void testWithLoadConfigurations(final String testConfigFile) throws Exception {
        LOGGER.debug("About to send Config command, total routes: {}", getProducerTemplate().getCamelContext().getRoutes().size());
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        Configurations configurations = configurationManager.getConfigurations();

        // Validate presence of config file
        Path pathToConfigFile = Paths.get(this.pathToMockConfigurationsFolder.toAbsolutePath().toString(), testConfigFile);
        Assert.assertTrue(Files.exists(pathToConfigFile));

        // Prepare new configuration
        configurations.getConfigurations().clear();
        configurations.getConfigurations().addAll(this.loadTestConfiguration(pathToConfigFile).getConfigurations());

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