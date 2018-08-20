package net.technolords.micro.test.factory;

import net.technolords.micro.model.jaxb.registration.HealthCheck;
import net.technolords.micro.model.jaxb.registration.Service;

public class ServiceFactory {

    /**
     * Create a service like:
     *
     *  <service address="192.168.10.10" port="9090" id="mock-1" name="mock-service" >
     *      <health-check enabled="true" interval="60s" deregister-after="90m"/>
     *  </service>
     *
     * @return
     *  A service
     */
    public static Service createService() {
        Service service = new Service();
        service.setId("mock-1");
        service.setName("mock-service");
        service.setAddress("192.168.10.10");
        service.setPort(9090);
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setEnabled(true);
        healthCheck.setInterval("30s");
        healthCheck.setDeRegisterAfter("90m");
        service.setHealthCheck(healthCheck);
        return service;
    }
}
