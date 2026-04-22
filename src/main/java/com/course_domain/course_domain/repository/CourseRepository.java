package com.course_domain.course_domain.repository;

import java.util.List;

import com.course_domain.course_domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
