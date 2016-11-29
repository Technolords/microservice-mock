Back to [main](https://github.com/Technolords/microservice-mock).

# Commands
This following commands are supported (sorted by alphabet):

* [config](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#config)
* [log](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#log)
* [reset](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#reset)
* [stats](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#stats)
* [stop](https://github.com/Technolords/microservice-mock/blob/master/github/doc/commands.md#stop)

## Config
This command will show the current config, like:

    http://localhost:9090/mock/cmd?config=current
    
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

## Reset
This command will reset the statistics, like

    http://localhost:9090/mock/cmd?reset=now
    
At this moment the value is irrelevant and can be anything.

## Stats
This command will report the statistics, like

    http://localhost:9090/mock/cmd?stats=html
    
At this moment the value can be html or anything else. The latter will return a single line with statistics in a single line (as text)

## Stop
This command will stop the mock service, like:

    http://localhost:9090/mock/cmd?stop=now
    
At this moment the value is irrelevant and can be anything.