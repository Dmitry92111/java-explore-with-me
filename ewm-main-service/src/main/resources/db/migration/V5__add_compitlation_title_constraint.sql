ALTER TABLE compilations
ALTER COLUMN title TYPE VARCHAR(50);

ALTER TABLE compilations
ADD CONSTRAINT compilations_title_min_length
CHECK (char_length(btrim(title)) >= 1);