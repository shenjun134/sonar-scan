package com.xiangsoft.sonar.sonarreport.processor;

import com.xiangsoft.sonar.sonarreport.Client;
import com.xiangsoft.sonar.sonarreport.model.ClassLevelStatisticsInfo;
import com.xiangsoft.sonar.sonarreport.model.EmployeeLevelStatisticsInfo;
import com.xiangsoft.sonar.sonarreport.model.Report;
import com.xiangsoft.sonar.sonarreport.model.TeamLevelStatisticsInfo;
import com.xiangsoft.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

public class HtmlWriteProcessor {

    private String projectName;
    private Date startDt;
    private Date endDt;
    private String startDtStr;
    private String endDtStr;

    private int totalFiles;

    public HtmlWriteProcessor() {

    }

    public HtmlWriteProcessor(String projectName, Date startDt, Date endDt, String startDtStr, String endDtStr, int totalFiles) {
        this.projectName = projectName;
        this.startDt = startDt;
        this.endDt = endDt;
        this.startDtStr = startDtStr;
        this.endDtStr = endDtStr;
        this.totalFiles = totalFiles;
    }

    String summerPart = "<table style=\"width: 100%; font-family: Calibri; font-size: medium;\">\n" +
            "<tr>\n" +
            "<td class=\"style1\" style=\"background-color: #D6EBFC\">\n" +
            "Project Name</td>\n" +
            "<td class=\"style2\">\n" +
            "${project_name}</td>\n" +
            "<td class=\"style3\" style=\"background-color: #D6EBFC\">" +
            "Team Coverage Rate</td>\n" +
            "<td class=\"style2\" id=\"TotalScriptsCount\">" +
            "${total_coverage_rate}%</td>\n" +
            "<td class=\"style3\" style=\"background-color: #D6EBFC\">" +
            "Team Compliance Rate</td>\n" +
            "<td class=\"style2\" id=\"TotalManualCaseCount\">" +
            "${total_compliance_rate}%</td>\n" +
            "</tr>\n" +
            "<tr>\n" +
            "<td class=\"style1\" style=\"background-color: #D6EBFC\">" +
            "Start Time</td>\n" +
            "<td class=\"style2\">" +
            "${start_time}</td>\n" +
            "<td class=\"style1\" style=\"background-color: #D6EBFC\">" +
            "Total File ${skipTest}</td>\n" +
            "<td class=\"style2\">" +
            "${totalFile}</td>\n" +
            "</tr>\n" +
            "<tr>\n" +
            "<td class=\"style1\" style=\"background-color: #D6EBFC\">" +
            "   End Time</td>\n" +
            "<td class=\"style2\">" +
            "${end_time}</td>\n" +
            "<td class=\"style3\">" +
            "&nbsp;</td>\n" +
            "<td>" +
            "&nbsp;</td>\n" +
            "</tr>" +
            "</table>";

