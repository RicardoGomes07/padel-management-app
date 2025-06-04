drop table if exists rentals;
drop table if exists courts;
drop table if exists clubs;
drop table if exists users;


create table users (
    uid serial primary key,
    name varchar(255) not null,
    email varchar(255) unique not null,
    password varchar(255) not null,
    token text unique,
    CONSTRAINT unique_email_token UNIQUE (email, token)
);

create table clubs (
    cid serial primary key,
    name varchar(80) unique not null,
    owner int references users(uid) on update cascade on delete set null
);

create table courts (
    crid serial primary key,
    name varchar(255) not null,
    club_id int references clubs(cid) on update cascade on delete cascade
);

create table rentals (
    rid serial primary key,
    date_ int not null,
    rd_start int not null,
    rd_end int not null,
    renter_id int references users(uid) on update cascade on delete set null,
    court_id int references courts(crid) on update cascade on delete set null
);
