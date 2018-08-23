package com.sonar.model;

public class MetricsDef {

    private boolean custom;

    private String description;

    private int direction;

    private String domain;

    private boolean hidden;

    private String id;

    private String key;

    private String name;

    private boolean qualitative;

    private String type;

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

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

    public boolean isQualitative() {
        return qualitative;
    }

    public void setQualitative(boolean qualitative) {
        this.qualitative = qualitative;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MetricsDef{" +
                "custom=" + custom +
                ", description='" + description + '\'' +
                ", direction=" + direction +
                ", domain='" + domain + '\'' +
                ", hidden=" + hidden +
                ", id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", qualitative=" + qualitative +
                ", type='" + type + '\'' +
                '}';
    }
}
