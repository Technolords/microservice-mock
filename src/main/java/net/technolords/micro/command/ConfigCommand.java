package net.technolords.micro.command;

import java.io.StringWriter;
import java.net.HttpURLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.registry.MockRegistry;

public class ConfigCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCommand.class);
    private static ConfigurationManager configurationManager;

    /**
     * Auxiliary method that executes the config command.
     *
     * @return
     *  The result of the log command.
     */
    public static ResponseContext executeCommand() {
        LOGGER.debug("Config command called");
        if (configurationManager == null) {
            configurationManager = MockRegistry.findConfigurationManager();
        }
        ResponseContext responseContext = new ResponseContext();
        try {
            responseContext.setContentType(ResponseContext.XML_CONTENT_TYPE);
            responseContext.setResponse(marshallToXML(configurationManager.getConfigurations()));
        } catch (JAXBException e) {
            responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
            responseContext.setResponse(e.getMessage());
            responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR));
        }
        return responseContext;
    }

    private static String marshallToXML(Configurations configurations) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        Marshaller marshaller = JAXBContext.newInstance(Configurations.class).createMarshaller();
        marshaller.marshal(configurations, stringWriter);
        return stringWriter.toString();
    }
}
