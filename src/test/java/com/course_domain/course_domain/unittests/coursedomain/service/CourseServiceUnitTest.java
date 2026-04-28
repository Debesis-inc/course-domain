package com.course_domain.course_domain.unittests.coursedomain.service;

import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.dto.response.CourseResponseDTO;
import com.course_domain.course_domain.exception.CourseNotFoundException;
import com.course_domain.course_domain.exception.DuplicateCourseFieldException;
import com.course_domain.course_domain.mapper.CourseConverter;
import com.course_domain.course_domain.mapper.CourseMapper;
import com.course_domain.course_domain.model.Course;
import com.course_domain.course_domain.repository.CourseRepository;
import com.course_domain.course_domain.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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

    @Test
    void shouldReturnAllCourses() {
        Course firstCourse = course("course-1", "SB101");
        Course secondCourse = course("course-2", "WEB101");
        CourseResponseDTO firstResponse = response("course-1", "SB101");
        CourseResponseDTO secondResponse = response("course-2", "WEB101");

        when(courseRepository.findAll()).thenReturn(List.of(firstCourse, secondCourse));
        when(courseMapper.toResponseDTO(firstCourse)).thenReturn(firstResponse);
        when(courseMapper.toResponseDTO(secondCourse)).thenReturn(secondResponse);

        List<CourseResponseDTO> result = courseService.getCourses();

        assertThat(result).containsExactly(firstResponse, secondResponse);
        verify(courseRepository).findAll();
        verify(courseMapper).toResponseDTO(firstCourse);
        verify(courseMapper).toResponseDTO(secondCourse);
    }

    @Test
    void shouldReturnEmptyListWhenNoCoursesExist() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseResponseDTO> result = courseService.getCourses();

        assertThat(result).isEmpty();
        verify(courseRepository).findAll();
    }

    @Test
    void shouldReturnCourseByIdWhenCourseExists() {
        Course course = course("course-1", "SB101");
        CourseResponseDTO response = response("course-1", "SB101");

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.getCourseById("course-1");

        assertThat(result).isEqualTo(response);
        verify(courseRepository).findById("course-1");
        verify(courseMapper).toResponseDTO(course);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCourseIdDoesNotExist() {
        when(courseRepository.findById("missing-course")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById("missing-course"))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found: missing-course");

        verify(courseRepository).findById("missing-course");
    }

    private CourseRequestDTO validRequest() {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setCode("SB101");
        request.setTitle("Spring Boot Fundamentals");
        request.setDescription("Build a REST API with Spring Boot");
        request.setInstructorId("TM1234");
        return request;
    }

    private Course course(String id, String code) {
        Course course = new Course();
        course.setId(id);
        course.setCode(code);
        course.setTitle("Spring Boot Fundamentals");
        course.setSlug("spring-boot-fundamentals");
        course.setDescription("Build a REST API with Spring Boot");
        course.setInstructorId("TM1234");
        return course;
    }

    private CourseResponseDTO response(String id, String code) {
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId(id);
        response.setCode(code);
        response.setTitle("Spring Boot Fundamentals");
        response.setSlug("spring-boot-fundamentals");
        response.setDescription("Build a REST API with Spring Boot");
        response.setInstructorId("TM1234");
        return response;
    }
}
