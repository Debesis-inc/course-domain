package com.course_domain.course_domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    private String code;

    @NotBlank
    @Size(max = 150)
    private String title;

    @Size(max = 5000)
    private String description;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}\\d{4}$", message = "instructorId must match the format TM1234")
    private String instructorId;

    @Size(max = 50)
    private String level;

    @Size(max = 10)
    private String languageCode;

    @Size(max = 500)
    private String thumbnailUrl;

    @PositiveOrZero
    private Integer durationHours;
}
