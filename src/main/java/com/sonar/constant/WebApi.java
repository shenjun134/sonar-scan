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
}
