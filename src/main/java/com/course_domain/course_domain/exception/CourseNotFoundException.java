package com.course_domain.course_domain.exception;

public class CourseNotFoundException extends RuntimeException {

    private final String id;

    public CourseNotFoundException(String id) {
        super("Course not found: " + id);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
