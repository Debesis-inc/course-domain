package com.course_domain.course_domain.integrationtests.coursedomain.support;

import com.course_domain.course_domain.integrationtests.coursedomain.config.WireMockTestConfig;
import com.course_domain.course_domain.repository.CourseRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(WireMockTestConfig.class)
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    static MongoDBContainer mongo =
            new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CourseRepository courseRepository;

    @Autowired
    protected WireMockServer wireMockServer;

    @BeforeEach
    void resetIntegrationTestState() {
        courseRepository.deleteAll();
        WireMockStubs.reset(wireMockServer);
    }
}
