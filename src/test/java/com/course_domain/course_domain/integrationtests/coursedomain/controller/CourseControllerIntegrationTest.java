package com.course_domain.course_domain.integrationtests.coursedomain.controller;

import com.course_domain.course_domain.integrationtests.coursedomain.support.BaseIntegrationTest;
import com.course_domain.course_domain.support.JsonFixtureLoader;
import com.course_domain.course_domain.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$.code").value("SB101"))
                .andExpect(jsonPath("$.title").value("Spring Boot Fundamentals"))
                .andExpect(jsonPath("$.slug").value("spring-boot-fundamentals"))
                .andExpect(jsonPath("$.description").value("Build a REST API with Spring Boot"))
                .andExpect(jsonPath("$.instructorId").value("TM1234"))
                .andExpect(jsonPath("$.createdAt", not(nullValue())))
                .andExpect(jsonPath("$.updatedAt", not(nullValue())));

        var persistedCourses = courseRepository.findAll();
        assertThat(persistedCourses).hasSize(1);
        assertThat(persistedCourses.getFirst().getCode()).isEqualTo("SB101");
        assertThat(persistedCourses.getFirst().getTitle()).isEqualTo("Spring Boot Fundamentals");
        assertThat(persistedCourses.getFirst().getSlug()).isEqualTo("spring-boot-fundamentals");
    }

    @Test
    void shouldReturnConflictWhenCourseCodeAlreadyExists() throws Exception {
        courseRepository.save(TestDataFactory.existingCourseWithCode("SB101"));
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
}
