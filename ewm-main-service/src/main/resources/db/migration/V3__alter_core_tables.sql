ALTER TABLE events
ALTER COLUMN description TYPE VARCHAR(7000),
ALTER COLUMN annotation TYPE VARCHAR(2000),
ALTER COLUMN title TYPE VARCHAR(120);

ALTER TABLE events
ADD CONSTRAINT events_description_min_length
CHECK (char_length(description) >= 20);

ALTER TABLE events
ADD CONSTRAINT events_annotation_min_length
CHECK (char_length(annotation) >= 20);

ALTER TABLE events
ADD CONSTRAINT events_title_min_length
CHECK (char_length(title) >= 3);

ALTER TABLE events
ADD CONSTRAINT events_participant_limit_min_value
CHECK (participant_limit >= 0);

ALTER TABLE events
ALTER COLUMN lat SET NOT NULL,
ALTER COLUMN lon SET NOT NULL;

ALTER TABLE events
ADD CONSTRAINT lat_value
CHECK (lat BETWEEN -90.0 AND 90.0);

ALTER TABLE events
ADD CONSTRAINT lon_value
CHECK (lon BETWEEN -180.0 AND 180.0);

ALTER TABLE categories
ALTER COLUMN name TYPE VARCHAR(50);

ALTER TABLE categories
ADD CONSTRAINT categories_name_not_blank
CHECK (char_length(btrim(name)) >= 1);

