package net.technolords.micro.command;

import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.log.LogManager;
import net.technolords.micro.model.ResponseContext;

public class LogCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCommand.class);

    /**
     * Auxiliary method to get the id associated with this command.
     *
     * @return
     *  The id associated with the command.
     */
    @Override
    public String getId() {
        return Command.LOG;
    }

    /**
     * Auxiliary method that executes the log command. For consistency this class is kept, but in reality it
     * is delegated to the LogManager which consolidates all logic (as well as the required classes) with the
     * underlying logging framework (other than the logging facade, i.e. SLF4J).
     *
     * @param exchange
     *  The Camel Exchange associated with the log command.
     *
     * @return
     *  The result of the log command.
     */
    @Override
    public ResponseContext executeCommand(Exchange exchange) {
        Map<String, Object> commands = exchange.getIn().getHeaders();
        String logLevel = (String) commands.get(Command.LOG);
        LOGGER.debug("Log command called, with log level {}", logLevel);
        return LogManager.changeLogLevel(logLevel);
    }

}
