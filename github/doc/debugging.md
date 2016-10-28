Back to [main](https://github.com/Technolords/microservice-mock).

# Debugging
Every tool needs debugging capabilities.

## Usage since v1.0.0
As the micro service uses the plain java virtual machine (JVM) as container, the standard debug options apply. This means additional CLI parameters need to be present before starting up, like:

    java -Xdebug -Xrunjdwp:transport=dt_socket,address=50000,server=y,suspend=n -jar ...
    
The example above mentions address, which is basically the port a debugger can connect to (such as an IDE).