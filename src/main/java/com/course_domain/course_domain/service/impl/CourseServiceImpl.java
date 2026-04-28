package com.course_domain.course_domain.service.impl;

import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.dto.response.CourseResponseDTO;
import com.course_domain.course_domain.exception.CourseNotFoundException;
import com.course_domain.course_domain.exception.DuplicateCourseFieldException;
import com.course_domain.course_domain.mapper.CourseConverter;
import com.course_domain.course_domain.mapper.CourseMapper;
import com.course_domain.course_domain.model.Course;
import com.course_domain.course_domain.repository.CourseRepository;
import com.course_domain.course_domain.service.CourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CourseConverter courseConverter;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        validateUniqueCourseFields(courseRequestDTO);

        Course course = courseConverter.toEntity(courseRequestDTO);
        Instant now = Instant.now();
        course.setSlug(toSlug(courseRequestDTO.getTitle()));
        course.setCreatedAt(now);
        course.setUpdatedAt(now);

        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponseDTO(savedCourse);
    }

    @Override
    public List<CourseResponseDTO> getCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toResponseDTO)
                .toList();
    }

    @Override
    public CourseResponseDTO getCourseById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return courseMapper.toResponseDTO(course);
    }

    private void validateUniqueCourseFields(CourseRequestDTO courseRequestDTO) {
        if (courseRepository.existsByCodeIgnoreCase(courseRequestDTO.getCode())) {
            log.warn("Course code already exists: {}", courseRequestDTO.getCode());
            throw new DuplicateCourseFieldException("code", courseRequestDTO.getCode());
        }

        if (courseRepository.existsByInstructorIdIgnoreCase(courseRequestDTO.getInstructorId())) {
            log.warn("Course instructorId already exists: {}", courseRequestDTO.getInstructorId());
            throw new DuplicateCourseFieldException("instructorId", courseRequestDTO.getInstructorId());
        }

        if (courseRepository.existsByTitleIgnoreCase(courseRequestDTO.getTitle())) {
            log.warn("Course title already exists: {}", courseRequestDTO.getTitle());
            throw new DuplicateCourseFieldException("title", courseRequestDTO.getTitle());
        }
    }

    private String toSlug(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
