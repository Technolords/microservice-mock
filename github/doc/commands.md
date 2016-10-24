Back to [main](https://github.com/Technolords/microservice-mock).

# Commands
This following commands are supported:

* [stop](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#Stop)
* [log](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#Log)
* [stats](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#Stats)
* [reset](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#Reset)

## Stop
This command will stop the mock service, like:

    http://localhost:9090/mock/cmd?stop=now
    
At this moment the value is irrelevant and can be anything.

## Log
This command will change the log level, like:

    http://localhost:9090/mock/cmd?log=off
    
At this moment the follow values are accepted:

* error
* warn
* info
* debug
* off

## Stats
This command will report the statistics, like

    http://localhost:9090/mock/cmd?stats=html
    
At this moment the value can be html or anything else. The latter will return a single line with statistics in a single line (as text)

## Reset
This command will reset the statistics, like

    http://localhost:9090/mock/cmd?reset=now