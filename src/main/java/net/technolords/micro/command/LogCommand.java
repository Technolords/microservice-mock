package net.technolords.micro.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.log.LogManager;
import net.technolords.micro.processor.ResponseContext;

public class LogCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogCommand.class);

    public static ResponseContext executeCommand(String logLevel) {
        LOGGER.debug("Log command called, with log level {}", logLevel);
        return LogManager.changeLogLevel(logLevel);
    }

}
