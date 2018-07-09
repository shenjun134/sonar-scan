package com.sonar.model;

import java.util.ArrayList;
import java.util.List;

public class IssueFlow {

    private List<IssueLocation> locations = new ArrayList<>();

    public List<IssueLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<IssueLocation> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return "IssueFlow{" +
                "locations=" + locations +
                '}';
    }
}
