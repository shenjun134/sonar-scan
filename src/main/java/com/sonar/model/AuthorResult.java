package com.sonar.model;

import com.sonar.constant.SeverityEnum;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;

public class AuthorResult {
    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(AuthorResult.class);

    private String author;

    /**
     * class name
     */
    private String clazz;

    private String componentId;

    private String componentKey;

    /**
     * total commit line
     */
    private int totalLine;

    /**
     * ut covered + not need ut covered
     */
    private int totalCoveredLine;

    /**
     * total will be unit test hit line
     */
    private int totalUtLine;

    /**
     * total have be unit test hit line
     */
    private int totalUtLineHits;

    private int totalUtConditions;

    private int totalUtCoveredConditions;
    /**
     * no need unit test hit
     */
    private int totalStaticLine;
    /**
     * coverage%
     */
    private double coverage;

    /**
     * 100% - block * 10 - critical * 5
     */
    private int compliance;

    /**
     * issue count
     */


    private int block;

    private int critical;

    private int major;

    private int minor;

    private int info;

    private boolean picked;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentKey() {
        return componentKey;
    }

    public void setComponentKey(String componentKey) {
        this.componentKey = componentKey;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }

    public int getTotalUtLine() {
        return totalUtLine;
    }

    public void setTotalUtLine(int totalUtLine) {
        this.totalUtLine = totalUtLine;
    }

    public int getTotalUtLineHits() {
        return totalUtLineHits;
    }

    public void setTotalUtLineHits(int totalUtLineHits) {
        this.totalUtLineHits = totalUtLineHits;
    }

    public int getTotalUtConditions() {
        return totalUtConditions;
    }

    public void setTotalUtConditions(int totalUtConditions) {
        this.totalUtConditions = totalUtConditions;
    }

    public int getTotalUtCoveredConditions() {
        return totalUtCoveredConditions;
    }

    public void setTotalUtCoveredConditions(int totalUtCoveredConditions) {
        this.totalUtCoveredConditions = totalUtCoveredConditions;
    }

    public int getTotalStaticLine() {
        return totalStaticLine;
    }

    public void setTotalStaticLine(int totalStaticLine) {
        this.totalStaticLine = totalStaticLine;
    }

