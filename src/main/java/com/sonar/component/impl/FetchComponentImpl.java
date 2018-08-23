package com.sonar.component.impl;

import com.sonar.component.FetchComponent;
import com.sonar.constant.SeverityEnum;
import com.sonar.constant.WebApi;
import com.sonar.model.*;
import com.sonar.util.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.sonar.wsclient.JdkUtils;
import org.sonar.wsclient.connectors.HttpClient4Connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FetchComponentImpl extends AbstractComponent implements FetchComponent {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(FetchComponentImpl.class);


    interface Constant {
        int retryTimes = 5;
    }


    private Date beginDate;

    private Date endDate;

    private String severityList;

    private TeamProperties teamProperties;

    public FetchComponentImpl(String webUser, String webHost, String webPassword) {
        this.webUser = webUser;
        this.webHost = webHost;
        this.webPassword = webPassword;
        util = new JdkUtils();
    }

    /**
     * @param clazz
     * @param connector
     * @return
     */
    @Override
    public ClazzResult fetchClazzLevel(ClazzDO clazz, HttpClient4Connector connector) {
        List<LineResult> lineResultList = fetchClazzLineResult(clazz, connector);
        if (CollectionUtils.isEmpty(lineResultList)) {
            return null;
        }
        ClazzResult clazzResult = new ClazzResult();
        int totalChangeLine = 0;
        int totalCoveredLine = 0;
        for (LineResult lineResult : lineResultList) {
            if (lineResult.getScmDate() == null || this.beginDate.after(lineResult.getScmDate()) || this.endDate.before(lineResult.getScmDate())) {
                logger.warn("this line is not matched with date range lineResult-" + lineResult);
                continue;
            }
            totalChangeLine = totalChangeLine + 1;
            AuthorResult authorResult = clazzResult.getAuthorMap().get(lineResult.getScmAuthor());
            if (authorResult == null) {
                authorResult = createAuthor(clazz);
                clazzResult.getAuthorMap().put(lineResult.getScmAuthor(), authorResult);
            }
            int covered = 0;
            if (lineResult.getUtLineHits() == null || lineResult.getUtLineHits() == 1) {
                covered = 1;
            }
            addLineResult(authorResult, lineResult, covered);
            totalCoveredLine = totalCoveredLine + covered;
        }
        clazzResult.setTotalLine(lineResultList.size());
        clazzResult.setTotalChangeLine(totalChangeLine);
        clazzResult.setTotalCoveredLine(totalCoveredLine);
        if (clazzResult.getAuthorMap().size() == 0) {
            logger.warn("no matched author for clazz - " + clazz);
            return null;
        }
        clazzResult.setLineResultList(lineResultList);
        List<IssueResult> issueResultList = fetchClazzIssueResult(clazz, connector);
        if (CollectionUtils.isNotEmpty(issueResultList)) {
            for (IssueResult issueResult : issueResultList) {
                AuthorResult authorResult = clazzResult.getAuthorMap().get(issueResult.getAuthor());
                if (authorResult == null) {
                    logger.warn("no author matched with - " + issueResult);
                    continue;
                }
                addIssueResult(authorResult, issueResult);
            }
        }
        for (AuthorResult authorResult : clazzResult.getAuthorMap().values()) {
            authorResult.calcCoverage();
            authorResult.calcCompliance();
        }
        return clazzResult;
    }


    @Override
    public List<LineResult> fetchClazzLineResult(ClazzDO clazz, HttpClient4Connector connector) {
        String url = webHost + WebApi.LINE_STATISTIC + clazz.getKey();
        String json = httpGet(url, connector);
        return parseLineResult(json, clazz);
    }

    @Override
    public List<IssueResult> fetchClazzIssueResult(ClazzDO clazz, HttpClient4Connector connector) {
//        String createdAfter = DateFormatUtils.format(this.beginDate, "yyyy-MM-dd");
//        String createdBefore = DateFormatUtils.format(this.endDate, "yyyy-MM-dd");
        String createdAfter = "";
        String createdBefore = "";
        String severity = StringUtils.isBlank(this.severityList) ? "" : this.severityList;
        String temp = WebApi.ISSUE_SEARCH;
        temp = StringUtils.replace(temp, "#createdAfter#", createdAfter);
        temp = StringUtils.replace(temp, "#createdBefore#", createdBefore);
        temp = StringUtils.replace(temp, "#componentKeys#", clazz.getKey());
        temp = StringUtils.replace(temp, "#severityList#", severity);

        String url = webHost + temp;
        String json = httpGet(url, connector);
        return parseIssueResult(json, clazz);
    }

    /*********************************************************private****************************************************/


    /**
     * @param clazz
     * @return
     */
    private AuthorResult createAuthor(ClazzDO clazz) {
        AuthorResult authorResult = new AuthorResult();
        authorResult.setClazz(clazz.getName());
        authorResult.setComponentId(clazz.getId());
        authorResult.setComponentKey(clazz.getKey());
        authorResult.setProjectId(clazz.getProjectId());
        return authorResult;
    }

    /**
     * @param authorResult
     * @param lineResult
     */
    private void addLineResult(AuthorResult authorResult, LineResult lineResult, int covered) {
        authorResult.addTotalLine();
        authorResult.addTotalUtLine(lineResult.getUtLineHits());
        authorResult.addTotalStaticLine(lineResult.getUtLineHits());
        authorResult.addTotalUtLineHits(lineResult.getUtLineHits());
        authorResult.addTotalUtConditions(lineResult.getUtConditions());
        authorResult.addTotalUtCoveredConditions(lineResult.getUtCoveredConditions());
        authorResult.addTotalCoveredLine(covered);
    }

    /**
     * INFO
     * MINOR
     * MAJOR
     * CRITICAL
     * BLOCKER
     *
     * @param authorResult
     * @param issueResult
     */
    private void addIssueResult(AuthorResult authorResult, IssueResult issueResult) {
        String severity = issueResult.getSeverity();
        if (StringUtils.isBlank(severity)) {
            logger.warn("no severity - " + issueResult);
            return;
        }
        int issueCount = 0;
        if (CollectionUtils.isEmpty(issueResult.getFlows())) {
            issueCount++;
        } else {
            //TODO
            issueCount++;
//            issueCount = issueResult.getFlows().size();
        }
        SeverityEnum severityEnum = SeverityEnum.codeOf(severity);
        switch (severityEnum) {
            case DEF:
                break;
            case INFO:
                authorResult.addInfo(issueCount);
                break;
            case MINOR:
                authorResult.addMinor(issueCount);
                break;
            case MAJOR:
                authorResult.addMajor(issueCount);
                break;
            case CRITICAL:
                authorResult.addCritical(issueCount);
                break;
            case BLOCKER:
                authorResult.addBlock(issueCount);
                break;
        }
    }


    /**
     * @param json
     * @param clazz
     */
    private List<LineResult> parseLineResult(String json, ClazzDO clazz) {
        List<LineResult> list = new ArrayList<>();
        if (StringUtils.isBlank(json)) {
            return list;
        }
        JSONObject content;
        try {
            content = (JSONObject) JSONValue.parse(json);
        } catch (Exception e) {
            logger.error("parseLineResult error with clazz: " + clazz, e);
            return list;
        }
        if (content == null) {
            return list;
        }
        for (Object oo : content.values()) {//total only one element
            JSONArray array;
            try {
                array = (JSONArray) JSONValue.parse(oo.toString());
            } catch (Exception e) {
                continue;
            }
            if (array == null) {
                continue;
            }
            parseLineArray(array, list);
        }
        return list;
    }

    /**
     * @param array
     * @param lineResultList
     */
    private void parseLineArray(JSONArray array, List<LineResult> lineResultList) {
        for (Object ar : array) {
            String scmAuthor = util.getString(ar, "scmAuthor");
            if (StringUtils.isBlank(scmAuthor)) {
                logger.warn("parseLineArray no scmAuthor");
                continue;
            }
            Integer line = util.getInteger(ar, "line");
            if (line == null) {
                logger.warn("parseLineArray no line");
                continue;
            }
            scmAuthor = convertAuthor(scmAuthor);

            Date scmDate = util.getDateTime(ar, "scmDate");
            String scmRevision = util.getString(ar, "scmRevision");
            boolean duplicated = util.getBoolean(ar, "duplicated");
            String code = util.getString(ar, "code");
            Integer utLineHits = util.getInteger(ar, "utLineHits");
            Integer utConditions = util.getInteger(ar, "utConditions");
            Integer utCoveredConditions = util.getInteger(ar, "utCoveredConditions");

            LineResult lineResult = new LineResult();
            lineResult.setCode(code);
            lineResult.setDuplicated(duplicated);
            lineResult.setLine(line);
            lineResult.setScmAuthor(scmAuthor);
            lineResult.setScmDate(scmDate);
            lineResult.setScmRevision(scmRevision);
            lineResult.setUtConditions(utConditions);
            lineResult.setUtCoveredConditions(utCoveredConditions);
            lineResult.setUtLineHits(utLineHits);
            lineResultList.add(lineResult);
        }
    }

    /**
     * @param json
     * @param clazz
     */
    private List<IssueResult> parseIssueResult(String json, ClazzDO clazz) {
        List<IssueResult> list = new ArrayList<>();
        if (StringUtils.isBlank(json)) {
            return list;
        }
        JSONObject content;
        try {
            content = (JSONObject) JSONValue.parse(json);
        } catch (Exception e) {
            logger.error("parseIssueResult error with clazz: " + clazz, e);
            return list;
        }
        if (content == null) {
            return list;
        }
        Object issuesObj = content.get("issues");
        if (issuesObj == null) {
            logger.warn("no issues here...");
            return list;
        }
        JSONArray issues = null;
        try {
            issues = (JSONArray) issuesObj;
        } catch (Exception e) {
            logger.error("parse issue obj error - " + issuesObj, e);
            return list;
        }
        if (issues == null) {
            logger.warn("no issues here - " + issuesObj);
            return list;
        }

        for (Object issue : issues) {//total only one element
            parseIssueObj(issue, list);
        }
        return list;
    }


    /**
     * @param issue
     * @param issueResultList
     */
    private void parseIssueObj(Object issue, List<IssueResult> issueResultList) {
        String author = util.getString(issue, "author");
        if (StringUtils.isBlank(author)) {
            logger.warn("parseIssueObj no author");
            return;
        }
        Integer line = util.getInteger(issue, "line");
        if (line == null) {
            logger.warn("parseIssueObj no line number");
            return;
        }
        author = convertAuthor(author);
        IssueResult issueResult = new IssueResult();
        Date creationDate = util.getDateTime(issue, "creationDate");
        Date updateDate = util.getDateTime(issue, "updateDate");
        String rule = util.getString(issue, "rule");
        String severity = util.getString(issue, "severity");
        String status = util.getString(issue, "status");
        String message = util.getString(issue, "message");
        String effort = util.getString(issue, "effort");
        String debt = util.getString(issue, "debt");
        String type = util.getString(issue, "type");

        Object textRangeObj = util.getField(issue, "textRange");
        Object flowsObj = util.getField(issue, "flows");
        issueResult.setRule(rule);
        issueResult.setLine(line);
        issueResult.setCreationDate(creationDate);
        issueResult.setUpdateDate(updateDate);
        issueResult.setSeverity(severity);
        issueResult.setStatus(status);
        issueResult.setMessage(message);
        issueResult.setEffort(effort);
        issueResult.setDebt(debt);
        issueResult.setType(type);
        issueResult.setAuthor(author);

        IssueTextRange textRange = parseTextRange(textRangeObj);
        if (textRange != null) {
            issueResult.setTextRange(textRange);
        }
        List<IssueFlow> flows = parseIssueFlow(flowsObj);
        if (CollectionUtils.isNotEmpty(flows)) {
            issueResult.setFlows(flows);
        }
        issueResultList.add(issueResult);
    }


    /**
     * @param json
     * @return
     */
    private IssueTextRange parseTextRange(Object json) {
        if (json == null || json.toString().trim().length() == 0) {
            return null;
        }
        try {
            IssueTextRange textRange = new IssueTextRange();
            Integer startLine = util.getInteger(json, "startLine");
            Integer endLine = util.getInteger(json, "endLine");
            Integer startOffset = util.getInteger(json, "startOffset");
            Integer endOffset = util.getInteger(json, "endOffset");
            if (startLine != null) {
                textRange.setStartLine(startLine);
            }
            if (endLine != null) {
                textRange.setEndLine(endLine);
            }
            if (startOffset != null) {
                textRange.setStartOffset(startOffset);
            }
            if (endOffset != null) {
                textRange.setEndOffset(endOffset);
            }
            return textRange;
        } catch (Exception e) {
            logger.error("parseTextRange error", e);
        }
        return null;
    }

    /**
     * @param flowsObj
     * @return
     */
    private List<IssueFlow> parseIssueFlow(Object flowsObj) {
        if (flowsObj == null || flowsObj.toString().trim().length() == 0) {
            return null;
        }
        List<IssueFlow> issueFlows = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) flowsObj;
            if (array == null) {
                return null;
            }
            for (Object obj : array) {
                //only one element  locations
                Object locationsObj = util.getField(obj, "locations");
                List<IssueLocation> issueLocations = parseIssueLocation(locationsObj);
                if (CollectionUtils.isEmpty(issueLocations)) {
                    continue;
                }
                IssueFlow flow = new IssueFlow();
                flow.setLocations(issueLocations);
                issueFlows.add(flow);
            }
        } catch (Exception e) {
            logger.error("parseIssueFlow error", e);
        }
        return issueFlows;
    }


    /**
     * @param locationsObj
     * @return
     */
    private List<IssueLocation> parseIssueLocation(Object locationsObj) {
        if (locationsObj == null) {
            return null;
        }
        try {
            JSONArray locations = (JSONArray) locationsObj;
            if (locations == null) {
                return null;
            }
            List<IssueLocation> list = new ArrayList<>();
            for (Object obj : locations) {
                IssueTextRange textRange = parseTextRange(util.getField(obj, "textRange"));
                if (textRange == null) {
                    continue;
                }
                String msg = util.getString(obj, "msg");
                IssueLocation issueLocation = new IssueLocation();
                issueLocation.setMsg(msg);
                issueLocation.setTextRange(textRange);
                list.add(issueLocation);
            }
            return list;
        } catch (Exception e) {
            logger.error("parseIssueLocation error", e);
        }
        return null;
    }

    /**
     * @param author
     * @return
     */
    private String convertAuthor(String author) {
        String tempAuthor = StringUtils.lowerCase(author);
        String employeeId = this.teamProperties.getMemberProperties().getTagMap().get(tempAuthor);
        if (StringUtils.isBlank(employeeId)) {
            return tempAuthor;
        }
        return employeeId;
    }


    /**********************************************************setter****************************************************/

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setSeverityList(String severityList) {
        this.severityList = severityList;
    }

    public void setTeamProperties(TeamProperties teamProperties) {
        this.teamProperties = teamProperties;
    }


    public static void main(String[] args) {

        System.out.println(Arrays.toString("sad|dasda|ss | ".split("\\|")));
        String lineResultFile = "line-result.txt";
        String issueResultFile = "issues-result.txt";
        FetchComponentImpl fetchComponent = new FetchComponentImpl(null, null, null);
        ClazzDO clazzDO = new ClazzDO();

//        String lineJson = FileUtils.loadFromFile(lineResultFile);
//        List<LineResult> lineResults = fetchComponent.parseLineResult(lineJson, clazzDO);
//        System.out.println(lineResults.size());
//        System.out.println(lineResults);
        TeamProperties teamProperties = new TeamProperties();
        fetchComponent.setTeamProperties(teamProperties);
        String issueJson = FileUtils.loadFromFile(issueResultFile);
        List<IssueResult> issueResults = fetchComponent.parseIssueResult(issueJson, clazzDO);
        ClazzResult clazzResult = new ClazzResult();
        for (IssueResult issueResult : issueResults) {
            AuthorResult authorResult = clazzResult.getAuthorMap().get(issueResult.getAuthor());
            if (authorResult == null) {
                logger.warn("no author matched with - " + issueResult);
                authorResult = new AuthorResult();
                authorResult.setAuthor(issueResult.getAuthor());
            }
            clazzResult.getAuthorMap().put(authorResult.getAuthor(), authorResult);
            fetchComponent.addIssueResult(authorResult, issueResult);
        }
        for (AuthorResult authorResult : clazzResult.getAuthorMap().values()) {
            authorResult.calcCoverage();
            authorResult.calcCompliance();
        }

        System.out.println(issueResults.size());
        System.out.println(issueResults);
        System.out.println(clazzResult);
    }

}
