package com.sonar.convert;

import com.sonar.model.ProjectSnapshot;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Date;

public class SnapshotConverter extends BaseConverter {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(SnapshotConverter.class);

    public static ProjectSnapshot convert2Snapshot(String json) {
        ProjectSnapshot snapshot = new ProjectSnapshot();
        if (StringUtils.isBlank(json)) {
            return snapshot;
        }

        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parse(json);
            boolean canBeFavorite = getBoolean(jsonObj, "canBeFavorite");
            boolean isComparable = getBoolean(jsonObj, "isComparable");
            boolean isFavorite = getBoolean(jsonObj, "isFavorite");
            String key = getString(jsonObj, "key");
            String name = getString(jsonObj, "name");
            Date snapshotDate = getDate(jsonObj, "snapshotDate");
            String uuid = getString(jsonObj, "uuid");
            String version = getString(jsonObj, "version");

            snapshot.setCanBeFavorite(canBeFavorite);
            snapshot.setComparable(isComparable);
            snapshot.setFavorite(isFavorite);
            snapshot.setKey(key);
            snapshot.setName(name);
            snapshot.setSnapshotDate(snapshotDate);
            snapshot.setUuid(uuid);
            snapshot.setVersion(version);

        } catch (Exception e) {
            logger.error("convert2Snapshot error", e);
        }


        return snapshot;
    }
}
