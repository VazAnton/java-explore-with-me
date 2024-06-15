DROP TABLE IF EXISTS compilations_events;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar NOT NULL,
email varchar UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS locations
(
location_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
lat FLOAT NOT NULL,
lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
annotation varchar NOT NULL,
category_id BIGINT REFERENCES categories(category_id) ON DELETE CASCADE,
confirmed_requests INTEGER NOT NULL,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
description varchar NOT NULL,
event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
location_id BIGINT REFERENCES locations(location_id) ON DELETE CASCADE,
paid BOOLEAN NOT NULL,
participant_limit INTEGER NOT NULL,
published TIMESTAMP WITHOUT TIME ZONE,
request_moderation BOOLEAN NOT NULL,
state varchar NOT NULL,
title varchar NOT NULL,
views INTEGER
);

CREATE TABLE IF NOT EXISTS requests
(
request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
event_id BIGINT REFERENCES events(event_id) ON DELETE CASCADE,
user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
status varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations
(
compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
pinned BOOLEAN NOT NULL,
title varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events
(
compilation_id BIGINT REFERENCES compilations(compilation_id) ON DELETE CASCADE,
event_id BIGINT REFERENCES events(event_id) ON DELETE CASCADE
);