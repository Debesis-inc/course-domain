package com.course_domain.course_domain.unittests.coursedomain.service;

import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.dto.response.CourseResponseDTO;
import com.course_domain.course_domain.exception.DuplicateCourseFieldException;
import com.course_domain.course_domain.mapper.CourseConverter;
import com.course_domain.course_domain.mapper.CourseMapper;
import com.course_domain.course_domain.model.Course;
import com.course_domain.course_domain.repository.CourseRepository;
import com.course_domain.course_domain.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CourseServiceUnitTest {

    private final CourseRepository courseRepository = mock(CourseRepository.class);
    private final CourseMapper courseMapper = mock(CourseMapper.class);
    private final CourseConverter courseConverter = mock(CourseConverter.class);
    private final CourseServiceImpl courseService = new CourseServiceImpl(
            courseRepository,
            courseMapper,
            courseConverter
    );

    @Test
    void shouldCreateCourseWithSlug() {
        CourseRequestDTO request = validRequest();
        Course course = new Course();
        CourseResponseDTO response = new CourseResponseDTO();
        response.setSlug("spring-boot-fundamentals");

        when(courseConverter.toEntity(request)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.createCourse(request);

        assertThat(result.getSlug()).isEqualTo("spring-boot-fundamentals");
        assertThat(course.getSlug()).isEqualTo("spring-boot-fundamentals");
        assertThat(course.getCreatedAt()).isNotNull();
        assertThat(course.getUpdatedAt()).isNotNull();
        verify(courseRepository).save(course);
    }

    @Test
    void shouldRejectDuplicateCourseCode() {
        CourseRequestDTO request = validRequest();
        when(courseRepository.existsByCodeIgnoreCase("SB101")).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(request))
                .isInstanceOf(DuplicateCourseFieldException.class)
                .hasMessage("Course code already exists: SB101");
    }

    private CourseRequestDTO validRequest() {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setCode("SB101");
        request.setTitle("Spring Boot Fundamentals");
        request.setDescription("Build a REST API with Spring Boot");
        request.setInstructorId("TM1234");
        return request;
    }
}
