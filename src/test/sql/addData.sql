insert into users(name, email, hashed_password, token) values
    ('Leonel Correia', 'leonel@gmail.com', '01234567890123456789012345678912', 'b734312a-94c6-492e-a243-5ebe17e023ca'),
    ('Paulo Carvalho', 'paulo@hotmail.com', '01234567890123456789012345678913','71c78a21-8edd-4711-bfaa-ac50a65e7911'),
    ('Ricardo Gomes', 'ricardo@isel.pt', '01234567890123456789012345678914', '501f5a93-c395-4055-888f-3cc4630703ab');

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