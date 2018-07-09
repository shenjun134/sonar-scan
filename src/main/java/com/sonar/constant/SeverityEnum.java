package com.sonar.constant;

import org.apache.commons.lang.StringUtils;

public enum SeverityEnum {
    DEF("default", 0),

    INFO("INFO", 1),
    MINOR("MINOR", 2),
    MAJOR("MAJOR", 3),
    CRITICAL("CRITICAL", 5),
    BLOCKER("BLOCKER", 10),;


    private String code;

    private int point;


    SeverityEnum(String code, int point) {
        this.code = code;
        this.point = point;
    }

    /**
     * @param code
     * @return
     */
    public static SeverityEnum codeOf(String code) {
        if (StringUtils.isBlank(code)) {
            return DEF;
        }
        for (SeverityEnum severityEnum : values()) {
            if (StringUtils.equalsIgnoreCase(severityEnum.code, code)) {
                return severityEnum;
            }
        }
        return DEF;
    }

    public String getCode() {
        return code;
    }

    public int getPoint() {
        return point;
    }
}
