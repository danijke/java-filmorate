# java-filmorate
## Структура [баз данных](diagram_filmorate.jpg) -> [MIRO](https://miro.com/app/board/uXjVIQMs0_4=/?share_link_id=137127835375)
### films - хранит данные о фильме
* film_id [PK] - id фильма
* name - название фильма
* description - описание фильма
* rating [FK] - id рейтинга фильма
* duration - длительность фильма
* release_date - дата выхода фильма

### film_likes - хранит данные о лайках пользователей у фильма
* film_id [FK] - id фильма
* user_id [FK] - id юзера

### rating - хранит данные о рейтингах
* rating_id [PK] - id рейтинга
* name - название рейтинга

### film_genres - хранит данные о жанрах 
* film_id [FK] - id фильма
* genre_id [FK] - id жанра

### genres - хранит данные видах жанров
* genre_id [PK] - id жанра
* name - название жанра

### users - хранит данные о юзерах
* user_id [PK] - id юзера
* email - email юзера
* login - логин юзера
* name - имя юзера
* birthday_date - дата рождения юзера

### user_friends - хранит данные о друзьях пользователя
* user_id [FK] - id юзера
* user_friend_id [FK] - id юзера друга

### directors - хранит данные о режиссерах
* director_id [PK] - id режиссера
* director_name - имя режиссера

### films_directors - хранит данные о режиссерах в фильмах
* film_id [FK] - id режиссера
* director_id [FK] - имя режиссера

### reviews - хранит данные о отзывах пользователей на фильмы
* review_id [PK] - id отзыва
* content - содержание отзыва
* is_positive - тип отзыва
* user_id [FK] - id юзера
* film_id [FK] - id фильма
* useful - рейтинг отзыва

### review_useful - хранит данные о режиссерах в фильмах
* review_id [FK] - id отзыва
* user_id [FK] - id юзера оценивщего отзыв
* useful - тип оценки (полезность)

### feed - хранит данные о событиях/действиях пользователя
* event_id [PK] - id события
* timestamp - дата события
* user_id [FK] - id юзера 
* event_type_id [FK] - id типы события
* operation_id [FK] - id операции события
* entity_id - id сущности

### operations - хранит данные о типах операций
* operation_id [PK] - id операции
* name - название операции (удаление, добавление, обновление)

### event_types - хранит данные о типах событий
* event_type_id [PK] - id типа события
* name - название события (лайк, отзыв, друзья)

## Примеры запросов
### Запрос на вывод 10 самых популярных фильмов(по лайкам)
```sql
SELECT f.*,
       r.*,
       COUNT(user_id) likes
FROM user_film_likes ufl
JOIN films f ON ufl.film_id = f.film_id
JOIN rating r ON f.rating_id = r.rating_id
GROUP BY ufl.film_id
ORDER BY likes DESC
LIMIT ?
```
### Запрос на вывод общих друзей
```sql
SELECT u.* 
FROM users u
JOIN user_friends uf1 ON u.user_id = uf1.friend_id
JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id
WHERE uf1.user_id = ? AND uf2.user_id = ?;
```
