package com.course_domain.course_domain.exception;

public class DuplicateCourseFieldException extends RuntimeException {

    private final String field;
    private final String value;

    public DuplicateCourseFieldException(String field, String value) {
        super("Course " + field + " already exists: " + value);
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
