Back to [main](https://github.com/Technolords/microservice-mock).

# Logging
Every tool needs logging and this tool is no exception.

## History
Initially, the implementation of this mock was using the [logback](http://logback.qos.ch/) library. This library is dubbed successor of the log4j framework. However, after research I have switched back to log4j again. Or more specifically the next generation called [log4j2](http://logging.apache.org/log4j/2.x/).

Especially after reading that their implementation uses async log appenders, and optimized garbage collection it seems tailor made for high performance, high volume logging. See also: http://logging.apache.org/log4j/2.x/performance.html
 
## Usage
There is a default logging configuration embedded in the jar file. For many this is good enough, supporting almost all use cases.

### Default: log configuration:

    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="info">
        <Appenders>
            <Console name="console" target="SYSTEM_OUT">
                <PatternLayout pattern="%date{DEFAULT} [%level] [%thread] [%class{36}] %message%n"/>
            </Console>
        </Appenders>
        <Loggers>
            <Logger name="net.technolords.micro.filter" level="error" additivity="false">
                <AppenderRef ref="console"/>
            </Logger>
            <Root level="info">
                <AppenderRef ref="console"/>
            </Root>
        </Loggers>
    </Configuration>

Note that the default configuration will log from the filter only on error level.

It is possible to have your own log configuration, where you dictate fully the format and levels. See also: https://logging.apache.org/log4j/2.x/manual/layouts.html
In order to achieve this a system property needs to be specific on the command like:

    -Dlog4j.configurationFile=log4j2.xml

The value can be relative or an absolute path referring to your log configuration file.

### Example: log configuration which logs special information per request made on info level:

    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="info">
        <Appenders>
            <Console name="console" target="SYSTEM_OUT">
                <PatternLayout pattern="%date{DEFAULT} [%level] [%thread] [%class{36}] %message%n"/>
            </Console>
            <Console name="filterAppender" target="SYSTEM_OUT">
                <PatternLayout pattern="%d{UNIX_MILLIS} %X{httpUri} %X{httpStatus} %message%n"/>
            </Console>
        </Appenders>
        <Loggers>
            <Logger name="net.technolords.micro.filter" level="info" additivity="false">
                <AppenderRef ref="filterAppender"/>
            </Logger>
            <Root level="info">
                <AppenderRef ref="console"/>
            </Root>
        </Loggers>
    </Configuration>

Using the configuration above, it will produce output that looks like:

    1476992980855 /mock/cmd 200 16
    1476992981586 /mock/cmd 200 1
    
Note that specific lookups _%X{httpUri}_ and _%X{httpStatus}_ are part of the configuration. These lookups are supported by the current mock implementation. 
See also: https://logging.apache.org/log4j/2.x/manual/lookups.html
In addition, a specific appender is used, with _additivity_ false (this will prevents double log entries).

### Supported 
The following special lookups are supported:
* httpUri: will contain the URI part of the request made towards the mock service
* httpStatus: will contain the http status code aka response code (i.e. 200, 400, 500, etc)

## Reference
The following references are relevant:
* performance: https://logging.apache.org/log4j/2.x/performance.html
* appenders: https://logging.apache.org/log4j/2.x/manual/appenders.html
* layouts: https://logging.apache.org/log4j/2.x/manual/layouts.html
* lookups: https://logging.apache.org/log4j/2.x/manual/lookups.html

Back to [main](../README.md).