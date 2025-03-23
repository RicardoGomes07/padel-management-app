insert into users(name, email) values
    ('Leonel Correia', 'leonel@gmail.com', 'b734312a-94c6-492e-a243-5ebe17e023ca'),
    ('Paulo Carvalho', 'paulo@hotmail.com', '71c78a21-8edd-4711-bfaa-ac50a65e7911'),
    ('Ricardo Gomes', 'ricardo@isel.pt', '501f5a93-c395-4055-888f-3cc4630703ab');

insert into clubs(name, owner) values
    ('Benfica', 1),
    ('Porto', 2),
    ('Sporting', 3);

insert into courts(name, club_id) values
    ('Estádio da Luz', 1),
    ('Estádio do Dragão', 2),
    ('Estádio de Alvalade', 3);

insert into rentals(date_, rd_start, rd_end, renter, court_id) values
    -- 14/09/2025 (in epoch days), from 10h to 14h
    (20345, 10, 14, 1, 1),
    -- 13/01/2026 from 11h to 13h
    (20466, 11, 13, 2, 2),
    -- 03/04/2025 from 9h to 12h
    (20181, 9, 12, 3, 3);