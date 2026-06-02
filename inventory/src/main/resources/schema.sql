create table IF NOT EXISTS Businesses (
    id UUID primary key,
    name varchar(255),
    address varchar(255),
    postcode varchar(255)
);

create table IF NOT EXISTS Products (
    id int primary key,
    business_id UUID,
    name varchar(255),
    price decimal(10,2),
    quantity int,
    foreign key (business_id) references Businesses(id)
);