INSERT INTO `users`(`email`, `password`, `role`) VALUES ('user@gmail.com', '$2a$10$DAJw87bOrV.8HFxUgO2qsulbnqZA2le6PpixvgQDWSXPChy6YaTme', 'USER');
INSERT INTO `users`(`email`, `password`, `role`) VALUES ('user2@gmail.com', '$2aa$10$wxic4uP575V9BtzPxvYade0vJoM4mj80oN809rGMIdl.4bZDXWiVK', 'USER');

INSERT IGNORE `products`(`available`, `price`, `title`) VALUES (100, 300, 'book');
INSERT IGNORE `products`(`available`, `price`, `title`) VALUES (30, 2000, 'phone');
INSERT IGNORE `products`(`available`, `price`, `title`) VALUES (1000, 10, 'pen');
INSERT IGNORE `products`(`available`, `price`, `title`) VALUES (10, 5000, 'toilet');