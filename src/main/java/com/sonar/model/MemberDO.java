package com.sonar.model;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemberDO {
    private String src;

    private String employeeId;

    private String name;

    private String email;

    private List<String> tag = new ArrayList<>();


    public MemberDO(String src, String employeeId) {
        this.src = src;
        this.employeeId = employeeId.toLowerCase();
        this.tag.add(this.employeeId);
        if (StringUtils.isBlank(src) || StringUtils.equalsIgnoreCase(src.trim(), "?")) {
            return;
        }
        String[] arr = src.trim().split("\\|");
        if (arr == null || arr.length == 0) {
            return;
        }
        if (arr.length > 0) {
            name = arr[0].trim();
            this.tag.add(this.name.toLowerCase());
        }
        if (arr.length > 1) {
            email = arr[1].trim();
            this.tag.add(this.email.toLowerCase());
        }
        if (arr.length > 2) {
            splitTag(arr[2]);
        }
    }

    /**
     * @param tag
     */
    private void splitTag(String tag) {
        if (StringUtils.isBlank(tag)) {
            return;
        }
        String[] tagArr = tag.toLowerCase().trim().split(",");
        if (tagArr != null) {
            this.tag.addAll(new ArrayList<String>(Arrays.asList(tagArr)));
        }
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "MemberDO{" +
                "src='" + src + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", tag=" + tag +
                '}';
    }
}
