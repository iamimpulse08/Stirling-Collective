create table IF NOT EXISTS businesses (
    id UUID PRIMARY KEY,
    name varchar(255),
    email varchar(255),
    address varchar(255),
    postcode varchar(255),
    phone varchar(255),
    website varchar(255),
    logo_uri varchar(255)
);

create table IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    business_id UUID,
    name varchar(255),
    category varchar(255),
    quantity int,
    price decimal(10,2),
    image_uri varchar(255),
    foreign key (business_id) references businesses(id)
);

CREATE INDEX products_business_id_idx ON products (business_id);

create table IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email varchar(255) UNIQUE NOT NULL,
    password varchar(255) NOT NULL,
    role varchar(255) NOT NULL DEFAULT 'VIEWER',
    business_id UUID REFERENCES businesses(id) ON DELETE SET NULL,
    check ( role in ('VIEWER', 'OPERATOR', 'BUSINESS', 'PARTNER_VIEWER') )
);

create table IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiration TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX refresh_tokens_revoked_idx ON refresh_tokens (revoked);
CREATE INDEX refresh_tokens_expiration_idx ON refresh_tokens (expiration);
CREATE INDEX refresh_tokens_user_id_idx ON refresh_tokens (user_id);