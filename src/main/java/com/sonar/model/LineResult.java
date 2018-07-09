package com.sonar.model;

import java.util.Date;

public class LineResult {

    private int line;

    private String scmAuthor;

    private String scmRevision;

    private Date scmDate;

    private boolean duplicated;

    private String code;

    private Integer utLineHits;

    private Integer utConditions;

    private Integer utCoveredConditions;

    /**
     * CT + CF + LC
     *
     * @return
     */
    public int covered() {
        int sum = 0;
        if (utLineHits != null) {
            sum += utLineHits;
        }
        if (utCoveredConditions != null) {
            sum += utCoveredConditions;
        }
        return sum;
    }

    /**
     * 2*B + EL
     *
     * @return
     */
    public int toBeCovered() {
        int sum = 0;
        if (utLineHits != null) {
            sum += utLineHits;
        }
        if (utConditions != null) {
            sum += utConditions;
        }
        return sum;
    }

    /**
     * (CT + CF + LC) / (2*B + EL)
     *
     * @return
     */
    public double coverage() {
        int toBeCovered = toBeCovered();
        if (toBeCovered == 0) {
            return 0;
        }
        return Math.round(covered() / toBeCovered * 100);
    }


    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getScmAuthor() {
        return scmAuthor;
    }

    public void setScmAuthor(String scmAuthor) {
        this.scmAuthor = scmAuthor;
    }

    public String getScmRevision() {
        return scmRevision;
    }

    public void setScmRevision(String scmRevision) {
        this.scmRevision = scmRevision;
    }

    public Date getScmDate() {
        return scmDate;
    }

    public void setScmDate(Date scmDate) {
        this.scmDate = scmDate;
    }

    public boolean isDuplicated() {
        return duplicated;
    }

    public void setDuplicated(boolean duplicated) {
        this.duplicated = duplicated;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getUtLineHits() {
        return utLineHits;
    }

    public void setUtLineHits(Integer utLineHits) {
        this.utLineHits = utLineHits;
    }

    public Integer getUtConditions() {
        return utConditions;
    }

    public void setUtConditions(Integer utConditions) {
        this.utConditions = utConditions;
    }

    public Integer getUtCoveredConditions() {
        return utCoveredConditions;
    }

    public void setUtCoveredConditions(Integer utCoveredConditions) {
        this.utCoveredConditions = utCoveredConditions;
    }


    @Override
    public String toString() {
        return "LineResult{" +
                "line=" + line +
                ", scmAuthor='" + scmAuthor + '\'' +
                ", scmRevision='" + scmRevision + '\'' +
                ", scmDate=" + scmDate +
                ", duplicated=" + duplicated +
                ", code='" + code + '\'' +
                ", utLineHits=" + utLineHits +
                ", utConditions=" + utConditions +
                ", utCoveredConditions=" + utCoveredConditions +
                '}';
    }

    public static void main(String[] args) {
        System.out.println();
    }
}
