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
import static org.mockito.Mockito.never;
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

    @Test
    void shouldUpdateCourseWhenCourseExists() {
        CourseRequestDTO request = validRequest();
        request.setTitle("Updated Spring Boot Fundamentals");
        Course course = course("course-1", "OLD101");
        CourseResponseDTO response = response("course-1", "SB101");
        response.setTitle("Updated Spring Boot Fundamentals");
        response.setSlug("updated-spring-boot-fundamentals");

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseRepository.findByCodeIgnoreCase("SB101")).thenReturn(Optional.empty());
        when(courseRepository.findByInstructorIdIgnoreCase("TM1234")).thenReturn(Optional.empty());
        when(courseRepository.findByTitleIgnoreCase("Updated Spring Boot Fundamentals")).thenReturn(Optional.empty());
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.updateCourse("course-1", request);

        assertThat(result).isEqualTo(response);
        assertThat(course.getCode()).isEqualTo("SB101");
        assertThat(course.getTitle()).isEqualTo("Updated Spring Boot Fundamentals");
        assertThat(course.getSlug()).isEqualTo("updated-spring-boot-fundamentals");
        assertThat(course.getId()).isEqualTo("course-1");
        verify(courseRepository).findById("course-1");
        verify(courseRepository).save(course);
        verify(courseMapper).toResponseDTO(course);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingUnknownCourseId() {
        CourseRequestDTO request = validRequest();
        when(courseRepository.findById("missing-course")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.updateCourse("missing-course", request))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found: missing-course");

        verify(courseRepository).findById("missing-course");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldRejectDuplicateCourseCodeWhenUpdatingCourse() {
        CourseRequestDTO request = validRequest();
        Course course = course("course-1", "OLD101");
        Course duplicateCourse = course("course-2", "SB101");

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseRepository.findByCodeIgnoreCase("SB101")).thenReturn(Optional.of(duplicateCourse));

        assertThatThrownBy(() -> courseService.updateCourse("course-1", request))
                .isInstanceOf(DuplicateCourseFieldException.class)
                .hasMessage("Course code already exists: SB101");

        verify(courseRepository).findById("course-1");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldRejectDuplicateCourseTitleWhenUpdatingCourse() {
        CourseRequestDTO request = validRequest();
        Course course = course("course-1", "OLD101");
        Course duplicateCourse = course("course-2", "WEB101");

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseRepository.findByCodeIgnoreCase("SB101")).thenReturn(Optional.empty());
        when(courseRepository.findByInstructorIdIgnoreCase("TM1234")).thenReturn(Optional.empty());
        when(courseRepository.findByTitleIgnoreCase("Spring Boot Fundamentals")).thenReturn(Optional.of(duplicateCourse));

        assertThatThrownBy(() -> courseService.updateCourse("course-1", request))
                .isInstanceOf(DuplicateCourseFieldException.class)
                .hasMessage("Course title already exists: Spring Boot Fundamentals");

        verify(courseRepository).findById("course-1");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldPreserveIdAndCreatedAtDuringUpdate() {
        CourseRequestDTO request = validRequest();
        Course course = course("course-1", "OLD101");
        var createdAt = java.time.Instant.parse("2026-04-28T10:00:00Z");
        course.setCreatedAt(createdAt);

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseRepository.findByCodeIgnoreCase("SB101")).thenReturn(Optional.empty());
        when(courseRepository.findByInstructorIdIgnoreCase("TM1234")).thenReturn(Optional.empty());
        when(courseRepository.findByTitleIgnoreCase("Spring Boot Fundamentals")).thenReturn(Optional.empty());
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponseDTO(course)).thenReturn(response("course-1", "SB101"));

        courseService.updateCourse("course-1", request);

        assertThat(course.getId()).isEqualTo("course-1");
        assertThat(course.getCreatedAt()).isEqualTo(createdAt);
        assertThat(course.getUpdatedAt()).isNotNull();
        assertThat(course.getUpdatedAt()).isAfter(createdAt);
    }

    @Test
    void shouldDeleteCourseWhenCourseExists() {
        Course course = course("course-1", "SB101");
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));

        courseService.deleteCourse("course-1");

        verify(courseRepository).findById("course-1");
        verify(courseRepository).delete(course);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingUnknownCourseId() {
        when(courseRepository.findById("missing-course")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourse("missing-course"))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found: missing-course");

        verify(courseRepository).findById("missing-course");
        verify(courseRepository, never()).delete(any(Course.class));
    }

    @Test
    void shouldReturnCourseBySlugWhenSlugExists() {
        Course course = course("course-1", "SB101");
        CourseResponseDTO response = response("course-1", "SB101");

        when(courseRepository.findBySlug("spring-boot-fundamentals")).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.getCourseBySlug("spring-boot-fundamentals");

        assertThat(result).isEqualTo(response);
        verify(courseRepository).findBySlug("spring-boot-fundamentals");
        verify(courseMapper).toResponseDTO(course);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSlugDoesNotExist() {
        when(courseRepository.findBySlug("missing-course")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseBySlug("missing-course"))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessage("Course not found: missing-course");

        verify(courseRepository).findBySlug("missing-course");
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
