package com.course_domain.course_domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "course")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Course {

    @Id
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

    @CreatedDate
    private Instant createdAt;

    private Instant updatedAt;
}
