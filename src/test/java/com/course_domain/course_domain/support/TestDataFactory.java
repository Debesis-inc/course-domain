package com.course_domain.course_domain.support;

import com.course_domain.course_domain.model.Course;
import com.course_domain.course_domain.model.enums.ClassLevel;
import com.course_domain.course_domain.model.enums.CourseCategory;
import com.course_domain.course_domain.model.enums.CourseStatus;
import com.course_domain.course_domain.model.enums.CourseType;
import com.course_domain.course_domain.model.enums.CurriculumLevel;

import java.time.Instant;
import java.util.Locale;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Course existingCourseWithCode(String code) {
        Course course = new Course();
        course.setInstitutionId("SCH001");
        course.setAcademicYearId("AY2026");
        course.setTermId("TERM1");
        course.setCode(code);
        course.setTitle("Existing Course");
        course.setSlug("existing-course");
        course.setCurriculumLevel(CurriculumLevel.O_LEVEL);
        course.setClassLevel(ClassLevel.S1);
        course.setSubjectId("EXISTING");
        course.setSubjectName("Existing Course");
        course.setCourseCategory(CourseCategory.SCIENCE);
        course.setCourseType(CourseType.COMPULSORY);
        course.setInstructorId("TCH999");
        course.setStatus(CourseStatus.DRAFT);
        return course;
    }

    public static Course persistedCourse(String id, String code, String title, String instructorId) {
        Course course = new Course();
        course.setId(id);
        course.setInstitutionId("SCH001");
        course.setAcademicYearId("AY2026");
        course.setTermId("TERM1");
        course.setCode(code);
        course.setTitle(title);
        course.setSlug(toSlug(title));
        course.setDescription(title + " for Term 1.");
        course.setCurriculumLevel(CurriculumLevel.O_LEVEL);
        course.setClassLevel(ClassLevel.S1);
        course.setStreamId("S1-A");
        course.setSubjectId(code.split("-")[0]);
        course.setSubjectName(title);
        course.setCourseCategory(CourseCategory.SCIENCE);
        course.setCourseType(CourseType.COMPULSORY);
        course.setInstructorId(instructorId);
        course.setInstructorUsername("mr-kato");
        course.setLevel("beginner");
        course.setLanguageCode("en");
        course.setThumbnailUrl("https://cdn.example.com/course.jpg");
        course.setDurationHours(48);
        course.setStatus(CourseStatus.DRAFT);
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
