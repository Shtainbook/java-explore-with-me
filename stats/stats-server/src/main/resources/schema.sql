DROP TABLE IF EXISTS hits CASCADE;

CREATE TABLE IF NOT EXISTS hits (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(512)                           NOT NULL,
    uri VARCHAR(512)                           NOT NULL,
    ip VARCHAR(512)                            NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE   NOT NULL,
    PRIMARY KEY (id)
);