package com.sonar.model;

import java.util.Date;

public class ProjectSnapshot {


    private boolean canBeFavorite;

    private boolean isComparable;

    private boolean isFavorite;

    private String key;

    private String name;

    private Date snapshotDate;

    private String uuid;

    private String version;

    public boolean isCanBeFavorite() {
        return canBeFavorite;
    }

    public void setCanBeFavorite(boolean canBeFavorite) {
        this.canBeFavorite = canBeFavorite;
    }

    public boolean isComparable() {
        return isComparable;
    }

    public void setComparable(boolean comparable) {
        isComparable = comparable;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public Date getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Date snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ProjectSnapshot{" +
                "canBeFavorite=" + canBeFavorite +
                ", isComparable=" + isComparable +
                ", isFavorite=" + isFavorite +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", snapshotDate=" + snapshotDate +
                ", uuid='" + uuid + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
