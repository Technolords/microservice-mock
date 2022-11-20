package net.technolords.util.dto.jaxb.resource;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

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
