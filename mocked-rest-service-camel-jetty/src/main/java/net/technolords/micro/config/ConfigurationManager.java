package net.technolords.micro.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.jaxb.Configurations;

/**
 * Created by Technolords on 2016-Jul-20.
 */
public class ConfigurationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String PATH_TO_CONFIG_FILE = "config/configuration.xml";
    private static final String PATH_TO_MOCKED_RESPONSE = "mock/remote-traxis-structure-service.json";
    private Configurations configurations = null;
    private String cachedResponse;

    public void initializeConfig() throws JAXBException {
        if (this.configurations == null) {
            LOGGER.info("About to initialize resources from configuration file...");
            Unmarshaller unmarshaller = JAXBContext.newInstance(Configurations.class).createUnmarshaller();
            this.configurations = (Configurations) unmarshaller.unmarshal(this.getClass().getClassLoader().getResourceAsStream(PATH_TO_CONFIG_FILE));
            LOGGER.info("Total loaded resources: {}", this.configurations.getConfigurations().size());
        }
    }

    public String findResponseForGetOperationWithPath(String path) {
        return null;
    }

    public String findResponseForPostOperationWithPathAndMessage(String path, String message) throws IOException, JAXBException {
        this.initializeConfig();
        return this.generateResponse();
    }

    protected String generateResponse() throws IOException, JAXBException {
        this.initializeConfig();
        if (this.cachedResponse == null) {
            InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PATH_TO_MOCKED_RESPONSE);
            LOGGER.info("Path to mocked file exists: {}",  fileStream.available());
            this.cachedResponse = new BufferedReader(new InputStreamReader(fileStream)).lines().collect(Collectors.joining("\n"));
        }
        return this.cachedResponse;
    }
}