    String teamBodyPart = "<table  style=\"border-style: ridge; border-width: 1px; width:100%; border-spacing: 0px;\" cellpadding=\"0\" cellspacing=\"0\">\n" +
            "<tr>" +
            "<th width=\"5%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #005CA6; color: #FFFFFF; font-weight: bold; font-size: x-large;class=\"style6\">" +
            " &nbsp;</th> \n" +
            "<th width=\"20%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\" class=\"style5\"> " +
            " Team ID</th> \n" +
            "<th width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\" class=\"style7\"> " +
            " Team Line Count</th> \n" +
            "<th width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\" class=\"style4\"> " +
            " Coverage Rate</th> \n" +
            "<th width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #005CA6; color: #FFFFFF; font-weight: bold; font-size: large;\"> " +
            " Compliance Rate</th> \n" +
            "</tr>\n ";
    String teamDetail = " <tr> " +
            "	<td width=\"5%\" id=\"main${teamSeq}\" style=\"vertical-align: middle; text-align: left; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF;\">\n" +
            "<A onclick=\"toggle(${teamSeq}, 'open')\" class=\"style7\"  href=\"javascript:void(0)\">+</A> &nbsp;</td>" +
            "<td width=\"20%\" style=\"vertical-align: middle; text-align: left; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF;\"> ${teamId}</td>" +
            "	<td width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF;\"> ${teamTotalLineNumber}</td>" +
            "	<td width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF; color: #008000;\"> ${teamCoverageRate}%</td> " +
            "<td width=\"25%\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF; color: #008000;\"> ${teamComplianceRate}%</td> " +
            "</tr>\n";
    String empBodyPart = "<table align=\"center\" style=\"border-style: ridge; border-width: 1px; width:98%;\" cellpadding=\"0\" cellspacing=\"0\" >" +
            "<tr>" +
            "<th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #007CA6; color: #FFFFFF; font-weight: bold; font-size:medium ;class=\"style6\">" +
            " &nbsp;</th> " +
            "<th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #007CA6; color: #FFFFFF; font-weight: bold; font-size: medium;\" class=\"style5\"> " +
            " Emp ID</th> " +
            "<th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #007CA6; color: #FFFFFF; font-weight: bold; font-size: medium;\" class=\"style5\"> " +
            " File Count</th> " +
            "<th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #007CA6; color: #FFFFFF; font-weight: bold; font-size: medium;\" class=\"style7\"> " +
            " Emp Line Count</th> " +
            "<th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #007CA6; color: #FFFFFF; font-weight: bold; font-size: medium;\" class=\"style4\"> " +
            " Coverage Rate</th> " +
            "<th style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #007CA6; color: #FFFFFF; font-weight: bold; font-size: medium;\"> " +
            " Compliance Rate</th> " +
            "</tr>\n ";
    String empIdInfo = " <tr> " +
            "	<td id=\"empIdInfo\" style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF;\"> &nbsp;</td>" +
            "<td style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF;\"> ${empId}</td>" +
            "<td style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF;\"> ${fileCount}</td>" +
            "	<td style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF;\"> ${empTotalLineNumber}</td>" +
            "	<td style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF; color: #008000;\"> ${empCoverageRate}%</td> " +
            "<td style=\"vertical-align: middle; text-align: center; font-family: Calibri; padding: 0px; margin: 0px; background-color: #FFFFFF; color: #008000;\"> ${empComplianceRate}%</td> " +
            "</tr>\n";
    String tableTagEnd = "</table>\n";
    String divTagStart = "<div id=subItem${teamSeq} style=\"display:none;\">";
    String divTagEnd = "</div>\n";
    String detailPartHead = "<table align=\"center\" style=\"border-style: ridge; border-width: 2px; width:97%;\" cellpadding=\"0\" cellspacing=\"0\" > " +
            "<tr>" +
            "	<th width=\"70%\" style=\"border-style: solid; border-width: 1px; background-color: #D6EBFC\">Class Name</th>" +
            "	<th width=\"10%\" style=\"border-style: solid; border-width: 1px; background-color: #D6EBFC\">Line number</th>" +
            "	<th width=\"10%\" style=\"border-style: solid; border-width: 1px; background-color: #D6EBFC\">Coverage Rate</th>" +
            "	<th width=\"10%\" style=\"border-style: solid; border-width: 1px; background-color: #F6EBFC\">Compliance Rate </th>" +
            "</tr>\n";
    String detailPartContent = "<tr>" +
            "	<td style=\"border-style: solid; border-width: 1px;\"><a target=\"_blank\" href=\"${host}/dashboard/index/${classId}\"> ${className}</a></td>" +
            "	<td style=\"border-style: solid; text-align: left; border-width: 1px;\"> ${classLineNumber}</td>" +
            "	<td style=\"border-style: solid; text-align: left; border-width: 1px;\"> ${classCoverageRate}%</td>" +
            "	<td style=\"border-style: solid; border-width: 1px;\" > ${classComplianceRate}%</td>" +
            "</tr>\n";


