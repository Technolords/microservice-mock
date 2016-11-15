package net.technolords.micro.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.log.LogManager;
import net.technolords.micro.domain.ResponseContext;

public class LogCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCommand.class);

    /**
     * Auxiliary method that executes the log command. For consistency this class is kept, but in reality it
     * is delegated to the LogManager which consolidates all logic (as well as the required classes) with the
     * underlying logging framework (other than the logging facade, i.e. SLF4J).
     *
     * @param logLevel
     *  The log level to set.
     *
     * @return
     *  The result of the log command.
     */
    public static ResponseContext executeCommand(String logLevel) {
        LOGGER.debug("Log command called, with log level {}", logLevel);
        return LogManager.changeLogLevel(logLevel);
    }

}
