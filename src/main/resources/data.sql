INSERT INTO users (email, password, role, status)
VALUES ('user@mail.com', '$2a$12$VSEmah/.3ClrcW./uGxB0.4GDKrXW.gcDTUhmFujUznMy8mOaspja', 'USER', 'ACTIVE'),
       ('admin@mail.com', '$2a$12$Hdkzd48/v8bABtUBA/Fbvu.u5cmCz6n1KAq/NGAa/pxSYMc.c4GvC', 'ADMIN', 'ACTIVE'),
       ('unlucky1user@mail.com', '$2a$12$VSEmah/.3ClrcW./uGxB0.4GDKrXW.gcDTUhmFujUznMy8mOaspja', 'USER', 'BANNED');

INSERT INTO restaurant (name, address)
VALUES ('McDonald''s', '1201 Broadway, Nashville, TN 37203, United States'),
       ('Burger King', '1501 Charlotte Ave, Nashville, TN 37203, United States');

INSERT INTO dish (name, price, restaurant_id, date_entry)
VALUES ('Big Mac', 10, 1, CURRENT_DATE),
       ('McDouble', 10, 1, CURRENT_DATE),
       ('Quarter Pounder', 10, 1, CURRENT_DATE),
       ('Crispy Chicken Sandwich', 10, 1, CURRENT_DATE),
       ('World Famous Fries', 10, 1, CURRENT_DATE),
       ('Whopper', 10, 2, CURRENT_DATE),
       ('Bacon King', 10, 2, CURRENT_DATE),
       ('Cheeseburger', 10, 2, CURRENT_DATE),
       ('Ch''King Deluxe Sandwich', 10, 2, CURRENT_DATE),
       ('Classic Fries', 10, 2, CURRENT_DATE);

INSERT INTO vote (restaurant_id, user_id, date_entry)
VALUES (2, 1, '2021-01-01'),
       (1, 3, '2021-01-01');