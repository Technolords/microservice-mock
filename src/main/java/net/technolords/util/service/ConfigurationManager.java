package net.technolords.util.service;

import org.apache.camel.CamelContext;
import org.apache.camel.VetoCamelContextStartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConfigurationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);

    @Value("${camel.boot.config-file::config.xml}")
    private String pathToConfigFile;

    public void initializeConfiguration(CamelContext camelContext) throws VetoCamelContextStartException {
        LOGGER.info("About to initialize configuration from file: {}", this.pathToConfigFile);
        Path path = Paths.get(this.pathToConfigFile);
        if (!Files.exists(path)) {
            throw new VetoCamelContextStartException("config file does not exist: " + this.pathToConfigFile, camelContext);
        }
        LOGGER.info("Config file exists -> proceeding with XSD validation...");
    }

    @PostConstruct
    public void report() {
        LOGGER.info("... created!");
    }
}
