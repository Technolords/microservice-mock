package net.technolords.micro.config.jaxb.resource;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Technolords on 2016-Jul-21.
 */
public class ResourceGroup {
    private XpathConfig xpathConfig;
    private SimpleResource simpleResource;

    @XmlElement(name = "xpath")
    public XpathConfig getXpathConfig() {
        return xpathConfig;
    }

    public void setXpathConfig(XpathConfig xpathConfig) {
        this.xpathConfig = xpathConfig;
    }

    @XmlElement(name = "resource")
    public SimpleResource getSimpleResource() {
        return simpleResource;
    }

    public void setSimpleResource(SimpleResource simpleResource) {
        this.simpleResource = simpleResource;
    }
}