CREATE TABLE IF NOT EXISTS rating (
    rating_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    rating_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    login VARCHAR(100) NOT NULL,
    user_name VARCHAR(100),
    birthday_date DATE
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    rating_id BIGINT REFERENCES rating(rating_id) ON DELETE CASCADE,
    duration INTEGER NOT NULL CHECK (duration > 0),
    release_date DATE
);

CREATE TABLE IF NOT EXISTS user_friends (
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id BIGINT REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS user_film_likes (
    film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);
