package com.course_domain.course_domain.integrationtests.coursedomain.controller;

import com.course_domain.course_domain.integrationtests.coursedomain.support.BaseIntegrationTest;
import com.course_domain.course_domain.support.JsonFixtureLoader;
import com.course_domain.course_domain.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldCreateCourseAndPersistDocumentInMongo() throws Exception {
        String requestBody = JsonFixtureLoader.load("fixtures/course-domain/requests/create-course-valid.json");

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(nullValue())))
                .andExpect(jsonPath("$.institutionId").value("SCH001"))
                .andExpect(jsonPath("$.academicYearId").value("AY2026"))
                .andExpect(jsonPath("$.termId").value("TERM1"))
                .andExpect(jsonPath("$.code").value("BIO-S1-T1-2026"))
                .andExpect(jsonPath("$.title").value("Biology"))
                .andExpect(jsonPath("$.slug").value("biology"))
                .andExpect(jsonPath("$.description").value("Senior 1 Biology for Term 1."))
                .andExpect(jsonPath("$.curriculumLevel").value("O_LEVEL"))
                .andExpect(jsonPath("$.classLevel").value("S1"))
                .andExpect(jsonPath("$.streamId").value("S1-A"))
                .andExpect(jsonPath("$.courseId").value("BIO"))
                .andExpect(jsonPath("$.courseName").value("Biology"))
                .andExpect(jsonPath("$.courseCategory").value("SCIENCE"))
                .andExpect(jsonPath("$.courseType").value("COMPULSORY"))
                .andExpect(jsonPath("$.instructorId").value("TCH001"))
                .andExpect(jsonPath("$.instructorUsername").value("mr-kato"))
                .andExpect(jsonPath("$.durationHours").value(48))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.createdAt", not(nullValue())))
                .andExpect(jsonPath("$.updatedAt", not(nullValue())));

        var persistedCourses = courseRepository.findAll();
        assertThat(persistedCourses).hasSize(1);
        assertThat(persistedCourses.getFirst().getCode()).isEqualTo("BIO-S1-T1-2026");
        assertThat(persistedCourses.getFirst().getTitle()).isEqualTo("Biology");
        assertThat(persistedCourses.getFirst().getSlug()).isEqualTo("biology");
        assertThat(persistedCourses.getFirst().getSubjectId()).isEqualTo("BIO");
        assertThat(persistedCourses.getFirst().getSubjectName()).isEqualTo("Biology");
    }

    @Test
    void shouldReturnConflictWhenCourseCodeAlreadyExists() throws Exception {
        courseRepository.save(TestDataFactory.existingCourseWithCode("BIO-S1-T1-2026"));
        String requestBody = JsonFixtureLoader.load("fixtures/course-domain/requests/create-course-valid.json");

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/api/v1/courses"));
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() throws Exception {
        String requestBody = JsonFixtureLoader.load("fixtures/course-domain/requests/create-course-missing-title.json");

        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAllPersistedCourses() throws Exception {
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-1",
                "BIO-S1-T1-2026",
                "Biology",
                "TCH001"
        ));
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-2",
                "PHY-S1-T1-2026",
                "Physics",
                "TCH002"
        ));

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder("course-1", "course-2")))
                .andExpect(jsonPath("$[*].code", containsInAnyOrder("BIO-S1-T1-2026", "PHY-S1-T1-2026")))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Biology",
                        "Physics"
                )));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoCoursesExist() throws Exception {
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnPersistedCourseById() throws Exception {
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-1",
                "BIO-S1-T1-2026",
                "Biology",
                "TCH001"
        ));

        mockMvc.perform(get("/api/v1/courses/course-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("course-1"))
                .andExpect(jsonPath("$.institutionId").value("SCH001"))
                .andExpect(jsonPath("$.academicYearId").value("AY2026"))
                .andExpect(jsonPath("$.termId").value("TERM1"))
                .andExpect(jsonPath("$.code").value("BIO-S1-T1-2026"))
                .andExpect(jsonPath("$.title").value("Biology"))
                .andExpect(jsonPath("$.slug").value("biology"))
                .andExpect(jsonPath("$.description").value("Biology for Term 1."))
                .andExpect(jsonPath("$.curriculumLevel").value("O_LEVEL"))
                .andExpect(jsonPath("$.classLevel").value("S1"))
                .andExpect(jsonPath("$.courseId").value("BIO"))
                .andExpect(jsonPath("$.courseName").value("Biology"))
                .andExpect(jsonPath("$.courseCategory").value("SCIENCE"))
                .andExpect(jsonPath("$.courseType").value("COMPULSORY"))
                .andExpect(jsonPath("$.instructorId").value("TCH001"))
                .andExpect(jsonPath("$.instructorUsername").value("mr-kato"))
                .andExpect(jsonPath("$.level").value("beginner"))
                .andExpect(jsonPath("$.languageCode").value("en"))
                .andExpect(jsonPath("$.thumbnailUrl").value("https://cdn.example.com/course.jpg"))
                .andExpect(jsonPath("$.durationHours").value(48))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldReturnNotFoundWhenCourseIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/courses/missing-course"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/missing-course"));
    }

    @Test
    void shouldUpdatePersistedCourse() throws Exception {
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-1",
                "BIO-S1-T1-2026",
                "Biology",
                "TCH001"
        ));
        String requestBody = JsonFixtureLoader.load("fixtures/course-domain/requests/update-course-valid.json");

        mockMvc.perform(put("/api/v1/courses/course-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("course-1"))
                .andExpect(jsonPath("$.termId").value("TERM2"))
                .andExpect(jsonPath("$.code").value("PHY-S1-T2-2026"))
                .andExpect(jsonPath("$.title").value("Physics"))
                .andExpect(jsonPath("$.slug").value("physics"))
                .andExpect(jsonPath("$.description").value("Senior 1 Physics for Term 2."))
                .andExpect(jsonPath("$.courseId").value("PHY"))
                .andExpect(jsonPath("$.courseName").value("Physics"))
                .andExpect(jsonPath("$.instructorId").value("TCH002"))
                .andExpect(jsonPath("$.instructorUsername").value("ms-nambi"))
                .andExpect(jsonPath("$.durationHours").value(36))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        var persistedCourse = courseRepository.findById("course-1").orElseThrow();
        assertThat(persistedCourse.getCode()).isEqualTo("PHY-S1-T2-2026");
        assertThat(persistedCourse.getTitle()).isEqualTo("Physics");
        assertThat(persistedCourse.getSlug()).isEqualTo("physics");
        assertThat(persistedCourse.getSubjectId()).isEqualTo("PHY");
        assertThat(persistedCourse.getSubjectName()).isEqualTo("Physics");
        assertThat(persistedCourse.getCreatedAt()).isEqualTo(java.time.Instant.parse("2026-04-28T10:00:00Z"));
        assertThat(persistedCourse.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingCourseIdDoesNotExist() throws Exception {
        String requestBody = JsonFixtureLoader.load("fixtures/course-domain/requests/update-course-valid.json");

        mockMvc.perform(put("/api/v1/courses/missing-course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/missing-course"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithInvalidRequest() throws Exception {
        String requestBody = JsonFixtureLoader.load("fixtures/course-domain/requests/create-course-missing-title.json");

        mockMvc.perform(put("/api/v1/courses/course-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenUpdatingCourseCodeAlreadyExists() throws Exception {
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-1",
                "BIO-S1-T1-2026",
                "Biology",
                "TCH001"
        ));
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-2",
                "PHY-S1-T1-2026",
                "Physics",
                "TCH002"
        ));
        String requestBody = """
                {
                  "institutionId": "SCH001",
                  "academicYearId": "AY2026",
                  "termId": "TERM1",
                  "code": "PHY-S1-T1-2026",
                  "title": "Biology Updated",
                  "description": "Updated course description.",
                  "curriculumLevel": "O_LEVEL",
                  "classLevel": "S1",
                  "courseId": "BIO",
                  "courseName": "Biology",
                  "courseCategory": "SCIENCE",
                  "courseType": "COMPULSORY",
                  "instructorId": "TCH001",
                  "level": "beginner",
                  "languageCode": "en",
                  "thumbnailUrl": "https://cdn.example.com/biology-updated.jpg",
                  "durationHours": 30
                }
                """;

        mockMvc.perform(put("/api/v1/courses/course-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Course code already exists: PHY-S1-T1-2026"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/course-1"));
    }

    @Test
    void shouldDeletePersistedCourse() throws Exception {
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-1",
                "BIO-S1-T1-2026",
                "Biology",
                "TCH001"
        ));

        mockMvc.perform(delete("/api/v1/courses/course-1"))
                .andExpect(status().isNoContent());

        assertThat(courseRepository.findById("course-1")).isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingCourseIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/missing-course"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/missing-course"));
    }

    @Test
    void shouldReturnPersistedCourseBySlug() throws Exception {
        courseRepository.save(TestDataFactory.persistedCourse(
                "course-1",
                "BIO-S1-T1-2026",
                "Biology",
                "TCH001"
        ));

        mockMvc.perform(get("/api/v1/courses/slug/biology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("course-1"))
                .andExpect(jsonPath("$.code").value("BIO-S1-T1-2026"))
                .andExpect(jsonPath("$.title").value("Biology"))
                .andExpect(jsonPath("$.slug").value("biology"));
    }

    @Test
    void shouldReturnNotFoundWhenSlugDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/courses/slug/missing-course"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/slug/missing-course"));
    }
}
