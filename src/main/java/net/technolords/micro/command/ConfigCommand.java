package net.technolords.micro.command;

import java.io.StringWriter;
import java.net.HttpURLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.model.jaxb.Configurations;
import net.technolords.micro.registry.MockRegistry;

public class ConfigCommand implements Command {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Auxiliary method to get the id associated with this command.
     *
     * @return
     *  The id associated with the command.
     */
    @Override
    public String getId() {
        return Command.CONFIG;
    }

    /**
     * Auxiliary method that executes the config command.
     *
     * @param exchange
     * The Camel Exchange associated with the config command.
     *
     * @return
     *  The result of the config command.
     */
    @Override
    public ResponseContext executeCommand(Exchange exchange) {
        LOGGER.debug("Config command called");
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
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
