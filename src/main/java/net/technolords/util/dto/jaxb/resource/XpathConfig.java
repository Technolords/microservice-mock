package net.technolords.util.dto.jaxb.resource;

import javax.xml.bind.annotation.XmlValue;

public class XpathConfig {
    private String xpath;

    @XmlValue
    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
}
