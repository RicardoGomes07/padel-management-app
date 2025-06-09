-- all passwords are 'password'
insert into users(name, email, hashed_password) values
    ('Leonel Correia', 'leonel@gmail.com', 's+/9j8R9cA/SY0xOlzLGC3BZkQioifQDK4Z/dCn0Xu0='),
    ('Paulo Carvalho', 'paulo@hotmail.com', 's+/9j8R9cA/SY0xOlzLGC3BZkQioifQDK4Z/dCn0Xu0='),
    ('Ricardo Gomes', 'ricardo@isel.pt', 's+/9j8R9cA/SY0xOlzLGC3BZkQioifQDK4Z/dCn0Xu0=');

insert into clubs(name, owner) values
    ('Benfica', 1),
    ('Porto', 2),
    ('Sporting', 3);

insert into courts(name, club_id) values
    ('Campo A', 1),
    ('Campo B', 1),
    ('Campo C', 1),
    ('Campo 1', 2),
    ('Campo 2', 2),
    ('Campo 3', 2),
    ('Campo Norte', 3),
    ('Campo Sul', 3),
    ('Campo Oeste', 3);

insert into rentals(date_, rd_start, rd_end, renter_id, court_id) values
    -- 14/09/2025 (in epoch days), from 10h to 14h
    (20345, 10, 14, 1, 1),
    -- 25/12/2025 from 10h to 12h
    (20447, 10, 12, 1, 1),
    -- 31/12/2025 from 10h to 12h
    (20453, 11, 15, 1, 2),
    -- 13/01/2026 from 11h to 13h
    (20466, 11, 13, 2, 4),
    -- 03/04/2025 from 9h to 12h
    (20181, 9, 12, 3, 7);