package com.course_domain.course_domain.unittests.coursedomain.controller;

import com.course_domain.course_domain.controller.CourseController;
import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.dto.response.CourseResponseDTO;
import com.course_domain.course_domain.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
