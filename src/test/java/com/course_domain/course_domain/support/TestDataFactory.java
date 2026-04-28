package com.course_domain.course_domain.support;

import com.course_domain.course_domain.model.Course;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Course existingCourseWithCode(String code) {
        Course course = new Course();
        course.setCode(code);
        course.setTitle("Existing Course");
        course.setSlug("existing-course");
        course.setInstructorId("TM9999");
        return course;
    }
}
