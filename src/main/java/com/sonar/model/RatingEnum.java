package com.sonar.model;

public enum RatingEnum {

    RATING_A("background-color:#0a0;color:#fff", 1.0, "A"),

    RATING_B("background-color:#80cc00;color:#fff", 2.0, "B"),

    RATING_C("background-color:#fe0;color:#444", 3.0, "C"),

    RATING_D("background-color:#f77700;color:#fff", 4.0, "D"),

    RATING_E("background-color:#e00;color:#fff", 5.0, "E"),

    RATING_UNKNOWN("background-color:#444;color:#fff", -1.0, "NA"),;

    private String style;

    private double level;

    private String code;

    RatingEnum(String style, double level, String code) {
        this.style = style;
        this.level = level;
        this.code = code;
    }

    /**
     * @param level
     * @return
     */
    public static RatingEnum rateOf(double level) {
        for (RatingEnum temp : values()) {
            if (level - temp.level == 0) {
                return temp;
            }
        }
        return RATING_UNKNOWN;
    }


    public String getStyle() {
        return style;
    }


    public double getLevel() {
        return level;
    }


    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "RatingEnum{" +
                "style='" + style + '\'' +
                ", level=" + level +
                ", code='" + code + '\'' +
                '}';
    }
}
