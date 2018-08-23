package com.sonar.model;

public class UTDO {

    /**
     * AWH-M0sVBzQKWkhsDPxM
     */
    private String id;

    /**
     * UnitTestBase.java
     */
    private String name;

    /**
     * UTS
     */
    private String qualifier;

    /**
     * src/test/java/com/statestr/gcth/usecase/coa/common/processor/UnitTestBase.java
     */
    private String path;

    /**
     * java
     */
    private String language;

    private String kee;

    /**
     * Total unit test count
     */
    private int total;

    /**
     * failure unit test count
     */
    private int failure;

    /**
     * success rate
     */
    private float succRate;

    private int error;

    public String getKee() {
        return kee;
    }

    public void setKee(String kee) {
        this.kee = kee;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public float getSuccRate() {
        return succRate;
    }

    public void setSuccRate(float succRate) {
        this.succRate = succRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "UTDO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", path='" + path + '\'' +
                ", language='" + language + '\'' +
                ", kee='" + kee + '\'' +
                ", total=" + total +
                ", failure=" + failure +
                ", succRate=" + succRate +
                ", error=" + error +
                '}';
    }
}
