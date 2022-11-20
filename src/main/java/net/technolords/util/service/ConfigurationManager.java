package net.technolords.util.service;

import net.technolords.util.dto.jaxb.Configuration;
import net.technolords.util.dto.jaxb.Configurations;
import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String PATH_TO_SCHEMA_FILE = "xsd/configuration.xsd";
    private final Map<String, Map<String, Configuration>> allConfigurations = new HashMap<>();
    private Configurations configurations;

    @Value("${camel.boot.config-file::config.xml}")
    private String pathToConfigFile;

    public void initializeConfiguration(CamelContext camelContext) throws VetoCamelContextStartException {
        LOGGER.info("About to initialize configuration from file: {}", this.pathToConfigFile);
        Path path = Paths.get(this.pathToConfigFile);
        if (!Files.exists(path)) {
            throw new VetoCamelContextStartException("config file does not exist: " + this.pathToConfigFile, camelContext);
        }
        LOGGER.info("Config file exists -> proceeding with XSD validation...");
        try {
            this.validateConfigFile(path);
        } catch (SAXException | IOException e) {
            throw new VetoCamelContextStartException(e.getMessage(), camelContext);
        }
        LOGGER.info("Config file is valid -> proceeding with loading endpoints...");
        try {
            this.initializeConfiguration(path);
        } catch (JAXBException | IOException e) {
            throw new VetoCamelContextStartException(e.getMessage(), camelContext);
        }
        LOGGER.info("Config file endpoints loaded...");
    }

    protected void validateConfigFile(Path pathToConfigFile) throws SAXException, IOException {
        LOGGER.info("About to validate the configuration...");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ClassPathResource classPathResource = new ClassPathResource(PATH_TO_SCHEMA_FILE);
        Source xsdSource = new StreamSource(classPathResource.getInputStream());
        Schema schema = schemaFactory.newSchema(xsdSource);
        Validator validator = schema.newValidator();
        Source sourceToConfig = new StreamSource(pathToConfigFile.toFile());
        validator.validate(sourceToConfig);
        LOGGER.info("... valid, proceeding...");
    }

    protected void initializeConfiguration(Path path) throws JAXBException, IOException {
        LOGGER.info("About to initialize the configuration...");
        Unmarshaller unmarshaller = JAXBContext.newInstance(Configurations.class).createUnmarshaller();
        this.configurations = (Configurations) unmarshaller.unmarshal(Files.newInputStream(path));
        LOGGER.debug("Total loaded resources: {}", this.configurations.getConfigurations().size());
        for (Configuration configuration : this.configurations.getConfigurations()) {
            String type = configuration.getType();
            Map<String, Configuration> foundConfigurationMap = this.allConfigurations.get(type);
            if (foundConfigurationMap == null) {
                foundConfigurationMap = new HashMap<>();
            }
            foundConfigurationMap.put(configuration.getUrl(), configuration);
            this.allConfigurations.put(type, foundConfigurationMap);
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("... done, found URL mappings:\n");
        this.allConfigurations.forEach((type, typeConfigurationMap) -> buffer.append("\ttype: ").append(String.format("%-20s", type)).append("total: ").append(typeConfigurationMap.size()).append("\n"));
        LOGGER.info(buffer.toString());
        // TODO: move this elsewhere -> inputMatchResolver class?
//        this.xpathEvaluator = new XpathEvaluator();
//        this.jsonPathEvaluator = new JsonPathEvaluator();
    }

    @PostConstruct
    public void report() {
        LOGGER.info("... created!");
    }
}
