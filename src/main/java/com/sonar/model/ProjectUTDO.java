package com.sonar.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectUTDO {

    private Long id;

    private String name;

    private String kee;

    private PageDO pageDO;

    private UTDO baseComponent;

    /**
     * KEE, DO
     */
    private Map<String, UTDO> componentMap = new HashMap<>();

    private List<UTResult> failList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public PageDO getPageDO() {
        return pageDO;
    }

    public void setPageDO(PageDO pageDO) {
        this.pageDO = pageDO;
    }

    public UTDO getBaseComponent() {
        return baseComponent;
    }

    public void setBaseComponent(UTDO baseComponent) {
        this.baseComponent = baseComponent;
    }

    public Map<String, UTDO> getComponentMap() {
        return componentMap;
    }

    public void setComponentMap(Map<String, UTDO> componentMap) {
        this.componentMap = componentMap;
    }

    public ProjectUTDO() {
    }

    public ProjectUTDO(Long id, String name, String kee) {
        this.id = id;
        this.name = name;
        this.kee = kee;
    }

    public void add(UTDO utdo) {
        componentMap.put(utdo.getKee(), utdo);
    }


    public List<UTResult> getFailList() {
        return failList;
    }

    public void setFailList(List<UTResult> failList) {
        this.failList = failList;
    }

    @Override
    public String toString() {
        return "ProjectUTDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", kee='" + kee + '\'' +
                ", pageDO=" + pageDO +
                ", baseComponent=" + baseComponent +
                ", componentMap=" + componentMap +
                ", failList=" + failList +
                '}';
    }
}