    public double getCoverage() {
        return coverage;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public int getCompliance() {
        return compliance;
    }

    public void setCompliance(int compliance) {
        this.compliance = compliance;
    }

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getInfo() {
        return info;
    }

    public void setInfo(int info) {
        this.info = info;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public void plusTotalLine(int count) {
        this.totalLine = this.totalLine + count;
    }

    public void plusTotalCoveredLine(int count) {
        this.totalCoveredLine = this.totalCoveredLine + count;
    }

    public void addTotalLine() {
        this.totalLine = this.totalLine + 1;
    }


    /**
     * sum for each author
     *
     * @param utLineHits
     */
    public void plusTotalUtLine(int utLineHits) {
        this.totalUtLine = this.totalUtLine + utLineHits;
    }

    public void plusTotalStaticLine(int totalStaticLine) {
        this.totalStaticLine = this.totalStaticLine + totalStaticLine;
    }

    /**
     * for each line with utLineHits tag
     *
     * @param utLineHits
     */
    public void addTotalUtLine(Integer utLineHits) {
        this.totalUtLine = this.totalUtLine + (utLineHits == null ? 0 : 1);
    }

    public void addTotalStaticLine(Integer utLineHits) {
        this.totalStaticLine = this.totalStaticLine + (utLineHits == null ? 1 : 0);
    }

    public void plusTotalUtLineHits(int utLineHits) {
        this.totalUtLineHits = this.totalUtLineHits + utLineHits;
    }

    public void addTotalUtLineHits(Integer utLineHits) {
//        this.totalUtLineHits = this.totalUtLineHits + (utLineHits == null ? 0 : utLineHits);
        if (utLineHits != null) {
            this.totalUtLineHits = this.totalUtLineHits + utLineHits;
        }
    }

    public int getTotalCoveredLine() {
        return totalCoveredLine;
    }

    public void setTotalCoveredLine(int totalCoveredLine) {
        this.totalCoveredLine = totalCoveredLine;
    }

    public void plusTotalUtConditions(int utConditions) {
        this.totalUtConditions = this.totalUtConditions + utConditions;
    }

    public void addTotalUtConditions(Integer utConditions) {
        this.totalUtConditions = this.totalUtConditions + (utConditions == null ? 0 : utConditions);
    }


    public void plusTotalUtCoveredConditions(int utCoveredConditions) {
        this.totalUtCoveredConditions = this.totalUtCoveredConditions + utCoveredConditions;
    }

    public void addTotalUtCoveredConditions(Integer utCoveredConditions) {
        this.totalUtCoveredConditions = this.totalUtCoveredConditions + (utCoveredConditions == null ? 0 : utCoveredConditions);
    }

    public void addTotalCoveredLine(int covered) {
        this.totalCoveredLine = this.totalCoveredLine + covered;
    }


    public void addBlock(int block) {
        this.block = this.block + block;
    }


    public void addCritical(int critical) {
        this.critical = this.critical + critical;
    }


    public void addMajor(int major) {
        this.major = this.major + major;
    }


    public void addMinor(int minor) {
        this.minor = this.minor + minor;
    }

    public void addInfo(int info) {
        this.info = this.info + info;
    }

    /**
     * @return
     */
    public double calcCoverage() {
        double willCovered = totalUtLine + totalStaticLine + totalUtConditions;
        if (willCovered == 0.0) {
            this.coverage = 100.00;
            logger.info("coverage: 0, " + this);
            return this.coverage;
        }
        double covered = totalUtLineHits + totalUtCoveredConditions + totalStaticLine;
        if(covered == 0.0){
            logger.info("coverage: 0, " + this);
        }
        this.coverage = ((double) Math.round(covered * 10000 / willCovered)) / 100;
        return this.coverage;
    }


    /**
     *
     */
    public double calcCompliance() {
        this.compliance = 100;
        this.compliance = this.compliance - this.block * SeverityEnum.BLOCKER.getPoint();
        this.compliance = this.compliance - this.critical * SeverityEnum.CRITICAL.getPoint();
        this.compliance = this.compliance - this.major * SeverityEnum.MAJOR.getPoint();
        this.compliance = this.compliance - this.minor * SeverityEnum.MINOR.getPoint();
        this.compliance = this.compliance - this.info * SeverityEnum.INFO.getPoint();
        return this.compliance;
    }


    @Override
    public String toString() {
        return "AuthorResult{" +
                "author='" + author + '\'' +
                ", clazz='" + clazz + '\'' +
                ", componentId='" + componentId + '\'' +
                ", componentKey='" + componentKey + '\'' +
                ", totalLine=" + totalLine +
                ", totalCoveredLine=" + totalCoveredLine +
                ", totalUtLine=" + totalUtLine +
                ", totalUtLineHits=" + totalUtLineHits +
                ", totalUtConditions=" + totalUtConditions +
                ", totalUtCoveredConditions=" + totalUtCoveredConditions +
                ", totalStaticLine=" + totalStaticLine +
                ", coverage=" + coverage +
                ", compliance=" + compliance +
                ", block=" + block +
                ", critical=" + critical +
                ", major=" + major +
                ", minor=" + minor +
                ", info=" + info +
                ", picked=" + picked +
                '}';
    }

    public static void main(String[] args) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        int num = 33;
        int num2 = 42;
        double result = (double) Math.round((double) (num) / (num2) * 10000) / 10;
        System.out.println(result);

        System.out.println(twoDForm.format(result));

        System.out.println(twoDForm.format(0));
        System.out.println(twoDForm.format(7.2));

        System.out.println(0 == 0.0);
        System.out.println(0 == 0);

    }
}
