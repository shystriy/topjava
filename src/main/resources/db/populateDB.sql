DELETE FROM user_roles;
DELETE FROM meals;
DELETE FROM users;

ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password) VALUES
  ('User', 'user@yandex.ru', 'password'),
  ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001);

INSERT INTO meals (description, date_time, calories, user_id) VALUES
  ('myaso', CAST('2007-04-07 12:35:00.000' AS TIMESTAMP), 1000, 100000),
  ('voda', CAST('2007-04-07 13:35:00.000' AS TIMESTAMP), 100, 100000);
