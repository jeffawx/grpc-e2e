CREATE TABLE users
(
    id    UUID DEFAULT random_uuid() PRIMARY KEY,
    name  VARCHAR NOT NULL,
    email VARCHAR
);
