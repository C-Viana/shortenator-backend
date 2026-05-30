CREATE TABLE url_access_logs (
    id          BIGSERIAL PRIMARY KEY,
    url_id      BIGINT NOT NULL REFERENCES urls(id) ON DELETE CASCADE,
    accessed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    device_type VARCHAR(20),
    referrer    VARCHAR(500)
);

CREATE INDEX idx_access_logs_url_id ON url_access_logs(url_id);
CREATE INDEX idx_access_logs_accessed_at ON url_access_logs(accessed_at);