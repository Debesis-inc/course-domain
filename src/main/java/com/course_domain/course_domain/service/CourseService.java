package com.course_domain.course_domain.service;

import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.dto.response.CourseResponseDTO;

import java.util.List;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO);

    List<CourseResponseDTO> getCourses();

    CourseResponseDTO getCourseById(String id);

    CourseResponseDTO updateCourse(String id, CourseRequestDTO courseRequestDTO);

    void deleteCourse(String id);

    CourseResponseDTO getCourseBySlug(String slug);
}
