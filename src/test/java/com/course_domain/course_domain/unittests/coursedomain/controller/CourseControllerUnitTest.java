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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
