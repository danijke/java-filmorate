INSERT INTO rating (rating_name) SELECT 'G' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_name = 'G');
INSERT INTO rating (rating_name) SELECT 'PG' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_name = 'PG');
INSERT INTO rating (rating_name) SELECT 'PG-13' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_name = 'PG-13');
INSERT INTO rating (rating_name) SELECT 'R' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_name = 'R');
INSERT INTO rating (rating_name) SELECT 'NC-17' WHERE NOT EXISTS (SELECT 1 FROM rating WHERE rating_name = 'NC-17');

INSERT INTO genres (genre_name) SELECT 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Комедия');
INSERT INTO genres (genre_name) SELECT 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Драма');
INSERT INTO genres (genre_name) SELECT 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Мультфильм');
INSERT INTO genres (genre_name) SELECT 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Триллер');
INSERT INTO genres (genre_name) SELECT 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Документальный');
INSERT INTO genres (genre_name) SELECT 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_name = 'Боевик');

INSERT INTO event_types (name) SELECT 'LIKE' WHERE NOT EXISTS (SELECT 1 FROM event_types WHERE name = 'LIKE');
INSERT INTO event_types (name) SELECT 'REVIEW' WHERE NOT EXISTS (SELECT 1 FROM event_types WHERE name = 'REVIEW');
INSERT INTO event_types (name) SELECT 'FRIEND' WHERE NOT EXISTS (SELECT 1 FROM event_types WHERE name = 'FRIEND');

INSERT INTO operations (name) SELECT 'ADD' WHERE NOT EXISTS (SELECT 1 FROM operations WHERE name = 'ADD');
INSERT INTO operations (name) SELECT 'REMOVE' WHERE NOT EXISTS (SELECT 1 FROM operations WHERE name = 'REMOVE');
INSERT INTO operations (name) SELECT 'UPDATE' WHERE NOT EXISTS (SELECT 1 FROM operations WHERE name = 'UPDATE');
