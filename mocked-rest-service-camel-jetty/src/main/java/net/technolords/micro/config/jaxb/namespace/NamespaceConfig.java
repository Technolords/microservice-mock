package net.technolords.micro.config.jaxb.namespace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by Technolords on 2016-Jul-25.
 */
public class NamespaceConfig {
    private String prefix;
    private String namespaceURI;

    @XmlAttribute(name = "prefix")
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @XmlValue
    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }
}
