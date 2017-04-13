package net.technolords.micro.command;

import org.apache.camel.Exchange;

import net.technolords.micro.model.ResponseContext;

public interface Command {
    String CONFIG = "config";
    String LOG = "log";
    String RESET = "reset";
    String STATS = "stats";
    String STOP = "stop";

    String getId();
    ResponseContext executeCommand(Exchange exchange);
}
