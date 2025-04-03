# java-filmorate
## Структура [баз данных](https://github.com/danijke/java-filmorate/blob/sql_diagram/diagram_filmorate.jpg) -> [MIRO](https://miro.com/app/board/uXjVIQMs0_4=/?share_link_id=137127835375)
### film - хранит данные о фильме
* film_id [PK] - id фильма
* name - название фильма
* description - описание фильма
* rating [FK] - id рейтинга фильма
* duration - длительность фильма
* release_date - дата выхода фильма

### film_likes - хранит данные о лайках пользователей у фильма
* film_id [PK] - id фильма
* user_id [PK] - id юзера

### rating - хранит данные о рейтингах
* genre_id [PK] - id рейтинга
* name - название рейтинга

### film_genres - хранит данные о жанрах 
* film_id [PK] - id фильма
* genre_id [PK] - id жанра

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
* user_id [PK] - id юзера
* user_friend_id [PK] - id юзера друга
* friend_status [FK] - id статуса друга

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
