--DROP TABLE if exists public.bookings CASCADE;
--DROP TABLE if exists public."comments" CASCADE;
--DROP TABLE if exists public.items CASCADE;
--DROP TABLE if exists public.requests CASCADE;
--DROP TABLE if exists public.users CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT generated by default as identity primary key,
  name VARCHAR(255) not null,
  email VARCHAR(512) not null,
  constraint uq_users_email unique (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT generated by default as identity primary key,
  description VARCHAR(512) not null,
  requester_id BIGINT not null references users(id),
  created TIMESTAMP without TIME zone not null
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT generated by default as identity primary key,
  name VARCHAR(255) not null,
  description VARCHAR(512),
  is_available BOOLEAN,
  owner_id BIGINT not null references users(id),
  request_id BIGINT references requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT generated by default as identity primary key,
  start_date TIMESTAMP without TIME zone not null,
  end_date TIMESTAMP without TIME zone not null,
  item_id BIGINT not null references items(id),
  booker_id BIGINT not null references users(id),
  status VARCHAR(50) not null
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT generated by default as identity primary key,
  text VARCHAR(512) not null,
  item_id BIGINT references items(id),
  author_id BIGINT references users(id),
  created TIMESTAMP without TIME zone not null
);