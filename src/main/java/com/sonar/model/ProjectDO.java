package com.sonar.model;

public class ProjectDO {

    private String name;

    private String kee;

    private Long id;

    public ProjectDO(String name, String kee, Long id) {
        this.name = name;
        this.kee = kee;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKee() {
        return kee;
    }

    public void setKee(String kee) {
        this.kee = kee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ProjectDO{" +
                "name='" + name + '\'' +
                ", kee='" + kee + '\'' +
                ", id=" + id +
                '}';
    }
}
