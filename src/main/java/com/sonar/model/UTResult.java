package com.sonar.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UTResult {

    private List<UTDetailVO> tests = new ArrayList<>();

    private List<UTDetailVO> noOk = new ArrayList<>();

    public List<UTDetailVO> getTests() {
        return tests;
    }

    public void setTests(List<UTDetailVO> tests) {
        this.tests = tests;
    }

    public void add(UTDetailVO utDetailVO) {
        tests.add(utDetailVO);
        if (!StringUtils.equalsIgnoreCase("OK", utDetailVO.getStatus())) {
            noOk.add(utDetailVO);
        }

    }

    public List<UTDetailVO> getNoOk() {
        return noOk;
    }

    public void setNoOk(List<UTDetailVO> noOk) {
        this.noOk = noOk;
    }


    public void printNoOK() {
        if (CollectionUtils.isEmpty(noOk)) {
            System.out.println("###################################################################################");
            System.out.println("########################### Congratulations, there is no fail stack trace!");
            System.out.println("###################################################################################");
            return;
        }

        for (UTDetailVO vo : noOk) {
            System.out.println("###################################################################################");
            System.out.println("########################### File Name: " + vo.getFileName());
            System.out.println("########################### Unit Test: " + vo.getName());
            System.out.println("########################### Status: " + vo.getStatus());
            System.out.println("########################### Stacktrace: " + vo.getStacktrace());
        }
    }


    @Override
    public String toString() {
        return "UTResult{" +
                "tests=" + tests +
                ", noOk=" + noOk +
                '}';
    }
}
