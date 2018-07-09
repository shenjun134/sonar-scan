package com.sonar.service;

import com.sonar.constant.SeverityEnum;
import com.sonar.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.io.*;
import java.util.*;

public class SeverityHtmlService {
    private SonarProperties sonarProperties;
    private TeamProperties teamProperties;
    private SeverityResult severityResult;

    public SeverityHtmlService(SonarProperties sonarProperties, TeamProperties teamProperties, SeverityResult severityResult) {
        this.sonarProperties = sonarProperties;
        this.teamProperties = teamProperties;
        this.severityResult = severityResult;
    }

    interface Constant {
        String summary = "#SUMMARY_PART#";
        String detail = "#DETAIL_PART#";
        String template = "severity-template.htm";
    }

    public void generateReport() throws IOException {
        String path = System.getProperty("user.dir");
        BufferedReader htmlTemplate = null;
        StringBuilder template = new StringBuilder();
        try {
            String templateName = sonarProperties.getSeverityTemplate();
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
        Integer totalBlocker = severityResult.getSeverityStatistic().get(SeverityEnum.BLOCKER);
        totalBlocker = totalBlocker == null ? 0 : totalBlocker;

        Integer totalCritical = severityResult.getSeverityStatistic().get(SeverityEnum.CRITICAL);
        totalCritical = totalCritical == null ? 0 : totalCritical;

        temp = temp.replace("${projectName}", severityResult.getProjects().toString());
        temp = temp.replace("${totalBlocker}", "" + totalBlocker);
        temp = temp.replace("${totalCritical}", "" + totalCritical);
        return temp;
    }

    private String detail() {
        Map<String, AuthorSeverity> authorSeverityMap = severityResult.getAuthorSeverityMap();
        if (authorSeverityMap == null) {
            return Template.NO_ISSUE;
        }
        List<AuthorSeverity> authorSeverityList = new ArrayList<>(authorSeverityMap.values());
        sort(authorSeverityList);
        StringBuilder stringBuilder = new StringBuilder();
        for (AuthorSeverity authorSeverity : authorSeverityList) {
            stringBuilder.append(createAuthorHtml(authorSeverity));
        }
        return stringBuilder.toString();
    }

    private static void sort(List<AuthorSeverity> authorSeverityList) {
        Collections.sort(authorSeverityList, new Comparator<AuthorSeverity>() {
            @Override
            public int compare(AuthorSeverity o1, AuthorSeverity o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (01 == 02) {
                    return 0;
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return -(o1.getCompliance() - o2.getCompliance());
            }
        });
    }


    private String createAuthorHtml(AuthorSeverity authorSeverity) {
        StringBuilder stringBuilder = new StringBuilder();
        Integer totalBlocker = authorSeverity.getSeverityStatistic().get(SeverityEnum.BLOCKER);
        Integer totalCritical = authorSeverity.getSeverityStatistic().get(SeverityEnum.CRITICAL);
        String blockerOpacity = "block";
        String criticalOpacity = "block";
        if (totalBlocker == null || totalBlocker == 0) {
            totalBlocker = 0;
            blockerOpacity = "none";
        }
        if (totalCritical == null || totalCritical == 0) {
            totalCritical = 0;
            criticalOpacity = "none";
        }

        String temp = Template.authorSummary;
        String author = teamProperties.convert2EmployInfo(authorSeverity.getAuthor());
        temp = temp.replace("${author}", author);
        temp = temp.replace("${totalBlocker}", "" + totalBlocker);
        temp = temp.replace("${blockerDisplay}", "" + blockerOpacity);
        temp = temp.replace("${totalCritical}", "" + totalCritical);
        temp = temp.replace("${criticalDisplay}", "" + criticalOpacity);
        stringBuilder.append(temp);


        for (SeverityEnum severityEnum : authorSeverity.getSeverityStatistic().keySet()) {
            List<SeverityInfo> infoList = authorSeverity.getSeverityMap().get(severityEnum);
            stringBuilder.append(builderSeverityList(infoList));
        }
        return stringBuilder.toString();
    }

    private String builderSeverityList(List<SeverityInfo> infoList) {
        StringBuilder stringBuilder = new StringBuilder();
        String linkBase = sonarProperties.getWebHost() + "/dashboard/index/";
        String ruleList = sonarProperties.getWebHost() + "/coding_rules#qprofile=java-sonar_ooa_devops-08479|activation=true|types=";
        int i = 0;
        for (SeverityInfo info : infoList) {
            String temp = Template.classRow;
            String bgColor = i % 2 == 0 ? Template.evenBgColor : Template.oddBgColor;
            String className = info.getClazz().getName();
            String classId = info.getClazz().getId();
            String sourceLink = linkBase + classId;
            int line = info.getIssueResult().getLine();
            String severityLevel = info.getSeverity().getCode();
            Date creationDate = info.getIssueResult().getCreationDate();
            String creationDtStr = DateFormatUtils.format(creationDate, "yyyy-MM-dd HH:mm:ss");
            String type = info.getIssueResult().getType();
            String rule = info.getIssueResult().getRule();
            String message = info.getIssueResult().getMessage();
            String effort = info.getIssueResult().getEffort();
            String debt = info.getIssueResult().getDebt();
            String severityColor = info.getSeverity().getCode().equalsIgnoreCase(SeverityEnum.BLOCKER.getCode()) ? "darkred" : "red";
            temp = temp.replace("${className}", "" + className);
            temp = temp.replace("${sourceLink}", "" + sourceLink);
            temp = temp.replace("${bgColor}", "" + bgColor);
            temp = temp.replace("${line}", "" + line);
            temp = temp.replace("${severityLevel}", "" + severityLevel);
            temp = temp.replace("${severityColor}", "" + severityColor);
            temp = temp.replace("${creationDate}", "" + creationDtStr);
            temp = temp.replace("${type}", "" + type);
            temp = temp.replace("${ruleLink}", "" + ruleList + type);
            temp = temp.replace("${rule}", "" + rule);
            temp = temp.replace("${message}", "" + message);
            temp = temp.replace("${effort}", "" + effort);
            temp = temp.replace("${debt}", "");
            stringBuilder.append(temp);
            i++;
        }
        return stringBuilder.toString();
    }


    private String fileName() {
        return "sonar-severity-report.html";
    }


    interface Template {
        String evenBgColor = "#e6ffff";
        String oddBgColor = "#f5f5f0";

        String summary = "<table style=\"width: 100%; font-family: Calibri; font-size: 14px;\">\n" +
                "                <tr>\n" +
                "                    <td style=\"background: #D6EBFC\">Project Name</td>\n" +
                "                    <td>${projectName}</td>\n" +
                "                    <td style=\"background: #D6EBFC\">Total Blocker</td>\n" +
                "                    <td>${totalBlocker}</td>\n" +
                "                    <td style=\"background: #D6EBFC\">Total Critical</td>\n" +
                "                    <td>${totalCritical}</td>\n" +
                "                </tr>\n" +
                "            </table>";


        String authorSummary = "<table class=\"author-summary\" style=\"border-style: ridge; border-width: 1px; width:100%; border-spacing: 0px;\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                <tr>\n" +
                "                    <th width=\"50%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background: #005CA6; color: #FFFFFF; font-weight: bold; font-size: 14px;\">\n" +
                "                        Author</th>\n" +
                "                    <th width=\"50%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; padding-right: 10px; margin: 0px; background: #005CA6; color: #FFFFFF; font-weight: bold; font-size: 14px;\">\n" +
                "                        Issue Detail</th>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td width=\"50%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background: #FFFFFF;\">\n" +
                "                        ${author}\n" +
                "                    </td>\n" +
                "                    <td width=\"50%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; padding-right: 20px; margin: 0px; background: #FFFFFF;\">\n" +
                "                        <span style=\"color: darkred; padding-right: 10px; display: ${blockerDisplay};\">Blocker: ${totalBlocker}</span>\n" +
                "                        <span style=\"color: red; padding-right: 10px; display: ${criticalDisplay};\">Critical: ${totalCritical}</span>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>";

        String classRow = "<table class=\"author-detail\" align=\"center\" style=\"border-style: ridge; border-width: 1px; width:98%;\" cellpadding=\"0\" cellspacing=\"0\" >\n" +
                "                    <tr>\n" +
                "                        <td width=\"50%\" style=\"vertical-align: middle; text-align: left; font-family: Calibri; background: ${bgColor}; padding-left: 10px; margin: 0px; font-size: 14px;\">\n" +
                "                            <a href=\"${sourceLink}\" target=\"_blank\">${className}</a>\n" +
                "                            <span style=\"padding-right: 10px; padding-left: 10px; color: #777;\">Line:${line}</span>\n" +
                "                            <span style=\"padding-right: 10px; color: ${severityColor};\">${severityLevel}</span>\n" +
                "                            <span style=\"padding-right: 10px; color: #777;\">${creationDate}</span>\n" +
                "                            <a href=\"${ruleLink}\" target=\"_blank\"><span style=\"padding-right: 10px; color: #777;\">${type}</span></a>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td width=\"50%\" style=\"vertical-align: middle; text-align: left; font-family: Calibri; background: ${bgColor}; padding-left: 10px; margin: 0px; font-size: 14px;\">\n" +
                "                            <span style=\"color: #777; padding-right: 5px;\">Tip:</span>" +
                "                            ${message}" +
                "                            <span style=\"color: #777; padding-left: 20px;\">${effort}</span>\n" +
                "                            <span style=\"color: #777; padding-left: 20px;\">${debt}</span>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>";

        String blocker = "<span style=\"color: darkred; margin-right: 5px;\">Blocker:${count}</span>";
        String critical = "<span style=\"color: red; margin-right: 5px;\">Critical:${count}</span>";
        String major = "<span style=\"color: green; margin-right: 5px;\">Major:${count}</span>";
        String minor = "<span style=\"color: #777; margin-right: 5px;\">Minor:${count}</span>";
        String info = "<span style=\"color: #ccc; margin-right: 5px;\">Info:${count}</span>";

        String NO_ISSUE = "<div style=\"text-align: center; font-size: 24px; color: green;\">No Blocker and Critical Issue.</div>";

    }


    public static void main(String[] args) {
        List<AuthorSeverity> authorSeverityList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int r = Math.round(i * 100 + 77) % Math.round(77);
            if (r < 20) {
                authorSeverityList.add(null);
            }
            authorSeverityList.add(new AuthorSeverity("sa" + i).setCompliance(r));
        }
        sort(authorSeverityList);
        for (AuthorSeverity authorSeverity : authorSeverityList) {
            if (authorSeverity == null) {
                System.out.println("null");
                continue;
            }
            System.out.println(authorSeverity.getAuthor() + " : " + authorSeverity.getCompliance());
        }
    }

}
