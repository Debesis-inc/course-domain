package com.course_domain.course_domain.course;
/*
import static org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCreateCourse() {
        webTestClient.post()
                .uri("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "title": "Spring Boot Fundamentals",
                          "description": "Build a REST API with Spring Boot"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/v1/courses/1")
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Spring Boot Fundamentals")
                .jsonPath("$.description").isEqualTo("Build a REST API with Spring Boot")
                .jsonPath("$.createdAt").isNotEmpty();
    }

    @Test
    void shouldListCourses() {
        createCourse("Spring Boot Fundamentals", "Build a REST API with Spring Boot");
        createCourse("Kafka for Beginners", "Publish and consume domain events");

        webTestClient.get()
                .uri("/api/v1/courses")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].title").isEqualTo("Spring Boot Fundamentals")
                .jsonPath("$[1].title").isEqualTo("Kafka for Beginners");
    }

    private BodyContentSpec createCourse(String title, String description) {
        return webTestClient.post()
                .uri("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "title": "%s",
                          "description": "%s"
                        }
                        """.formatted(title, description))
                .exchange()
                .expectStatus().isCreated()
                .expectBody();
    }
}

 */
