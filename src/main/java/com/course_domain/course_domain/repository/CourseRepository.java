package com.course_domain.course_domain.repository;

import com.course_domain.course_domain.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByInstructorIdIgnoreCase(String instructorId);

    boolean existsByTitleIgnoreCase(String title);
}
