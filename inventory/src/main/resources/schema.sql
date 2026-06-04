create table IF NOT EXISTS businesses (
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(255),
    email varchar(255),
    address varchar(255),
    postcode varchar(255),
    phone varchar(255),
    website varchar(255),
    logo_uri varchar(255)
);

create table IF NOT EXISTS products (
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    business_id int,
    name varchar(255),
    category varchar(255),
    quantity int,
    price decimal(10,2),
    image_uri varchar(255),
    foreign key (business_id) references businesses(id)
);