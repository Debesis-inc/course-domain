ALTER TABLE courses
    ALTER COLUMN instructor_id TYPE varchar(6)
    USING left(replace(instructor_id::text, '-', ''), 6);
