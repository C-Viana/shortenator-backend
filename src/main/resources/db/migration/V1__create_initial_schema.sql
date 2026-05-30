CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL
);

CREATE TABLE urls (
    id                    BIGSERIAL PRIMARY KEY,
    name_url              VARCHAR(100) NOT NULL,
    source_domain         VARCHAR(255) NOT NULL,
    source_url            TEXT NOT NULL,
    shortened_url_code    VARCHAR(10) NOT NULL UNIQUE,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    expires_at            TIMESTAMP WITH TIME ZONE,
    user_id               BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_urls_code ON urls(shortened_url_code);
CREATE INDEX idx_urls_user_id ON urls(user_id);
CREATE INDEX idx_urls_source_domain ON urls(source_domain);