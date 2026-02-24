insert into users (id, email, first_name, last_name, username)
values (RANDOM_UUID(), 'ivan@mail.ru', 'Иван', 'Иванов', 'ivanov'),
       (RANDOM_UUID(), 'petr@mail.ru', 'Петр', 'Петров', 'petrov'),
       (RANDOM_UUID(), 'maria@mail.ru', 'Мария', 'Сидорова', 'maria_s'),
       (RANDOM_UUID(), 'alex@mail.ru', 'Алексей', 'Алексеев', 'alex_alex'),
       (RANDOM_UUID(), 'user13@mainl.ru', 'Oleg', 'Olegovich', 'olegik88');

insert into tasks (id, title, description, status, priority, author_id, assignee_id)
values (RANDOM_UUID(),
        'Разработать API',
        'Создать REST контроллеры для задач',
        'TO_DO',
        'HIGH',
        (select id from users where username = 'ivanov'),
        (select id from users where username = 'petrov')),

       (RANDOM_UUID(),
        'Написать тесты',
        'Покрыть код юнит-тестами',
        'IN_PROGRESS',
        'MEDIUM',
        (select id from users where username = 'petrov'),
        (select id from users where username = 'maria_s')),

       (RANDOM_UUID(),
        'Обновить документацию',
        'Добавить описание новых эндпоинтов',
        'TO_DO',
        'LOW',
        (select id from users where username = 'ivanov'),
        null);

insert into tasks (id, title, description, author_id, assignee_id)
values (RANDOM_UUID(),
        'Теcтовое задание с дефолтными значениями',
        'Создать REST контроллеры для задач',
        (select id from users where username = 'ivanov'),
        (select id from users where username = 'petrov'));