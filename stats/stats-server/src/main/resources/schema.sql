DROP TABLE IF EXISTS hits CASCADE;

CREATE TABLE IF NOT EXISTS hits
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    app
    VARCHAR
(
    512
) NOT NULL,
    uri VARCHAR
(
    512
) NOT NULL,
    ip VARCHAR
(
    512
) NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_hit PRIMARY KEY
(
    id
)
    );

CREATE INDEX IF NOT EXISTS idx_hit_app ON hits (app);
CREATE INDEX IF NOT EXISTS idx_hit_uri ON hits (uri);
CREATE INDEX IF NOT EXISTS idx_hit_ip ON hits (ip);
CREATE INDEX IF NOT EXISTS idx_hit_created_date ON hits (created_date);