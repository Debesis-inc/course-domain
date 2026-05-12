package com.course_domain.course_domain.unittests.coursedomain.service;

import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.dto.response.CourseResponseDTO;
import com.course_domain.course_domain.exception.CourseNotFoundException;
import com.course_domain.course_domain.exception.DuplicateCourseFieldException;
import com.course_domain.course_domain.mapper.CourseConverter;
import com.course_domain.course_domain.mapper.CourseMapper;
import com.course_domain.course_domain.model.Course;
import com.course_domain.course_domain.model.enums.ClassLevel;
import com.course_domain.course_domain.model.enums.CourseCategory;
import com.course_domain.course_domain.model.enums.CourseStatus;
import com.course_domain.course_domain.model.enums.CourseType;
import com.course_domain.course_domain.model.enums.CurriculumLevel;
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
        response.setSlug("biology");

        when(courseConverter.toEntity(request)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.createCourse(request);

        assertThat(result.getSlug()).isEqualTo("biology");
        assertThat(course.getSlug()).isEqualTo("biology");
        assertThat(course.getCreatedAt()).isNotNull();
        assertThat(course.getUpdatedAt()).isNotNull();
        verify(courseRepository).save(course);
    }

    @Test
    void shouldRejectDuplicateCourseCode() {
        CourseRequestDTO request = validRequest();
        when(courseRepository.existsByCodeIgnoreCase("BIO-S1-T1-2026")).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(request))
                .isInstanceOf(DuplicateCourseFieldException.class)
                .hasMessage("Course code already exists: BIO-S1-T1-2026");
    }

    @Test
    void shouldReturnAllCourses() {
        Course firstCourse = course("course-1", "BIO-S1-T1-2026");
        Course secondCourse = course("course-2", "PHY-S1-T1-2026");
        CourseResponseDTO firstResponse = response("course-1", "BIO-S1-T1-2026");
        CourseResponseDTO secondResponse = response("course-2", "PHY-S1-T1-2026");

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
        Course course = course("course-1", "BIO-S1-T1-2026");
        CourseResponseDTO response = response("course-1", "BIO-S1-T1-2026");

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
        request.setTermId("TERM2");
        request.setCode("PHY-S1-T2-2026");
        request.setTitle("Physics");
        request.setCourseId("PHY");
        request.setCourseName("Physics");
        request.setInstructorId("TCH002");
        request.setInstructorUsername("ms-nambi");
        request.setStatus(CourseStatus.ACTIVE);
        Course course = course("course-1", "OLD101");
        CourseResponseDTO response = response("course-1", "PHY-S1-T2-2026");
        response.setTitle("Physics");
        response.setSlug("physics");

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseRepository.findByCodeIgnoreCase("PHY-S1-T2-2026")).thenReturn(Optional.empty());
        when(courseRepository.findByInstructorIdIgnoreCase("TCH002")).thenReturn(Optional.empty());
        when(courseRepository.findByTitleIgnoreCase("Physics")).thenReturn(Optional.empty());
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.updateCourse("course-1", request);

        assertThat(result).isEqualTo(response);
        assertThat(course.getInstitutionId()).isEqualTo("SCH001");
        assertThat(course.getAcademicYearId()).isEqualTo("AY2026");
        assertThat(course.getTermId()).isEqualTo("TERM2");
        assertThat(course.getCode()).isEqualTo("PHY-S1-T2-2026");
        assertThat(course.getTitle()).isEqualTo("Physics");
        assertThat(course.getSlug()).isEqualTo("physics");
        assertThat(course.getSubjectId()).isEqualTo("PHY");
        assertThat(course.getSubjectName()).isEqualTo("Physics");
        assertThat(course.getInstructorUsername()).isEqualTo("ms-nambi");
        assertThat(course.getStatus()).isEqualTo(CourseStatus.ACTIVE);
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
        Course duplicateCourse = course("course-2", "BIO-S1-T1-2026");

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseRepository.findByCodeIgnoreCase("BIO-S1-T1-2026")).thenReturn(Optional.of(duplicateCourse));

        assertThatThrownBy(() -> courseService.updateCourse("course-1", request))
                .isInstanceOf(DuplicateCourseFieldException.class)
                .hasMessage("Course code already exists: BIO-S1-T1-2026");

        verify(courseRepository).findById("course-1");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void shouldRejectDuplicateCourseTitleWhenUpdatingCourse() {
        CourseRequestDTO request = validRequest();
        Course course = course("course-1", "OLD101");
        Course duplicateCourse = course("course-2", "PHY-S1-T1-2026");

        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(courseRepository.findByCodeIgnoreCase("BIO-S1-T1-2026")).thenReturn(Optional.empty());
        when(courseRepository.findByInstructorIdIgnoreCase("TCH001")).thenReturn(Optional.empty());
        when(courseRepository.findByTitleIgnoreCase("Biology")).thenReturn(Optional.of(duplicateCourse));

        assertThatThrownBy(() -> courseService.updateCourse("course-1", request))
                .isInstanceOf(DuplicateCourseFieldException.class)
                .hasMessage("Course title already exists: Biology");

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
        when(courseRepository.findByCodeIgnoreCase("BIO-S1-T1-2026")).thenReturn(Optional.empty());
        when(courseRepository.findByInstructorIdIgnoreCase("TCH001")).thenReturn(Optional.empty());
        when(courseRepository.findByTitleIgnoreCase("Biology")).thenReturn(Optional.empty());
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponseDTO(course)).thenReturn(response("course-1", "BIO-S1-T1-2026"));

        courseService.updateCourse("course-1", request);

        assertThat(course.getId()).isEqualTo("course-1");
        assertThat(course.getCreatedAt()).isEqualTo(createdAt);
        assertThat(course.getUpdatedAt()).isNotNull();
        assertThat(course.getUpdatedAt()).isAfter(createdAt);
    }

    @Test
    void shouldDeleteCourseWhenCourseExists() {
        Course course = course("course-1", "BIO-S1-T1-2026");
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
        Course course = course("course-1", "BIO-S1-T1-2026");
        CourseResponseDTO response = response("course-1", "BIO-S1-T1-2026");

        when(courseRepository.findBySlug("biology")).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDTO(course)).thenReturn(response);

        CourseResponseDTO result = courseService.getCourseBySlug("biology");

        assertThat(result).isEqualTo(response);
        verify(courseRepository).findBySlug("biology");
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
        request.setInstitutionId("SCH001");
        request.setAcademicYearId("AY2026");
        request.setTermId("TERM1");
        request.setCode("BIO-S1-T1-2026");
        request.setTitle("Biology");
        request.setDescription("Senior 1 Biology for Term 1.");
        request.setCurriculumLevel(CurriculumLevel.O_LEVEL);
        request.setClassLevel(ClassLevel.S1);
        request.setStreamId("S1-A");
        request.setCourseId("BIO");
        request.setCourseName("Biology");
        request.setCourseCategory(CourseCategory.SCIENCE);
        request.setCourseType(CourseType.COMPULSORY);
        request.setInstructorId("TCH001");
        request.setInstructorUsername("mr-kato");
        request.setLanguageCode("en");
        request.setDurationHours(48);
        request.setStatus(CourseStatus.DRAFT);
        return request;
    }

    private Course course(String id, String code) {
        Course course = new Course();
        course.setId(id);
        course.setInstitutionId("SCH001");
        course.setAcademicYearId("AY2026");
        course.setTermId("TERM1");
        course.setCode(code);
        course.setTitle("Biology");
        course.setSlug("biology");
        course.setDescription("Senior 1 Biology for Term 1.");
        course.setCurriculumLevel(CurriculumLevel.O_LEVEL);
        course.setClassLevel(ClassLevel.S1);
        course.setSubjectId("BIO");
        course.setSubjectName("Biology");
        course.setCourseCategory(CourseCategory.SCIENCE);
        course.setCourseType(CourseType.COMPULSORY);
        course.setInstructorId("TCH001");
        course.setStatus(CourseStatus.DRAFT);
        return course;
    }

    private CourseResponseDTO response(String id, String code) {
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId(id);
        response.setInstitutionId("SCH001");
        response.setAcademicYearId("AY2026");
        response.setTermId("TERM1");
        response.setCode(code);
        response.setTitle("Biology");
        response.setSlug("biology");
        response.setDescription("Senior 1 Biology for Term 1.");
        response.setCurriculumLevel(CurriculumLevel.O_LEVEL);
        response.setClassLevel(ClassLevel.S1);
        response.setCourseId("BIO");
        response.setCourseName("Biology");
        response.setCourseCategory(CourseCategory.SCIENCE);
        response.setCourseType(CourseType.COMPULSORY);
        response.setInstructorId("TCH001");
        response.setStatus(CourseStatus.DRAFT);
        return response;
    }
}
