package com.course_domain.course_domain.repository;

import com.course_domain.course_domain.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByInstructorIdIgnoreCase(String instructorId);

    boolean existsByTitleIgnoreCase(String title);

    Optional<Course> findByCodeIgnoreCase(String code);

    Optional<Course> findByInstructorIdIgnoreCase(String instructorId);

    Optional<Course> findByTitleIgnoreCase(String title);

    Optional<Course> findBySlug(String slug);
}
