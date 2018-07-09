package com.sonar.model;

public class IssueLocation {

    private IssueTextRange textRange;

    /**
     * +1
     */
    private String msg;


    public IssueTextRange getTextRange() {
        return textRange;
    }

    public void setTextRange(IssueTextRange textRange) {
        this.textRange = textRange;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "IssueLocation{" +
                "textRange=" + textRange +
                ", msg='" + msg + '\'' +
                '}';
    }
}
