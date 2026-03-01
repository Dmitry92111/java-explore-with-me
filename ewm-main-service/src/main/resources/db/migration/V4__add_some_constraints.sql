ALTER TABLE users
ALTER COLUMN name TYPE VARCHAR(250),
ALTER COLUMN email TYPE VARCHAR(254);

ALTER TABLE users
ADD CONSTRAINT users_name_min_length CHECK (char_length(name) >= 2),
ADD CONSTRAINT users_email_min_length CHECK (char_length(email) >= 6);