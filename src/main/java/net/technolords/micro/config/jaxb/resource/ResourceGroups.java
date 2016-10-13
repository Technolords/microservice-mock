package net.technolords.micro.config.jaxb.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ResourceGroups {
    private List<ResourceGroup> resourceGroup;

    public ResourceGroups() {
        this.resourceGroup = new ArrayList<>();
    }

    @XmlElement(name = "resource-group")
    public List<ResourceGroup> getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(List<ResourceGroup> resourceGroup) {
        this.resourceGroup = resourceGroup;
    }
}
