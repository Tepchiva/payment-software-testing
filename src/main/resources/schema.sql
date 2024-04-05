-- create customer table
CREATE TABLE IF NOT EXISTS CUSTOMER (
    ID UUID not null,
    name VARCHAR(100),
    phone_no VARCHAR(20),
    email VARCHAR(100),
    primary key (ID)
);

-- create payment table
CREATE TABLE IF NOT EXISTS PAYMENT (
    ID serial4 not null,
    customer_id UUID not null,
    amount DECIMAL(10, 2) not null,
    ccy VARCHAR(3) not null,
    source VARCHAR(100) not null,
    description VARCHAR(100),
    primary key (ID),
    foreign key (customer_id) references CUSTOMER(ID)
);