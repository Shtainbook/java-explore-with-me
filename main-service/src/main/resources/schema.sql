DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS comment_likes CASCADE;
DROP TABLE IF EXISTS comment_dislikes CASCADE;

CREATE TABLE IF NOT EXISTS categories
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    50
) UNIQUE NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    email
    VARCHAR
(
    254
) UNIQUE NOT NULL,
    name varchar
(
    250
) NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS locations
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    lat
    REAL
    NOT
    NULL,
    lon
    REAL
    NOT
    NULL,
    PRIMARY
    KEY
(
    id
),
    UNIQUE
(
    lat,
    lon
)
    );

create table IF NOT EXISTS events
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    annotation
    VARCHAR
(
    2000
) NOT NULL,
    category_id BIGINT REFERENCES categories
(
    id
) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR
(
    7000
) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT REFERENCES users
(
    ID
) NOT NULL,
    location_id BIGINT REFERENCES locations
(
    ID
) NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR
(
    255
) NOT NULL,
    title VARCHAR
(
    120
) NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    created
    TIMESTAMP
    NOT
    NULL,
    event_id
    BIGINT
    REFERENCES
    events
(
    id
),
    requester_id BIGINT REFERENCES users
(
    id
),
    status VARCHAR
(
    255
) NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS compilations
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    pinned
    BOOLEAN
    NOT
    NULL,
    title
    VARCHAR
(
    50
) UNIQUE NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS compilations_events
(
    event_id BIGINT REFERENCES events
(
    id
),
    compilation_id BIGINT REFERENCES compilations
(
    id
),
    PRIMARY KEY
(
    event_id,
    compilation_id
)
    );

CREATE TABLE IF NOT EXISTS comments
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    author_id
    BIGINT
    REFERENCES
    users
(
    id
),
    event_id BIGINT REFERENCES events
(
    id
),
    comment_text VARCHAR NOT NULL,
    created_on TIMESTAMP NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS comment_likes
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    comment_id
    BIGINT
    REFERENCES
    comments
(
    id
),
    user_id BIGINT REFERENCES users
(
    id
),
    created_on TIMESTAMP NOT NULL,
    PRIMARY KEY
(
    id
)
    );

CREATE TABLE IF NOT EXISTS comment_dislikes
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    comment_id
    BIGINT
    REFERENCES
    comments
(
    id
),
    user_id BIGINT REFERENCES users
(
    id
),
    created_on TIMESTAMP NOT NULL,
    PRIMARY KEY
(
    id
)
    );