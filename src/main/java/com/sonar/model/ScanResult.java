package com.sonar.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScanResult {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(ScanResult.class);

    /**
     * class level
     */
    private TreeMap<String, List<AuthorResult>> authorMapList = new TreeMap<>();

    private TreeMap<String, AuthorResult> totalTeamMap = new TreeMap<>();

    private TreeMap<String, AuthorResult> totalAuthorMap = new TreeMap<>();

    private List<ClazzResult> clazzResultList = new ArrayList<>();

    private AuthorResult total;

    private int totalLine;

    private int totalCoveredLine;

    private int totalChangeLine;

    private int totalChangeClz;

    interface Constant {
        String restTeam = "rest-members";
    }


    /**
     * each author total coverage, compliance
     */
    public void calcTotal(List<TeamResult> teamResultList, TeamProperties teamProperties) {
        totalTeamMap.clear();
        totalAuthorMap.clear();
        total = new AuthorResult();
        for (Map.Entry<String, List<AuthorResult>> entry : authorMapList.entrySet()) {
            String author = entry.getKey();
            List<AuthorResult> authorResultList = entry.getValue();
            totalAuthorMap.put(author, createAuthorResult(authorResultList, author));
            plus(total, authorResultList);
        }
        total.calcCompliance();
        total.calcCoverage();

        for (TeamResult teamResult : teamResultList) {
            if (CollectionUtils.isEmpty(teamResult.getAuthorList())) {
                logger.warn("no author found for -" + teamResult);
                continue;
            }
            AuthorResult teamLevel = new AuthorResult();
            for (String author : teamResult.getAuthorList()) {
                AuthorResult personLevel = totalAuthorMap.get(author);
                if (personLevel == null) {
                    personLevel = new AuthorResult();
                }
                personLevel.setPicked(true);
                plus(teamLevel, personLevel);
            }
            teamLevel.calcCompliance();
            teamLevel.calcCoverage();
            totalTeamMap.put(teamResult.getTeamName(), teamLevel);
        }
        List<String> othersList = new ArrayList<>();
        AuthorResult others = new AuthorResult();
        for (AuthorResult authorResult : totalAuthorMap.values()) {
            if (authorResult.isPicked()) {
                continue;
            }
            plus(others, authorResult);
            othersList.add(authorResult.getAuthor());
        }
        others.calcCompliance();
        others.calcCoverage();
        totalTeamMap.put(Constant.restTeam, others);
        teamProperties.getTeamMap().put(Constant.restTeam, othersList);
    }

    /**
     * @param list
     * @param author
     * @return
     */
    private AuthorResult createAuthorResult(List<AuthorResult> list, String author) {
        AuthorResult result = new AuthorResult();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (AuthorResult temp : list) {
            plus(result, temp);
        }
        result.calcCompliance();
        result.calcCoverage();
        result.setAuthor(author);
        return result;
    }


    /**
     * @param result
     * @param list
     */
    private void plus(AuthorResult result, List<AuthorResult> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (AuthorResult temp : list) {
            plus(result, temp);
        }
    }

    /**
     * @param result
     * @param temp
     */
    private void plus(AuthorResult result, AuthorResult temp) {
        result.plusTotalLine(temp.getTotalLine());
        result.plusTotalUtLine(temp.getTotalUtLine());
        result.plusTotalStaticLine(temp.getTotalStaticLine());
        result.plusTotalUtConditions(temp.getTotalUtConditions());
        result.plusTotalUtCoveredConditions(temp.getTotalUtCoveredConditions());
        result.plusTotalUtLineHits(temp.getTotalUtLineHits());
        result.plusTotalCoveredLine(temp.getTotalCoveredLine());
        result.addInfo(temp.getInfo());
        result.addMinor(temp.getMinor());
        result.addMajor(temp.getMajor());
        result.addCritical(temp.getCritical());
        result.addBlock(temp.getBlock());
    }

    public TreeMap<String, List<AuthorResult>> getAuthorMapList() {
        return authorMapList;
    }

    public void setAuthorMapList(TreeMap<String, List<AuthorResult>> authorMapList) {
        this.authorMapList = authorMapList;
    }

    public List<ClazzResult> getClazzResultList() {
        return clazzResultList;
    }

    public void setClazzResultList(List<ClazzResult> clazzResultList) {
        this.clazzResultList = clazzResultList;
    }

    public TreeMap<String, AuthorResult> getTotalAuthorMap() {
        return totalAuthorMap;
    }

    public void setTotalAuthorMap(TreeMap<String, AuthorResult> totalAuthorMap) {
        this.totalAuthorMap = totalAuthorMap;
    }

    public TreeMap<String, AuthorResult> getTotalTeamMap() {
        return totalTeamMap;
    }

    public void setTotalTeamMap(TreeMap<String, AuthorResult> totalTeamMap) {
        this.totalTeamMap = totalTeamMap;
    }

    public AuthorResult getTotal() {
        return total;
    }

    public void setTotal(AuthorResult total) {
        this.total = total;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(int totalLine) {
        this.totalLine = totalLine;
    }

    public int getTotalChangeLine() {
        return totalChangeLine;
    }

    public void setTotalChangeLine(int totalChangeLine) {
        this.totalChangeLine = totalChangeLine;
    }

    public int getTotalChangeClz() {
        return totalChangeClz;
    }

    public void setTotalChangeClz(int totalChangeClz) {
        this.totalChangeClz = totalChangeClz;
    }

    public void printTotal() {
        logger.info(total);
        logger.info(totalAuthorMap);
        logger.info(totalTeamMap);
    }

    public int getTotalCoveredLine() {
        return totalCoveredLine;
    }

    public void setTotalCoveredLine(int totalCoveredLine) {
        this.totalCoveredLine = totalCoveredLine;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "authorMapList=" + authorMapList +
                ", totalTeamMap=" + totalTeamMap +
                ", totalAuthorMap=" + totalAuthorMap +
                ", clazzResultList=" + clazzResultList +
                ", total=" + total +
                ", totalLine=" + totalLine +
                ", totalCoveredLine=" + totalCoveredLine +
                ", totalChangeLine=" + totalChangeLine +
                ", totalChangeClz=" + totalChangeClz +
                '}';
    }
}
