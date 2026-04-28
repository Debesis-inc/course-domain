package com.course_domain.course_domain.mapper;

import com.course_domain.course_domain.dto.request.CourseRequestDTO;
import com.course_domain.course_domain.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseConverter {


    @Mapping(target = "slug", ignore = true)

    Course toEntity(CourseRequestDTO courseRequestDTO);
}
