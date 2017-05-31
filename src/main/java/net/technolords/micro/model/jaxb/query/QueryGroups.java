package net.technolords.micro.model.jaxb.query;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class QueryGroups {
    private List<QueryGroup> queryGroups;

    public QueryGroups() {
        this.queryGroups = new ArrayList<>();
    }

    @XmlElement (name = "query-group")
    public List<QueryGroup> getQueryGroups() {
        return queryGroups;
    }

    public void setQueryGroups(List<QueryGroup> queryGroups) {
        this.queryGroups = queryGroups;
    }
}
