package net.technolords.micro.command;

import net.technolords.micro.config.ConfigurationManager;
import net.technolords.micro.model.ResponseContext;
import net.technolords.micro.registry.MockRegistry;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadCommand implements Command {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public String getId() {
        return Command.RELOAD;
    }

    @Override
    public ResponseContext executeCommand(Exchange exchange) {
        LOGGER.info("Called...");
        ConfigurationManager configurationManager = MockRegistry.findConfigurationManager();
        ResponseContext responseContext = new ResponseContext();
        responseContext.setContentType(ResponseContext.PLAIN_TEXT_CONTENT_TYPE);
        responseContext.setResponse("Reloaded...");
        return responseContext;
    }
}
