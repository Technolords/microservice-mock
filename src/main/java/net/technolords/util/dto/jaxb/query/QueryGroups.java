package net.technolords.util.dto.jaxb.query;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class QueryGroups {
    private List<QueryGroup> queryGroups;

    @XmlElement(name = "query-group")
    public List<QueryGroup> getQueryGroups() {
        return queryGroups;
    }

    public void setQueryGroups(List<QueryGroup> queryGroups) {
        this.queryGroups = queryGroups;
    }
}
