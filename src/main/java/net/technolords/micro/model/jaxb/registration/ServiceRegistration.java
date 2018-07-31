package net.technolords.micro.model.jaxb.registration;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ServiceRegistration {
    private List<Registration> registrations;

    @XmlElement (name = "registration")
    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

}
