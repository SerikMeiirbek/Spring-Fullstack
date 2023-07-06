CREATE SEQUENCE customer_id_sequence;

CREATE TABLE customer(
    id BIGINT DEFAULT nextval('customer_id_sequence') PRIMARY KEY,
    name TEXT NOT null,
    email TEXT NOT null,
    age INT not null
)