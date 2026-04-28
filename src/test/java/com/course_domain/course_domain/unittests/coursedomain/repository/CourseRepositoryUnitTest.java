package com.course_domain.course_domain.unittests.coursedomain.repository;

import com.course_domain.course_domain.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.repository.MongoRepository;

import static org.assertj.core.api.Assertions.assertThat;

class CourseRepositoryUnitTest {

    @Test
    void shouldUseMongoRepository() {
        assertThat(MongoRepository.class.isAssignableFrom(CourseRepository.class)).isTrue();
    }
}
