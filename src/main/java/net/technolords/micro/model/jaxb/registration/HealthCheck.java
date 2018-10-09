package net.technolords.micro.model.jaxb.registration;

import javax.xml.bind.annotation.XmlAttribute;

public class HealthCheck {
    private boolean enabled;
    private String interval;
    private String deRegisterAfter;

    @XmlAttribute (name = "enabled")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @XmlAttribute (name = "interval")
    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @XmlAttribute (name = "deregister-after")
    public String getDeRegisterAfter() {
        return deRegisterAfter;
    }

    public void setDeRegisterAfter(String deRegisterAfter) {
        this.deRegisterAfter = deRegisterAfter;
    }
}
