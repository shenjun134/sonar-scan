package com.sonar.model;

import java.util.ArrayList;
import java.util.List;

public class TeamResult {

    private String teamName;

    private List<String> authorList = new ArrayList<>();

    public TeamResult(String teamName) {
        this.teamName = teamName;
    }

    public TeamResult(String teamName, List<String> authorList) {
        this.teamName = teamName;
        this.authorList = authorList;
    }

    public List<String> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<String> authorList) {
        this.authorList = authorList;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return "TeamResult{" +
                "teamName='" + teamName + '\'' +
                ", authorList=" + authorList +
                '}';
    }
}
