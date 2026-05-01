package com.course_domain.course_domain.unittests.coursedomain.controller;

import com.course_domain.course_domain.controller.CourseController;
import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.dto.response.CourseResponseDTO;
import com.course_domain.course_domain.exception.CourseNotFoundException;
import com.course_domain.course_domain.exception.GlobalExceptionHandler;
import com.course_domain.course_domain.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class CourseControllerUnitTest {

    private final CourseService courseService = mock(CourseService.class);
    private final CourseController courseController = new CourseController(courseService);

    @Test
    void shouldReturnCreatedStatusWhenCourseIsCreated() {
        CourseRequestDTO request = new CourseRequestDTO();
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId("course-id");
        when(courseService.createCourse(request)).thenReturn(response);

        var result = courseController.createCourse(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnOkStatusWhenCoursesAreFetched() {
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId("course-id");
        when(courseService.getCourses()).thenReturn(List.of(response));

        var result = courseController.getCourses();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void shouldReturnOkStatusWhenCourseIsFetchedById() {
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId("course-id");
        when(courseService.getCourseById("course-id")).thenReturn(response);

        var result = courseController.getCourseById("course-id");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnOkStatusWhenCourseIsUpdated() {
        CourseRequestDTO request = new CourseRequestDTO();
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId("course-id");
        when(courseService.updateCourse("course-id", request)).thenReturn(response);

        var result = courseController.updateCourse("course-id", request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnNoContentStatusWhenCourseIsDeleted() {
        var result = courseController.deleteCourse("course-id");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
    }

    @Test
    void shouldReturnOkStatusWhenCourseIsFetchedBySlug() {
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId("course-id");
        response.setSlug("spring-boot-fundamentals");
        when(courseService.getCourseBySlug("spring-boot-fundamentals")).thenReturn(response);

        var result = courseController.getCourseBySlug("spring-boot-fundamentals");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void shouldReturnNotFoundStatusWhenCourseDoesNotExist() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(courseService.getCourseById("missing-course"))
                .thenThrow(new CourseNotFoundException("missing-course"));

        mockMvc.perform(get("/api/v1/courses/missing-course"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/missing-course"));
    }

    @Test
    void shouldReturnNotFoundStatusWhenUpdatingCourseDoesNotExist() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        String requestBody = """
                {
                  "code": "SB101",
                  "title": "Spring Boot Fundamentals",
                  "instructorId": "TM1234"
                }
                """;
        when(courseService.updateCourse(org.mockito.ArgumentMatchers.eq("missing-course"), org.mockito.ArgumentMatchers.any(CourseRequestDTO.class)))
                .thenThrow(new CourseNotFoundException("missing-course"));

        mockMvc.perform(put("/api/v1/courses/missing-course")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/missing-course"));
    }

    @Test
    void shouldReturnBadRequestStatusWhenUpdatingWithInvalidRequest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        String requestBody = """
                {
                  "code": "",
                  "title": "",
                  "instructorId": ""
                }
                """;

        mockMvc.perform(put("/api/v1/courses/course-id")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundStatusWhenDeletingCourseDoesNotExist() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        org.mockito.Mockito.doThrow(new CourseNotFoundException("missing-course"))
                .when(courseService).deleteCourse("missing-course");

        mockMvc.perform(delete("/api/v1/courses/missing-course"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/missing-course"));
    }

    @Test
    void shouldReturnNotFoundStatusWhenSlugDoesNotExist() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        when(courseService.getCourseBySlug("missing-course"))
                .thenThrow(new CourseNotFoundException("missing-course"));

        mockMvc.perform(get("/api/v1/courses/slug/missing-course"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Course not found: missing-course"))
                .andExpect(jsonPath("$.path").value("/api/v1/courses/slug/missing-course"));
    }
}
