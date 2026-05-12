package com.course_domain.course_domain.dto.response;

import com.course_domain.course_domain.model.enums.ClassLevel;
import com.course_domain.course_domain.model.enums.CourseCategory;
import com.course_domain.course_domain.model.enums.CourseStatus;
import com.course_domain.course_domain.model.enums.CourseType;
import com.course_domain.course_domain.model.enums.CurriculumLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseResponseDTO {
    private String id;

    private String institutionId;

    private String academicYearId;

    private String termId;

    private String code;

    private String title;

    private String slug;

    private String description;

    private CurriculumLevel curriculumLevel;

    private ClassLevel classLevel;

    private String streamId;

    private String courseId;

    private String courseName;

    private CourseCategory courseCategory;

    private CourseType courseType;

    private String combinationCode;

    private String combinationName;

    private String instructorId;

    private String instructorUsername;

    private String level;

    private String languageCode;

    private String thumbnailUrl;

    private Integer durationHours;

    private CourseStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}
