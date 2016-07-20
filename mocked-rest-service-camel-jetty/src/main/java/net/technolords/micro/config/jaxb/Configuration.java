package net.technolords.micro.config.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by Technolords on 2016-Jul-20.
 */
public class Configuration {
    private String type;
    private String url;

    @XmlAttribute(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
