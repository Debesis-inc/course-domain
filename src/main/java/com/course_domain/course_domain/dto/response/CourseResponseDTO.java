package com.course_domain.course_domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseResponseDTO {
    private String id;

    private String code;

    private String title;

    private String slug;

    private String description;

    private String instructorId;

    private String level;

    private String languageCode;

    private String thumbnailUrl;

    private Integer durationHours;

    private Instant createdAt;

    private Instant updatedAt;
}
