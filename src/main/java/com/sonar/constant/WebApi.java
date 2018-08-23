package com.sonar.constant;

public interface WebApi {

    /**
     * https://docs.sonarqube.org/display/SONAR/Metric+Definitions
     *
     * http://jabdl3504.it:9113/web_api/api/
     */

    /**
     * Condition coverage = (CT + CF) / (2*B)
     * <p>
     * where
     * <p>
     * CT = conditions that have been evaluated to 'true' at least once
     * CF = conditions that have been evaluated to 'false' at least once
     * <p>
     * B = total number of conditions
     * <p>
     * severities:
     * INFO
     * MINOR
     * MAJOR
     * CRITICAL
     * BLOCKER
     * <p>
     * statuses:
     * OPEN
     * CONFIRMED
     * REOPENED
     * RESOLVED
     * CLOSED
     * <p>
     * types:
     * CODE_SMELL
     * BUG
     * VULNERABILITY
     **/

    //BLOCKER,CRITICAL
    //http://jabdl3504.it:9113/api/issues/search?facets=authors&severities=&statuses=OPEN,REOPENED&createdAfter=2018-03-06&createdBefore=2018-03-07&componentKeys=com.statestr.gcth:gcth-usecase-remediation-COA20:src/main/java/com/statestr/gcth/coa20/service/impl/MT540PedingDelShareParServiceImpl.java
    String ISSUE_SEARCH = "/api/issues/search?severities=#severityList#&statuses=OPEN,REOPENED&createdAfter=#createdAfter#&createdBefore=#createdBefore#&componentKeys=#componentKeys#";

    //http://jabdl3504.it:9113/api/sources/lines?key=com.statestr.gcth:gcth-usecase-remediation-COA:src/main/java/com/statestr/gcth/usecase/coa/service/impl/EnrichCartReferenceServiceImpl.java
    String LINE_STATISTIC = "/api/sources/lines?key=";

    //http://jabdl3504.it:9113/api/rules/search?rule_key=squid:S2259
    String RULES_SEARCH = "/api/rules/search?rule_key=#ruleKeys#";

    //http://jabdl3504.it.statestr.com:9113/coding_rules#qprofile=java-sonar_ooa_devops-08479|activation=true|types=BUG
    String RULE_LIST = "/coding_rules#qprofile=java-sonar_ooa_devops-08479|activation=true|types=BUG";


    String UT_SEARCH_BY_KEE = "http://jabdl3504.it.statestr.com:9113/api/measures/component?additionalFields=periods&componentKey=#componentKey#&metricKeys=test_success_density,tests,test_failures";

    String UT_SEARCH_BY_ID = "http://jabdl3504.it.statestr.com:9113/api/measures/component?additionalFields=periods&componentId=#componentId#&metricKeys=test_success_density,tests,test_failures";

    String UT_PAGE_SEARCH = "/api/measures/component_tree?asc=true&p=#pageIndex#&ps=500&metricSortFilter=withMeasuresOnly&s=metric%2Cname&metricSort=test_success_density&baseComponentKey=#baseComponentKey#&metricKeys=test_success_density&strategy=leaves";

    //Metrics type
    //http://jabdl3504.it.statestr.com:9113/api/metrics/search?ps=500

    //component tree
    //http://jabdl3504.it.statestr.com:9113/api/measures/component_tree?asc=true&p=12&ps=#pageSize#&metricSortFilter=withMeasuresOnly&p=1&s=metric%2Cname&metricSort=test_success_density&baseComponentKey=com.statestr.gcth:gcth-project-trunk-parent&metricKeys=test_success_density&strategy=leaves

    //overall example
    //http://jabdl3504.it.statestr.com:9113/api/measures/component?metricKeys=line_coverage,new_line_coverage,branch_coverage,new_branch_coverage,overall_uncovered_lines,new_overall_uncovered_lines,uncovered_conditions,new_uncovered_conditions,lines_to_cover,new_lines_to_cover,test_errors,test_failures,skipped_tests,test_success_density,test_execution_time,lines,statements,functions,classes,files,directories&componentKey=com.statestr.gcth:gcth-project-trunk-parent

    String OVER_ALL = "/api/measures/component?metricKeys=#metricKeys#&componentKey=#componentKey#";

    //http://jabdl3504.it.statestr.com:9113/api/measures/component_tree?asc=true&p=1&ps=50&metricSortFilter=withMeasuresOnly&p=1&s=metric%2Cname&metricSort=test_success_density&baseComponentKey=com.statestr.gcth:gcth-project-trunk-parent&metricKeys=test_success_density,test_errors,test_failures&qualifiers=UTS&strategy=all
    String UT_SUCC_RATE_BY_KEE = "/api/measures/component_tree?asc=true&p=1&ps=#pageSize#&metricSortFilter=withMeasuresOnly&p=1&s=metric%2Cname&metricSort=test_success_density&baseComponentKey=#baseComponentKey#&metricKeys=test_success_density,test_errors,test_failures&qualifiers=UTS&strategy=all";


    //http://jabdl3504.it.statestr.com:9113/api/tests/list?p=1&ps=100&testFileKey=com.statestr.gcth:gcth-usecase-inbound:src/test/java/com/statestr/gcth/inbound/processor/transformation/DefaultProcessorTest.java

    String UT_TRACE_BY_KEE = "/api/tests/list?p=1&ps=100&testFileKey=#testFileKey#";

    //http://jabdl3504.it.statestr.com:9113/api/navigation/component?componentKey=com.statestr.gcth%3Agcth-project-trunk-parent
    String SNAPSHOT_BY_KEE = "/api/navigation/component?componentKey=#componentKey#";

