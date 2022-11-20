package net.technolords.util.dto.jaxb.registration;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class ServiceRegistration {
    private List<Registration> registrations;

    @XmlElement(name = "registration")
    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }
}
