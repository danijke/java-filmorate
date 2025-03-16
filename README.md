# java-filmorate
## Структура [баз данных](https://github.com/danijke/java-filmorate/blob/sql_diagram/diagram.jpg) -> [MIRO](https://miro.com/app/board/uXjVIQMs0_4=/?share_link_id=137127835375)
### film - хранит данные о фильме
* film_id [PK] - id фильма
* name - название фильма
* description - описание фильма
* genre [FK] - id жанра
* rating [FK] - id рейтинга фильма
* duration - длительность фильма
* release_date - дата выхода фильма

### film_likes - хранит данные о лайках фильма
* film_id [PK] - id фильма
* user_id [PK] - id юзера

### rating - хранит данные о рейтингах
* genre_id [PK] - id рейтинга
* name - название рейтинга

### genre - хранит данные о жанрах
* genre_id [PK] - id жанра
* name - название жанра

### user - хранит данные о юзерах
* user_id [PK] - id юзера
* email - email юзера
* login - логин юзера
* name - имя юзера
* birhday_date - дата рождения юзера

### user_friends - хранит данные о друзьях юзера
* user_id [PK] - id юзера
* user_friend_id [PK] - id юзера друга
* friend_status [FK] - id статуса друга

### friend_status - хранит данные о статусе друга
* friend_status_id [PK] - id статуса друга
* status_name - наименование статуса друга

## Примеры запросов
### Запрос на вывод 10 самых популярных фильмов(по лайкам)
```sql
SELECT f.name
FROM film f
JOIN (
    SELECT fl.film_id
    FROM film_likes fl
    GROUP BY fl.film_id
    ORDER BY COUNT(fl.user_id) DESC
    LIMIT 10
) top_films ON f.film_id = top_films.film_id;
```
### Запрос на вывод всех подтвержденных друзей пользователя по логину
```sql
SELECT u.name
FROM user u
JOIN user_friends uf ON u.user_id = uf.user_friend_id
JOIN user danijke ON uf.user_id = danijke.user_id
WHERE danijke.login = 'danijke';
```
