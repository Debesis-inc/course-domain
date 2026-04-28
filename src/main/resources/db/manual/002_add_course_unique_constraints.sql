ALTER TABLE courses
    ADD CONSTRAINT uk_courses_code UNIQUE (code);

ALTER TABLE courses
    ADD CONSTRAINT uk_courses_instructor_id UNIQUE (instructor_id);

ALTER TABLE courses
    ADD CONSTRAINT uk_courses_title UNIQUE (title);
