package com.sonar.constant;

import org.apache.commons.lang.StringUtils;

public enum MetricsEnum {

    //http://jabdl3504.it.xxxxxxxxx.com:9113/api/metrics/search?ps=500


    LINE_COVERAGE("line_coverage", "Line coverage"),

    LINE_COVERAGE_ON_NEW("new_line_coverage", "Line coverage of added/changed code"),

    LINES_TO_COVER("lines_to_cover", "Lines to Cover"),

    LINES_TO_COVER_ON_NEW("new_lines_to_cover", "Lines to cover on new code"),

    CONDITION_COVERAGE("branch_coverage", "Condition coverage"),

    CONDITION_COVERAGE_ON_NEW("new_branch_coverage", "Condition coverage of new/changed code"),

    UNCOVERED_LINES("overall_uncovered_lines", "Uncovered lines by all tests"),

    UNCOVERED_LINES_ON_NEW("new_overall_uncovered_lines", "New lines that are not covered by any tests"),

    UNCOVERED_CONDITIONS("uncovered_conditions", "Uncovered conditions"),

    UNCOVERED_CONDITIONS_ON_NEW("new_uncovered_conditions", "Uncovered conditions on new code"),

    UT_SUCC_RATE("test_success_density", "Density of successful unit tests"),

    UT_FAIL_COUNT("test_failures", "Number of unit test failures"),

    UT_ERROR_COUNT("test_errors", "Number of unit test errors"),

    UT_SKIP_COUNT("skipped_tests", "Number of skipped unit tests"),

    UT_COUNT("tests", "Number of unit tests"),

    LINES("lines", "Number of lines"),

    STATEMENTS("statements", "Number of statements"),

    FUNCTIONS("functions", "Number of functions"),

    CLASSES("classes", "Number of classes"),

    FILES("files", "Number of files"),

    DIRECTORIES("directories", "Number of directories"),

    COMMENT_LINES("comment_lines", "Number of comment lines"),;


    private String code;

    private String message;


    MetricsEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static MetricsEnum codeOf(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (MetricsEnum temp : values()) {
            if (StringUtils.equalsIgnoreCase(temp.code, code)) {
                return temp;

            }
        }
        return null;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
