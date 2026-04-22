package com.course_domain.course_domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseResponseDTO {
    private Long courseId;

    private String title;

    private String description;

    private Instant createdAt;
}
