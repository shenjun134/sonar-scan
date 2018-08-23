package com.sonar.util;

import com.sonar.constant.CoverageOptionEnum;
import com.sonar.model.AuthorResult;
import org.apache.log4j.Logger;

public class CoverageUtil {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(CoverageUtil.class);


    private static final CoverageUtil instance = new CoverageUtil();

    private CoverageOptionEnum coverageEnum;

    public static final void init(CoverageOptionEnum coverageEnum) {
        newInstance().setCoverageEnum(coverageEnum);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CoverageUtil.init " + newInstance().coverageEnum);
    }


    public static final CoverageUtil newInstance() {
        return instance;
    }

    public double coverage(AuthorResult authorResult) {
        if (newInstance().getCoverageEnum() == null) {
            logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!no coverageEnum set");
            return realCoverage(authorResult);
        }
        switch (newInstance().getCoverageEnum()) {
            case REAL:
                return realCoverage(authorResult);
            case OPTIMISTIC:
                return optimisticCoverage(authorResult);
            case PESSIMISTIC:
                return pressimiticCoverage(authorResult);
        }
        return realCoverage(authorResult);
    }

    /**
     * @param authorResult
     * @return
     */
    public double optimisticCoverage(AuthorResult authorResult) {
        double coverage = 100.00;
        double willCovered = authorResult.getTotalUtLine() + authorResult.getTotalStaticLine() + authorResult.getTotalUtConditions();
        if (willCovered == 0.0) {
            logger.info("coverage: 0, " + authorResult);
            return coverage;
        }
        double covered = authorResult.getTotalUtLineHits() + authorResult.getTotalStaticLine() + authorResult.getTotalUtCoveredConditions();
        if (covered == 0.0) {
            logger.info("coverage: 0, " + authorResult);
        }
        coverage = ((double) Math.round(covered * 10000 / willCovered)) / 100;
        return coverage;
    }


    /**
     * @param authorResult
     * @return
     */
    public double pressimiticCoverage(AuthorResult authorResult) {
        double coverage = 100.00;
        double willCovered = authorResult.getTotalUtLine() + 2 * authorResult.getTotalUtConditions();
        if (willCovered == 0.0) {
            logger.info("coverage: 0, " + authorResult);
            return coverage;
        }
        double covered = authorResult.getTotalUtLineHits() + authorResult.getTotalUtConditions();
        if (covered == 0.0) {
            logger.info("coverage: 0, " + authorResult);
        }
        coverage = ((double) Math.round(covered * 10000 / willCovered)) / 100;
        return coverage;
    }

    public double realCoverage(AuthorResult authorResult) {
        double coverage = 100.00;
        double willCovered = authorResult.getTotalUtLine() + authorResult.getTotalUtConditions();
        if (willCovered == 0.0) {
            logger.info("coverage: 0, " + authorResult);
            return coverage;
        }
        double covered = authorResult.getTotalUtLineHits() + authorResult.getTotalUtCoveredConditions();
        if (covered == 0.0) {
            logger.info("coverage: 0, " + authorResult);
        }
        coverage = ((double) Math.round(covered * 10000 / willCovered)) / 100;
        return coverage;
    }

    public void setCoverageEnum(CoverageOptionEnum coverageEnum) {
        this.coverageEnum = coverageEnum;
    }

    public CoverageOptionEnum getCoverageEnum() {
        return coverageEnum;
    }
}
