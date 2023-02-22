drop table if exists orders;
drop table if exists orders_detail;
drop table if exists products;
drop table if exists shop_items;
drop table if exists users;

create table orders(
    id          bigint not null auto_increment,
    canceled_on datetime(6),
    status      varchar(255),
    ordered_on  datetime(6),
    total_price float(53),
    user_id     bigint,
    primary key (id)
);

create table orders_detail
(
    id         bigint not null auto_increment,
    product_id bigint,
    quantity   bigint,
    order_id   bigint not null,
    primary key (id)
);

create table products
(
    id    bigint    not null auto_increment,
    price float(53) not null,
    title varchar(255),
    primary key (id)
);

create table shop_items
(
    product_id bigint not null,
    available  bigint,
    primary key (product_id)
);

create table users
(
    id       bigint not null auto_increment,
    email    varchar(255),
    password varchar(255),
    role     varchar(255),
    primary key (id)
);

alter table users
    add constraint uc_email unique (email);

alter table orders
    add constraint fk_orders_users foreign key (user_id) references users (id);

alter table orders_detail
    add constraint fr_orders_detail_orders foreign key (order_id) references orders (id);

alter table shop_items
    add constraint fk_shop_items_products foreign key (product_id) references products (id);
