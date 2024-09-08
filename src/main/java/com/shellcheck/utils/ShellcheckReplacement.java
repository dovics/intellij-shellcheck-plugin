package com.shellcheck.utils;

public class ShellcheckReplacement {
    public enum InsertionPoint {
        BEFORE_START("beforeStart"),
        AFTER_END("afterEnd");

        private final String description;

        InsertionPoint(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public int column;
    public int endColumn;
    public int line;
    public int endLine;
    public InsertionPoint insertionPoint;
    public int precedence;
    public String replacement;

    ShellcheckReplacement(int line, int column, int endLine, int endColumn, String replacement, InsertionPoint insertionPoint, int precedence) {
        this.line = line;
        this.column = column;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.replacement = replacement;
        this.insertionPoint = insertionPoint;
        this.precedence = precedence;
    }

    public int getPrecedence() {
        return precedence;
    }


}