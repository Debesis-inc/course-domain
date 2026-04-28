package com.course_domain.course_domain.support;

import com.course_domain.course_domain.model.Course;

import java.time.Instant;
import java.util.Locale;

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

    public static Course persistedCourse(String id, String code, String title, String instructorId) {
        Course course = new Course();
        course.setId(id);
        course.setCode(code);
        course.setTitle(title);
        course.setSlug(toSlug(title));
        course.setDescription("Build a REST API with Spring Boot");
        course.setInstructorId(instructorId);
        course.setLevel("beginner");
        course.setLanguageCode("en");
        course.setThumbnailUrl("https://example.com/spring.png");
        course.setDurationHours(12);
        course.setCreatedAt(Instant.parse("2026-04-28T10:00:00Z"));
        course.setUpdatedAt(Instant.parse("2026-04-28T10:00:00Z"));
        return course;
    }

    private static String toSlug(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
