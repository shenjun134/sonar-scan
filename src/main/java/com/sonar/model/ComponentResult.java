package com.sonar.model;

import java.util.ArrayList;
import java.util.List;

public class ComponentResult {

    private String id;

    private String key;

    private String name;

    private String qualifier;

    /**
     * reliability_rating
     *
     * sqale_rating
     *
     * security_rating
     *
     */
    private List<String> ratingKey = new ArrayList<>();

    private List<MeasureVO> list = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public List<MeasureVO> getList() {
        return list;
    }

    public void setList(List<MeasureVO> list) {
        this.list = list;
    }


    public void add(MeasureVO measureO) {
        list.add(measureO);
    }

    @Override
    public String toString() {
        return "ComponentResult{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", list=" + list +
                '}';
    }
}