    public String writeOntoHtmlTemplate(Report uiReport) {

        String path = System.getProperty("user.dir");
        String host = Client.prop.getProperty("web_host");

        if (host == null) {
            host = "http://jabdl3504.it:9000";
        }

        try {
            BufferedReader htmlTemplate = new BufferedReader(new FileReader(path + "/SonarReport.htm"));

            StringBuilder sb = new StringBuilder();
            String s = "";
            while ((s = htmlTemplate.readLine()) != null) {
                sb.append(s);
            }


            String skipTestHtml = "<span style=\"padding-left: 10px; color: #555;\">";
            skipTestHtml = skipTestHtml + (uiReport.skipTest ? "skipTest" : "no-skipTest");
            skipTestHtml = skipTestHtml + "</span>";

            String sourceStr = sb.toString();
            String summerPartStr = summerPart.replace("${end_time}", DateUtil.formatDate(uiReport.endTime));
            summerPartStr = summerPartStr.replace("${start_time}", DateUtil.formatDate(uiReport.startTime));
            summerPartStr = summerPartStr.replace("${skipTest}", skipTestHtml);
            summerPartStr = summerPartStr.replace("${totalFile}", "" + uiReport.totalFiles);
            summerPartStr = summerPartStr.replace("${project_name}", uiReport.moduleName);
            summerPartStr = summerPartStr.replace("${total_coverage_rate}", String.valueOf(uiReport.coverageRate));
            summerPartStr = summerPartStr.replace("${total_compliance_rate}", String.valueOf(uiReport.complianceRate));
            sourceStr = sourceStr.replace("#SUMMARY_PART#", summerPartStr);

            List<TeamLevelStatisticsInfo> teamList = uiReport.teamLevelStatisticsInfoList;

            StringBuilder content = new StringBuilder();
            System.out.println("teamInfo size  " + teamList.size());

            int teamSeq = 0;
            for (TeamLevelStatisticsInfo teamInfo : teamList) {
                teamSeq++;
                content.append(teamBodyPart);
                String teamId = teamInfo.teamId;
                if (StringUtils.isNotBlank(teamInfo.teamMembers)) {
                    teamId = teamId + "<div style=\"font-size: 10px; color: #555;\">" + teamInfo.teamMembers + "</div>";
                }

                String teamInfoPart = teamDetail.replace("${teamId}", teamId);
                teamInfoPart = teamInfoPart.replace("${teamSeq}", String.valueOf(teamSeq));
                teamInfoPart = teamInfoPart.replace("${teamTotalLineNumber}", String.valueOf(teamInfo.checkedLineNumber));
                teamInfoPart = teamInfoPart.replace("${teamCoverageRate}", String.valueOf(teamInfo.coverageRate));
                teamInfoPart = teamInfoPart.replace("${teamComplianceRate}", String.valueOf(teamInfo.complianceRate));
                content.append(teamInfoPart);
                content.append(tableTagEnd);

                List<EmployeeLevelStatisticsInfo> employeeList = teamInfo.employeeLevelStatisticsInfoList;

                System.out.println("employeeList size  " + employeeList.size());
                String divTagContent = divTagStart.replace("${teamSeq}", String.valueOf(teamSeq));
                content.append(divTagContent);
                for (EmployeeLevelStatisticsInfo singleEmployee : employeeList) {
                    //if has no detail line info , don't display this employee.
                    if (singleEmployee.classLevelStatistiscInfoList == null || singleEmployee.classLevelStatistiscInfoList.size() == 0) {
                        continue;
                    }
                    String empInfoPart = empIdInfo.replace("${empId}", singleEmployee.employeeId);
                    empInfoPart = empInfoPart.replace("${fileCount}", String.valueOf(singleEmployee.classLevelStatistiscInfoList.size()));
                    empInfoPart = empInfoPart.replace("${empTotalLineNumber}", String.valueOf(singleEmployee.checkedLineNumber));
                    empInfoPart = empInfoPart.replace("${empCoverageRate}", String.valueOf(singleEmployee.coverageRate));
                    empInfoPart = empInfoPart.replace("${empComplianceRate}", String.valueOf(singleEmployee.complianceRate));

                    content.append(empBodyPart).append(empInfoPart).append(tableTagEnd);

                    content.append(detailPartHead);
                    for (ClassLevelStatisticsInfo clazzInfo : singleEmployee.classLevelStatistiscInfoList) {
                        String detailPart = detailPartContent.replace("${className}", clazzInfo.className.substring(clazzInfo.className.lastIndexOf(":") + 1));
                        detailPart = detailPart.replace("${classId}", String.valueOf(clazzInfo.classId));
                        detailPart = detailPart.replace("${classLineNumber}", String.valueOf(clazzInfo.checkedLineNumber));
                        detailPart = detailPart.replace("${classCoverageRate}", String.valueOf(clazzInfo.coverageRate));
                        detailPart = detailPart.replace("${classComplianceRate}", String.valueOf(clazzInfo.complianceRate));
                        detailPart = detailPart.replace("${host}", host);
                        content.append(detailPart);
                    }

                    content.append(tableTagEnd);
                    System.out.println(content);
                }
                content.append(divTagEnd).append("<br>");
            }

            sourceStr = sourceStr.replace("#DETAIL_PART#", content);
            File newFile = new File(path + "/" + fileName());
            //newFile.createNewFile();
            FileWriter fw = new FileWriter(newFile);
            System.out.println(sourceStr);
            System.out.println("!!!Please check the file: " + newFile.getAbsolutePath());
            fw.write(sourceStr);
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String fileName() {
        if (this.startDt == null || this.endDt == null) {
            return "test.htm";
        }
        String format = "yy.MMM.dd";

//        return "sonar_report(" + projectName + "# " + DateFormatUtils.format(startDt, format) + " ~ " + DateFormatUtils.format(endDt, format) + ").htm";
//        return projectName + ".html";
        return "sonar_report-" + projectName + "(" + this.startDtStr + "~" + this.endDtStr + ").html";
    }

    public static void main(String[] args) {
//		String path = System.getProperty("user.dir");
//		String fileName = "";
//    	File file = new File(path+ fileName);
        String format = "yy.MMM.dd";

        System.out.println(DateFormatUtils.format(new Date(), format));
    }

}
