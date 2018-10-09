package net.technolords.micro.model.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.technolords.micro.model.jaxb.registration.ServiceRegistration;

@XmlRootElement(name = "configurations", namespace = "http://xsd.technolords.net")
public class Configurations {
    private ServiceRegistration serviceRegistration;
    private List<Configuration> configurations;

    @XmlElement (name = "service-registrations")
    public ServiceRegistration getServiceRegistration() {
        return serviceRegistration;
    }

    public void setServiceRegistration(ServiceRegistration serviceRegistration) {
        this.serviceRegistration = serviceRegistration;
    }

    @XmlElement (name = "configuration")
    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }
}
