package net.technolords.micro.config.jaxb.resource;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Technolords on 2016-Jul-21.
 */
public class Complex {
    private Xpath xpath;
    private Simple resource;

    @XmlElement(name = "xpath")
    public Xpath getXpath() {
        return xpath;
    }

    public void setXpath(Xpath xpath) {
        this.xpath = xpath;
    }

    @XmlElement(name = "resource")
    public Simple getResource() {
        return resource;
    }

    public void setResource(Simple resource) {
        this.resource = resource;
    }
}

/*
    <resource>
        <xpath>/sample/message[@id = '1']</xpath>
        <resource>mock/sample-post1.json</resource>
    </resource>
 */