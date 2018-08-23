package com.sonar.service;

import com.sonar.constant.SeverityEnum;
import com.sonar.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class HtmlService {
    private static final Logger logger = Logger.getLogger(HtmlService.class);
    private SonarProperties sonarProperties;
    private TeamProperties teamProperties;
    private ReportResult reportResult;

    public HtmlService(SonarProperties sonarProperties, TeamProperties teamProperties, ReportResult reportResult) {
        this.sonarProperties = sonarProperties;
        this.teamProperties = teamProperties;
        this.reportResult = reportResult;
    }

    interface Constant {
        String summary = "#SUMMARY_PART#";
        String detail = "#DETAIL_PART#";
        String template = "report-template.htm";
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
    }

    public void generateReport() throws IOException {
        logger.info("begin to generateReport - " + reportResult);
        String path = System.getProperty("user.dir");
        BufferedReader htmlTemplate = null;
        StringBuilder template = new StringBuilder();
        try {
            String templateName = sonarProperties.getTemplateName();
            if (StringUtils.isBlank(templateName)) {
                templateName = Constant.template;
            }
            htmlTemplate = new BufferedReader(new FileReader(path + "/" + templateName));
            String temp;
            while ((temp = htmlTemplate.readLine()) != null) {
                template.append(temp);
            }
        } finally {
            if (htmlTemplate != null) {
                htmlTemplate.close();
            }
        }

        String summary = summary();
        String detail = detail();
        String resultHtml = template.toString();
        for (SeverityEnum severityEnum : SeverityEnum.values()) {
            resultHtml = resultHtml.replace("${" + severityEnum.getCode().toLowerCase() + "}", "" + severityEnum.getPoint());
        }
        resultHtml = resultHtml.replace(Constant.summary, summary);
        resultHtml = resultHtml.replace(Constant.detail, detail);
        File newFile = new File(path + "/" + fileName());
        FileWriter fw = null;
        try {
            fw = new FileWriter(newFile);
            fw.write(resultHtml);
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }

    private String summary() {
        String temp = Template.summary;
        String begin = DateFormatUtils.format(reportResult.getBeginDate(), "yyyy-MM-dd");
        String end = DateFormatUtils.format(reportResult.getEndDate(), "yyyy-MM-dd");
        String totalCompliance = Constant.twoDForm.format(reportResult.getScanResult().getTotal().getCompliance());
        int totalFile = reportResult.getTotalClz();
        int totalLine = reportResult.getTotalLine();
        int coveredLine = reportResult.getTotalCoveredLine();

        int totalChangeFile = reportResult.getTotalChangeClz();
        int totalChangeLine = reportResult.getTotalChangeLine();
        StringBuilder severityInfo = buildSeverity(reportResult.getScanResult().getTotal(), true);
        temp = temp.replace("${projectName}", reportResult.getProjectMap().keySet().toString());
        temp = temp.replace("${startTime}", begin);
        temp = temp.replace("${endTime}", end);
        temp = temp.replace("${totalCoverage}", buildCoverageRate(reportResult.getScanResult().getTotal().getCoverage(), "left"));

        String optionName = "Compliance";
        String optionValue = "" + totalCompliance + "%";
        if (sonarProperties.isEnableUTSucc()) {
            optionName = "Unit Test Succ%";
            double rate = reportResult.calcTotalRate();
            String bgColor = "#fff";
            String color = "#000";
            if (rate < 100) {
                bgColor = "#d4333f";
                color = "#fff";
            }
            optionValue = "<div style='width: 100%; height: 100%; text-align: center; color: " + color + ";background: " + bgColor + ";'>" + Constant.twoDForm.format(reportResult.calcTotalRate()) + "%</div>";
        }
        temp = temp.replace("${optionName}", optionName);
        temp = temp.replace("${totalOption}", optionValue);

        temp = temp.replace("${totalFile}", "" + totalFile);
        temp = temp.replace("${totalLine}", "" + totalLine);
        temp = temp.replace("${totalChangeFile}", "" + totalChangeFile);
        temp = temp.replace("${totalChangeLine}", "" + coveredLine + "/" + totalChangeLine);
        temp = temp + severityInfo.toString().replace("20px", "5px").replace("20px", "5px");
        return temp;
    }

    private String detail() {
        TreeMap<String, AuthorResult> teamMap = reportResult.getScanResult().getTotalTeamMap();
        TreeMap<String, AuthorResult> totalAuthorMap = reportResult.getScanResult().getTotalAuthorMap();
        TreeMap<String, List<AuthorResult>> authorMapList = reportResult.getScanResult().getAuthorMapList();

        if (teamMap == null || teamMap.size() == 0) {
            return Template.noTeam;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, AuthorResult> entry : teamMap.entrySet()) {
            String teamName = entry.getKey();
            if (StringUtils.equalsIgnoreCase(teamName, "BLANK")) {
                continue;
            }
            String temp = team(teamName, entry.getValue(), totalAuthorMap, authorMapList);
            stringBuilder.append("<div id=\"detail-ct\">");
            stringBuilder.append(temp);
            stringBuilder.append("</div>");
        }
        return stringBuilder.toString();
    }


    private String team(String teamName, AuthorResult teamResult, TreeMap<String, AuthorResult> totalAuthorMap, TreeMap<String, List<AuthorResult>> auorMapList) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> teamMember = teamProperties.getTeamMap().get(teamName);
        String teamInfo = teamName + "<br/><span style=\"font-size: 10px; color: #555;\">" + teamProperties.convert2Employ(teamMember) + "</span>";
        int teamLine = teamResult.getTotalLine();
        int teamCoveredLine = teamResult.getTotalCoveredLine();
        String teamCompliance = Constant.twoDForm.format(teamResult.getCompliance());
        String temp = Template.team;

        String optionName = "Compliance";
        String optionValue = "" + teamCompliance + "%";
        if (sonarProperties.isEnableUTSucc()) {
            optionName = "";
            optionValue = "";
        }

        StringBuilder severityInfo = buildSeverity(teamResult);
        temp = temp.replace("${teamId}", teamInfo + severityInfo.toString());
        temp = temp.replace("${teamTotalLineNumber}", "" + teamCoveredLine + "/" + teamLine);
        temp = temp.replace("${teamCoverageRate}", buildCoverageRate(teamResult.getCoverage(), "center"));
        temp = temp.replace("${teamOptionValue}", optionValue);
        temp = temp.replace("${optionName}", optionName);

        stringBuilder.append(temp);

        stringBuilder.append(" <div class=\"team-detail\">");
        String member = teamMember(teamMember, totalAuthorMap, auorMapList);
        stringBuilder.append(member);

        stringBuilder.append("</div>");
        return stringBuilder.toString();
    }

    private String teamMember(List<String> teamMember, TreeMap<String, AuthorResult> totalAuthorMap, TreeMap<String, List<AuthorResult>> auorMapList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isEmpty(teamMember)) {
            return Template.noTeamMember;
        }
        for (String member : teamMember) {
            AuthorResult authorResult = totalAuthorMap.get(member);
            List<AuthorResult> authorClzResult = auorMapList.get(member);
            int fileCount = 0;
            int lineCount = 0;
            int coveredLine = 0;
            double coverage = 0;
            double compliance = 0;
            String levelColor = "#222";
            if (authorResult != null) {
                coverage = authorResult.getCoverage();
                compliance = authorResult.getCompliance();
                lineCount = authorResult.getTotalLine();
                coveredLine = authorResult.getTotalCoveredLine();
                if (!sonarProperties.isEnableUTSucc()) {
                    if (authorResult.getBlock() > 0) {
                        levelColor = "#d4333f";
                    } else if (authorResult.getCritical() > 0) {
                        levelColor = "red";
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(authorClzResult)) {
                fileCount = authorClzResult.size();
            }
            StringBuilder severityInfo = buildSeverity(authorResult);
            String temp = Template.authorSummary;
            String optionName = "Compliance";
            String optionValue = Constant.twoDForm.format(compliance) + "%";
            if (sonarProperties.isEnableUTSucc()) {
                optionName = "";
                optionValue = "";
            }

            temp = temp.replace("${levelColor}", levelColor);
            temp = temp.replace("${empId}", teamProperties.convert2EmployInfo(member) + severityInfo.toString());
            temp = temp.replace("${fileCount}", "" + fileCount);
            temp = temp.replace("${empTotalLineNumber}", "" + coveredLine + "/" + lineCount);
            temp = temp.replace("${empCoverageRate}", buildCoverageRate(coverage, "center"));
            temp = temp.replace("${empOptionValue}", optionValue);
            temp = temp.replace("${optionName}", optionName);

            stringBuilder.append(temp);
            stringBuilder.append("<div class=\"author-detail\">");
            String memberHtml = member(authorClzResult);
            stringBuilder.append(memberHtml);
            stringBuilder.append("</div>");
        }

        return stringBuilder.toString();
    }

    private String member(List<AuthorResult> authorClzResult) {
        if (CollectionUtils.isEmpty(authorClzResult)) {
            return Template.noClass;
        }
        String optionName = "Compliance";
        if (sonarProperties.isEnableUTSucc()) {
            optionName = "Unit Test Succ%";
        }
        String header = StringUtils.replace(Template.classHeader, "${optionName}", optionName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(header);
        String linkBase = sonarProperties.getWebHost() + "/dashboard/index/";
        int i = 0;
        for (AuthorResult authorResult : authorClzResult) {
            i++;
            String classNameTag = linkBase + authorResult.getComponentId();
            String className = authorResult.getClazz();
            int lineCount = authorResult.getTotalLine();
            int coveredLine = authorResult.getTotalCoveredLine();
            double coverage = authorResult.getCoverage();
            double compliance = authorResult.getCompliance();
            StringBuilder severityInfo = buildSeverity(authorResult);
            String temp = Template.classRow;
            String clzRowStyle = i % 2 == 0 ? "background: #fff;" : "background: #eee;";
            String optionValue = Constant.twoDForm.format(compliance);

            if (!sonarProperties.isEnableUTSucc()) {
                if (authorResult.getBlock() > 0) {
                    temp = temp.replace("${levelColor}", "#d4333f");
                    temp = temp.replace("${level}", "color: #d4333f");
                } else if (authorResult.getCritical() > 0) {
                    temp = temp.replace("${levelColor}", "red");
                    temp = temp.replace("${level}", "color: red");
                } else {
                    temp = temp.replace("${levelColor}", "#222");
                    temp = temp.replace("${level}", "");
                }
            } else {
                UTDO utdo = utSuccRate(authorResult, reportResult);
                optionValue = toString(utdo);
                if (utdo == null) {
                    temp = temp.replace("${levelColor}", "#F39C12");
                    temp = temp.replace("${level}", "color: #F39C12");
                } else if (utdo.getSuccRate() < 100) {
                    temp = temp.replace("${levelColor}", "red");
                    temp = temp.replace("${level}", "color: red");
                } else {
                    temp = temp.replace("${levelColor}", "#222");
                    temp = temp.replace("${level}", "");
                }
            }

            temp = temp.replace("${link}", classNameTag);
            temp = temp.replace("${className}", className);
            temp = temp.replace("${severityInfo}", severityInfo.toString());
            temp = temp.replace("${classLineNumber}", "" + coveredLine + "/" + lineCount);
            temp = temp.replace("${classCoverageRate}", buildCoverageRate(coverage, "right"));
            temp = temp.replace("${classOptionValue}", optionValue);
            temp = temp.replace("${clzRowStyle}", clzRowStyle);
            stringBuilder.append(temp);
        }
        stringBuilder.append(Template.tableEnd);
        return stringBuilder.toString();
    }

    private String toString(UTDO utdo) {
        if (utdo == null) {
            return "N/A";
        }
        String rate = Constant.twoDForm.format(utdo.getSuccRate());
        return rate;
//        String color = "#222";
//        if (utdo.getFailure() > 0) {
//            color = "red";
//        }
//        return "<span style='color: " + color + ";'>" + utdo.getFailure() + "</span>/<span style='color: #222; padding-right: 10px'>" + utdo.getTotal() + "</span>" + rate;
    }


    private UTDO utSuccRate(AuthorResult authorResult, ReportResult reportResult) {
        return reportResult.fetchUT(authorResult.getProjectId(), authorResult.getComponentKey());
//        if (utdo == null) {
//            return "N/A";
//        }
//        return "" + utdo.getSuccRate();
    }

    private String buildCoverageRate(double coverage, String align) {
        String coverageStr = Constant.twoDForm.format(coverage);
        String restStr = Constant.twoDForm.format(100 - coverage);
        String inner = Template.coverageInner;
        inner = inner.replace("${coveragePer}", coverageStr + "%");
        inner = inner.replace("${coverageColor}", " #58d68d");
        inner = inner.replace("${restPer}", restStr + "%");
        inner = inner.replace("${restColor}", "#ec7063");

        String text = Template.coverageText.replace("${coverage}", coverageStr + "%").replace("${align}", align);
        String percentage = Template.coveragePercentage.replace("${inner}", inner);

        StringBuilder content = new StringBuilder();
        content.append(percentage);
        content.append(text);
        String temp = Template.coverage.replace("${content}", content.toString());
        return temp;
    }

    private StringBuilder buildSeverity(AuthorResult authorResult) {
        StringBuilder tempBuilder = new StringBuilder();
        tempBuilder.append(buildUTAndBranch(authorResult));
        if (authorResult == null) {
            return tempBuilder;
        }
        tempBuilder.append(Template.severityCt);
        if (authorResult.getBlock() > 0) {
            tempBuilder.append(Template.blocker.replace("${count}", "" + authorResult.getBlock()));
            tempBuilder.append("&nbsp;&nbsp;");
        }
        if (authorResult.getCritical() > 0) {
            tempBuilder.append(Template.critical.replace("${count}", "" + authorResult.getCritical()));
            tempBuilder.append("&nbsp;&nbsp;");
        }
        if (authorResult.getMajor() > 0) {
            tempBuilder.append(Template.major.replace("${count}", "" + authorResult.getMajor()));
            tempBuilder.append("&nbsp;&nbsp;");
        }
        if (authorResult.getMinor() > 0) {
            tempBuilder.append(Template.minor.replace("${count}", "" + authorResult.getMinor()));
            tempBuilder.append("&nbsp;&nbsp;");
        }
        if (authorResult.getInfo() > 0) {
            tempBuilder.append(Template.info.replace("${count}", "" + authorResult.getInfo()));
            tempBuilder.append("&nbsp;&nbsp;");
        }
        tempBuilder.append(Template.severityCtEnd);
        return tempBuilder;
    }

    private StringBuilder buildUTAndBranch(AuthorResult authorResult) {
        StringBuilder tempBuilder = new StringBuilder();
        if (authorResult == null) {
            return tempBuilder;
        }
        String ut = "" + authorResult.getTotalUtLineHits() + "/" + authorResult.getTotalUtLine();
        String branch = "" + authorResult.getTotalUtCoveredConditions() + "/" + authorResult.getTotalUtConditions();
        String staticCount = "" + authorResult.getTotalStaticLine();
        tempBuilder.append(Template.utAndBranchCt);
        tempBuilder.append(Template.ut.replace("${count}", ut + "&nbsp;&nbsp;"));
        tempBuilder.append(Template.branch.replace("${count}", branch + "&nbsp;&nbsp;"));
        tempBuilder.append(Template.staticArea.replace("${count}", staticCount + "&nbsp;&nbsp;"));
        tempBuilder.append(Template.utAndBranchCtEnd);
        return tempBuilder;
    }

    private StringBuilder buildSeverity(AuthorResult authorResult, boolean withFullname) {
        StringBuilder tempBuilder = this.buildSeverity(authorResult);
        if (!withFullname) {
            return tempBuilder;
        }
        String temp = tempBuilder.toString();
//        temp = temp.replace("A:", SeverityEnum.BLOCKER.getCode() + ":");
//        temp = temp.replace("B:", SeverityEnum.CRITICAL.getCode() + ":");
//        temp = temp.replace("C:", SeverityEnum.MAJOR.getCode() + ":");
//        temp = temp.replace("D:", SeverityEnum.MINOR.getCode() + ":");
//        temp = temp.replace("E:", SeverityEnum.INFO.getCode() + ":");
        return new StringBuilder().append(temp);
    }


    private String fileName() {
        if (reportResult.getBeginDate() == null || reportResult.getEndDate() == null) {
            return "test.htm";
        }
        String begin = DateFormatUtils.format(reportResult.getBeginDate(), "yyyy-MM-dd");
        String end = DateFormatUtils.format(reportResult.getEndDate(), "yyyy-MM-dd");
        return "sonar-report(" + begin + "~" + end + ").html";
    }


    interface Template {
        String summary = "<table style=\"width: 100%; font-family: Calibri; font-size: 14px;\">\n" +
                "            <tr>\n" +
                "                <td style=\"background: #D6EBFC\">Project Name</td>\n" +
                "                <td>${projectName}</td>\n" +
                "                <td style=\"background: #D6EBFC\">Total Coverage</td>\n" +
                "                <td>${totalCoverage}</td>\n" +
                "                <td style=\"background: #D6EBFC\">Total ${optionName}</td>\n" +
                "                <td>${totalOption}</td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td style=\"background: #D6EBFC\"> Start Time</td>\n" +
                "                <td>${startTime}</td>\n" +
                "                <td style=\"background: #D6EBFC\">Total File</td>\n" +
                "                <td>${totalFile}</td>\n" +
                "                <td style=\"background: #D6EBFC\">Total Line of Code</td>\n" +
                "                <td>${totalLine}</td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td style=\"background: #D6EBFC\">End Time</td>\n" +
                "                <td>${endTime}</td>\n" +
                "                <td style=\"background: #D6EBFC\">Total Change File</td>\n" +
                "                <td>${totalChangeFile}</td>\n" +
                "                <td style=\"background: #D6EBFC\">Total Covered/Change Line Of Code</td>\n" +
                "                <td>${totalChangeLine}</td>\n" +
                "            </tr>\n" +
                "        </table>";


        String team = "<table class=\"team-summary\" style=\"border-style: ridge; border-width: 1px; width:100%; border-spacing: 0px;\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                <tr>\n" +
                "                    <th width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\">\n" +
                "                        Team</th>\n" +
                "                    <th width=\"25%\" style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 10px; margin: 0px; background: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\">\n" +
                "                        Covered/Line Count</th>\n" +
                "                    <th width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; padding-right: 10px; margin: 0px; background: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\">\n" +
                "                        Coverage</th>\n" +
                "                    <th width=\"25%\" style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 10px; margin: 0px; background: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\">\n" +
                "                        ${optionName}</th>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background: #FFFFFF;\"> ${teamId}</td>\n" +
                "                    <td width=\"25%\" style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 20px; margin: 0px; background: #FFFFFF;\"> ${teamTotalLineNumber}</td>\n" +
                "                    <td width=\"25%\" style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 0px; margin: 0px; background: #FFFFFF;\"> ${teamCoverageRate}</td>\n" +
                "                    <td width=\"25%\" style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 20px; margin: 0px; background: #FFFFFF; color: #008000;\"> ${teamOptionValue}</td>\n" +
                "                </tr>\n" +
                "            </table>";


        String authorSummary = "<table class=\"author-summary\" align=\"center\" style=\"border-style: ridge; border-width: 1px; width:98%;\" cellpadding=\"0\" cellspacing=\"0\" >\n" +
                "                    <tr>\n" +
                "                        <th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background: #007CA6; color: #FFFFFF; font-weight: bold; font-size: 14px;\">Author</th>\n" +
                "                        <th style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px;  padding-right: 10px; margin: 0px; background: #007CA6; color: #FFFFFF; font-weight: bold; font-size: 14px;\">File Count</th>\n" +
                "                        <th style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 10px; margin: 0px; background: #007CA6; color: #FFFFFF; font-weight: bold; font-size: 14px;\">Covered/Line Count</th>\n" +
                "                        <th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px;padding-right: 10px;  margin: 0px; background: #007CA6; color: #FFFFFF; font-weight: bold; font-size: 14px;\">Coverage</th>\n" +
                "                        <th style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 10px; margin: 0px; background: #007CA6; color: #FFFFFF; font-weight: bold; font-size: 14px;\">${optionName}</th>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"vertical-align: middle; text-align: left; font-family: Calibri; padding-left: 20px; margin: 0px; background: #FFFFFF;\">${empId}</td>\n" +
                "                        <td style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 20px; margin: 0px; background: #FFFFFF;\"> ${fileCount}</td>\n" +
                "                        <td style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 20px; margin: 0px; background: #FFFFFF;\"> ${empTotalLineNumber}</td>\n" +
                "                        <td style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 0px; margin: 0px; background: #FFFFFF;\"> ${empCoverageRate}</td>\n" +
                "                        <td style=\"vertical-align: middle; text-align: right; font-family: Calibri; padding: 0px; padding-right: 20px; margin: 0px; background: #FFFFFF; color: ${levelColor};\"> ${empOptionValue}</td>\n" +
                "                    </tr>\n" +
                "                </table>";

        String classHeader = "<table align=\"center\" style=\"border-style: ridge; border-width: 1px; width:97%;\" cellpadding=\"0\" cellspacing=\"0\" >\n" +
                "                        <tr>\n" +
                "                            <th width=\"60%\" style=\"border-style: solid; border-width: 1px; font-size: 12px; background: #D6EBFC\">Class Name</th>\n" +
                "                            <th width=\"10%\" style=\"border-style: solid; border-width: 1px; font-size: 12px; background: #D6EBFC\">Covered/Line Count</th>\n" +
                "                            <th width=\"20%\" style=\"border-style: solid; border-width: 1px; font-size: 12px; background: #D6EBFC\">Coverage</th>\n" +
                "                            <th width=\"10%\" style=\"border-style: solid; border-width: 1px; font-size: 12px; background: #D6EBFC\">${optionName}</th>\n" +
                "                        </tr>";

        String classRow = "<tr style='${clzRowStyle}'>\n" +
                "                            <td style=\"border-style: solid; border-width: 1px; font-size: 12px;  padding-left: 20px;\"><a target=\"_blank\" href=\"${link}\" style=\"${level}\">${className}</a>${severityInfo}</td>\n" +
                "                            <td style=\"border-style: solid; text-align: right; padding-right: 20px; border-width: 1px; font-size: 12px; color: #222;\">${classLineNumber}</td>\n" +
                "                            <td style=\"border-style: solid; text-align: right; padding-right: 0px; border-width: 1px; font-size: 12px; color: #222; \">${classCoverageRate}</td>\n" +
                "                            <td style=\"border-style: solid; text-align: right; padding-right: 20px; border-width: 1px; font-size: 12px; color: ${levelColor}; \" >${classOptionValue}%</td>\n" +
                "                        </tr>";

        String tableEnd = "</table>";

        String noTeam = "<div style=\"color: #aaa; padding-left: 20px;\">No team found</div>";
        String noTeamMember = "<div style=\"color: #aaa; padding-left: 20px;\">No team member found</div>";
        String noClass = "<div style=\"color: #aaa; padding-left: 20px;\">No class found</div>";


        String severityCt = " <code style=\"margin-left: 10px;\">";
        String utAndBranchCt = " <code style=\"margin-left: 5px; background: #bbb; padding-left: 10px;\">";
        String severityCtEnd = " </code>";
        String utAndBranchCtEnd = " </code>";
        String blocker = "<span style=\"color: #d4333f; margin-right: 0px;\">Blocker:${count}</span>";
        String critical = "<span style=\"color: red; margin-right: 0px;\">Critical:${count}</span>";
        String major = "<span style=\"color: green; margin-right: 0px;\">Major:${count}</span>";
        String minor = "<span style=\"color: #555; margin-right: 0px;\">Minor:${count}</span>";
        String info = "<span style=\"color: #aaa; margin-right: 0px;\">Info:${count}</span>";
        String ut = "<span style=\"color: #fff; margin-right: 0px;\">UT:${count}</span>";
        String branch = "<span style=\"color: #fff; margin-right: 0px;\">Branch:${count}</span>";
        String staticArea = "<span style=\"color: #fff; margin-right: 0px;\">Static:${count}</span>";

//        String coverageInner = "<div style=\"width: 100%; height: 12px; border-radius: 4px; background: #ddd; \">" +
//                "<div style=\"width: ${coveragePer}; height: 12px; border-radius: 4px; background: ${coverageColor};\"></div>" +
//                "</div>";

        String coverageInner = "<table style=\"border-width: 0px; height: 12px; width: 100%\">" +
                "<tr>" +
                "<td style=\"height: 12px; width: ${restPer}; background: ${restColor}; padding: 0px;\"></td>" +
                "<td style=\"height: 12px; width: ${coveragePer}; background: ${coverageColor}; padding: 0px;\"></td>" +
                "</tr>" +
                "</table>";

        String coverage = "<table style=\"border-width: 0px; width: 100%; height: 12px;\">" +
                "<tr>${content}</tr>" +
                "</table>";

        String coverageText = "<td style=\"width: 50px; text-align: ${align};\">${coverage}</td>";
        String coveragePercentage = "<td style=\"width: calc(100% - 50px); min-width: 100px; height: 12px; border-radius: 4px; padding: 0px;\">${inner}</td>";

    }


    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("xx");
        System.out.println(set);
    }

}
