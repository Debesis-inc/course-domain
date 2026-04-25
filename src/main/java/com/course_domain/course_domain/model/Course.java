package com.course_domain.course_domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, unique = true, length = 150)
    private String title;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(length = 5000)
    private String description;

    @Column(nullable = false, unique = true, length = 6, columnDefinition = "varchar(6)")
    private String instructorId;

    @Column(length = 50)
    private String level;

    @Column(length = 10)
    private String languageCode;

    @Column(length = 500)
    private String thumbnailUrl;

    private Integer durationHours;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
