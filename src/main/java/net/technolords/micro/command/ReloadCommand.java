package net.technolords.micro.command;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.HttpURLConnection;

public class ReloadCommand implements Command {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public String getId() {
        return Command.RELOAD;
    }

    @Override
    public ResponseContext executeCommand(Exchange exchange) {
        LOGGER.info("Called...");
        ResponseContext responseContext = new ResponseContext();
        try {
            ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
            configurationManager.reloadConfiguration();
            responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
            responseContext.setResponse("Reloaded...");
        } catch (IOException | SAXException | JAXBException e) {
            responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
            responseContext.setResponse(e.getMessage());
            responseContext.setErrorCode(String.valueOf(HttpURLConnection.HTTP_INTERNAL_ERROR));
        }
        return responseContext;
    }
}
