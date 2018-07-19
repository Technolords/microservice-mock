package net.technolords.micro.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegistrationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ServiceRegistrationManager() {
        LOGGER.info("Bean instantiated...");
    }


}

/*
{
  "ID": "mock-1",
  "Name": "mock-service",
  "Tags": [
    "mock"
  ],
  "Address": "192.168.10.10",
  "Port": 9090,
  "Meta": {
  	"get": "/mock/get",
    "post": "/mock/post1, /mock/post2"
  },
  "EnableTagOverride": false,
  "Check": {
    "DeregisterCriticalServiceAfter": "90m",
    "HTTP": "http://192.168.10.10:9090/mock/cmd?config=current",
    "Interval": "30s"
  }
}
 */