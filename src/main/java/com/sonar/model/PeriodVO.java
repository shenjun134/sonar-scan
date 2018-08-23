package com.sonar.model;

import java.util.Date;

public class PeriodVO {
    private int index;
    private String value;

    private Date date;

    private String mode;

    private String parameter;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "PeriodVO{" +
                "index=" + index +
                ", value='" + value + '\'' +
                ", date=" + date +
                ", mode='" + mode + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
