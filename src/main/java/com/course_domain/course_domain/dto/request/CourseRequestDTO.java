package com.course_domain.course_domain.dto.request;

import com.course_domain.course_domain.model.enums.ClassLevel;
import com.course_domain.course_domain.model.enums.CourseCategory;
import com.course_domain.course_domain.model.enums.CourseStatus;
import com.course_domain.course_domain.model.enums.CourseType;
import com.course_domain.course_domain.model.enums.CurriculumLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseRequestDTO {
    @NotBlank
    @Size(max = 50)
    private String institutionId;

    @NotBlank
    @Size(max = 50)
    private String academicYearId;

    @NotBlank
    @Size(max = 50)
    private String termId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 5000)
    private String description;

    @NotNull
    private CurriculumLevel curriculumLevel;

    @NotNull
    private ClassLevel classLevel;

    @Size(max = 50)
    private String streamId;

    @Size(max = 50)
    private String courseId;

    @Size(max = 150)
    private String courseName;

    @NotNull
    private CourseCategory courseCategory;

    @NotNull
    private CourseType courseType;

    @Size(max = 50)
    private String combinationCode;

    @Size(max = 150)
    private String combinationName;

    @NotBlank
    @Size(max = 50)
    private String instructorId;

    @Size(max = 100)
    private String instructorUsername;

    @Size(max = 50)
    private String level;

    @Size(max = 10)
    private String languageCode;

    @Size(max = 500)
    private String thumbnailUrl;

    @PositiveOrZero
    private Integer durationHours;

    private CourseStatus status;
}
