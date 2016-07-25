package net.technolords.micro.config.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Technolords on 2016-Jul-20.
 */
@XmlRootElement(name = "configurations", namespace = "http://xsd.technolords.net")
public class Configurations {

    private List<Configuration> configurations;

    @XmlElement(name = "configuration")
    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }
}
