package com.course_domain.course_domain.model;

import com.course_domain.course_domain.model.enums.ClassLevel;
import com.course_domain.course_domain.model.enums.CourseCategory;
import com.course_domain.course_domain.model.enums.CourseStatus;
import com.course_domain.course_domain.model.enums.CourseType;
import com.course_domain.course_domain.model.enums.CurriculumLevel;
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

    private String subjectId;

    private String subjectName;

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

    @CreatedDate
    private Instant createdAt;

    private Instant updatedAt;
}