    //http://jabdl3504.it.statestr.com:9113/api/measures/component?additionalFields=periods&componentKey=com.statestr.gcth%3Agcth-project-trunk-parent&metricKeys=new_technical_debt%2Cblocker_violations%2Cbugs%2Cburned_budget%2Cbusiness_value%2Cclasses%2Ccode_smells%2Ccomment_lines%2Ccomment_lines_density%2Ccomplexity%2Cclass_complexity%2Cfile_complexity%2Cfunction_complexity%2Cbranch_coverage%2Cnew_it_branch_coverage%2Cnew_branch_coverage%2Cconfirmed_issues%2Ccoverage%2Cnew_it_coverage%2Cnew_coverage%2Ccritical_violations%2Cdirectories%2Cduplicated_blocks%2Cduplicated_files%2Cduplicated_lines%2Cduplicated_lines_density%2Ceffort_to_reach_maintainability_rating_a%2Cfalse_positive_issues%2Cfiles%2Cfunctions%2Cgenerated_lines%2Cgenerated_ncloc%2Cinfo_violations%2Cviolations%2Cit_branch_coverage%2Cit_coverage%2Cit_line_coverage%2Cit_uncovered_conditions%2Cit_uncovered_lines%2Cline_coverage%2Cnew_it_line_coverage%2Cnew_line_coverage%2Clines%2Cncloc%2Clines_to_cover%2Cnew_it_lines_to_cover%2Cnew_lines_to_cover%2Csqale_rating%2Cmajor_violations%2Cminor_violations%2Cnew_blocker_violations%2Cnew_bugs%2Cnew_code_smells%2Cnew_critical_violations%2Cnew_info_violations%2Cnew_violations%2Cnew_major_violations%2Cnew_minor_violations%2Cnew_vulnerabilities%2Copen_issues%2Coverall_branch_coverage%2Cnew_overall_branch_coverage%2Coverall_coverage%2Cnew_overall_coverage%2Coverall_line_coverage%2Cnew_overall_line_coverage%2Cnew_overall_lines_to_cover%2Coverall_uncovered_conditions%2Cnew_overall_uncovered_conditions%2Coverall_uncovered_lines%2Cnew_overall_uncovered_lines%2Cprojects%2Cpublic_api%2Cpublic_documented_api_density%2Cpublic_undocumented_api%2Calert_status%2Creliability_rating%2Creliability_remediation_effort%2Cnew_reliability_remediation_effort%2Creopened_issues%2Csecurity_rating%2Csecurity_remediation_effort%2Cnew_security_remediation_effort%2Cskipped_tests%2Cstatements%2Cteam_size%2Csqale_index%2Csqale_debt_ratio%2Cnew_sqale_debt_ratio%2Cuncovered_conditions%2Cnew_it_uncovered_conditions%2Cnew_uncovered_conditions%2Cuncovered_lines%2Cnew_it_uncovered_lines%2Cnew_uncovered_lines%2Ctest_execution_time%2Ctest_errors%2Ctest_failures%2Ctest_success_density%2Ctests%2Cvulnerabilities%2Cwont_fix_issues
    String MEASUEMENT_BY_KEE = "/api/measures/component?additionalFields=periods&componentKey=#componentKey#&metricKeys=#metricKeys#";

    String METRICS_KEY_LIST = "new_technical_debt,blocker_violations,bugs,burned_budget,business_value,classes,code_smells,comment_lines,comment_lines_density,complexity,class_complexity,file_complexity,function_complexity,branch_coverage,new_it_branch_coverage,new_branch_coverage,confirmed_issues,coverage,new_it_coverage,new_coverage,critical_violations,directories,duplicated_blocks,duplicated_files,duplicated_lines,duplicated_lines_density,effort_to_reach_maintainability_rating_a,false_positive_issues,files,functions,generated_lines,generated_ncloc,info_violations,violations,it_branch_coverage,it_coverage,it_line_coverage,it_uncovered_conditions,it_uncovered_lines,line_coverage,new_it_line_coverage,new_line_coverage,lines,ncloc,lines_to_cover,new_it_lines_to_cover,new_lines_to_cover,sqale_rating,major_violations,minor_violations,new_blocker_violations,new_bugs,new_code_smells,new_critical_violations,new_info_violations,new_violations,new_major_violations,new_minor_violations,new_vulnerabilities,open_issues,overall_branch_coverage,new_overall_branch_coverage,overall_coverage,new_overall_coverage,overall_line_coverage,new_overall_line_coverage,new_overall_lines_to_cover,overall_uncovered_conditions,new_overall_uncovered_conditions,overall_uncovered_lines,new_overall_uncovered_lines,projects,public_api,public_documented_api_density,public_undocumented_api,alert_status,reliability_rating,reliability_remediation_effort,new_reliability_remediation_effort,reopened_issues,security_rating,security_remediation_effort,new_security_remediation_effort,skipped_tests,statements,team_size,sqale_index,sqale_debt_ratio,new_sqale_debt_ratio,uncovered_conditions,new_it_uncovered_conditions,new_uncovered_conditions,uncovered_lines,new_it_uncovered_lines,new_uncovered_lines,test_execution_time,test_errors,test_failures,test_success_density,tests,vulnerabilities,wont_fix_issues";

    //http://jabdl3504.it.statestr.com:9113/api/metrics/search?ps=9999
    String METRICS_DEF = "/api/metrics/search?ps=9999";

}
