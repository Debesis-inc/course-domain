package com.course_domain.course_domain.repository;

import com.course_domain.course_domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByInstructorIdIgnoreCase(String instructorId);

    boolean existsByTitleIgnoreCase(String title);
}
