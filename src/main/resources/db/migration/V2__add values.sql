INSERT INTO `users`(`email`, `password`, `role`) VALUES ('user@gmail.com', '$2a$10$DAJw87bOrV.8HFxUgO2qsulbnqZA2le6PpixvgQDWSXPChy6YaTme', 'USER');
INSERT INTO `users`(`email`, `password`, `role`) VALUES ('user2@gmail.com', '$2a$10$SV3oTxolhiSWsuckNfP83u6U4y0jwRFzb/DY9BjtIxqdfqg33jRye', 'USER');

INSERT INTO `products`(`price`, `title`) VALUES (300, 'book');
INSERT INTO `products`(`price`, `title`) VALUES (2000, 'phone');
INSERT INTO `products`(`price`, `title`) VALUES (10, 'pen');
INSERT INTO `products`(`price`, `title`) VALUES (5000, 'toilet');

INSERT INTO `shop_items`(`product_id`, `available`) VALUES (1, 100);
INSERT INTO `shop_items`(`product_id`, `available`) VALUES (2, 10);
INSERT INTO `shop_items`(`product_id`, `available`) VALUES (3, 5000);
INSERT INTO `shop_items`(`product_id`, `available`) VALUES (4, 50);