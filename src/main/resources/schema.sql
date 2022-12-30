DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS request;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    id    long PRIMARY KEY AUTO_INCREMENT,
    name  varchar(255) NOT NULL,
    email varchar(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS request
(
    id    long PRIMARY KEY AUTO_INCREMENT,
    name  varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id    long PRIMARY KEY AUTO_INCREMENT,
    name  varchar(255) NOT NULL,
    description  varchar(255),
    available boolean,
    owner long  CONSTRAINT items_users_id_fk REFERENCES users (id),
    request long  CONSTRAINT request_users_id_fk REFERENCES request (id),
    PRIMARY KEY (id)
);
