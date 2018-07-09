package com.sonar.model;

public class IssueTextRange {

    private int startLine;

    private int endLine;

    private int startOffset;

    private int endOffset;

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    @Override
    public String toString() {
        return "IssueTextRange{" +
                "startLine=" + startLine +
                ", endLine=" + endLine +
                ", startOffset=" + startOffset +
                ", endOffset=" + endOffset +
                '}';
    }
}
