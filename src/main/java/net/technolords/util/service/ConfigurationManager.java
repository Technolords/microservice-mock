package net.technolords.util.service;

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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String PATH_TO_SCHEMA_FILE = "xsd/configuration.xsd";

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

    @PostConstruct
    public void report() {
        LOGGER.info("... created!");
    }
}
