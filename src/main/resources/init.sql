CREATE TABLE vk_group (
    id VARCHAR(255) PRIMARY KEY
);

CREATE TABLE posts (
    id INT NOT NULL,
    group_id VARCHAR(255) NOT NULL,
    text TEXT,
    date TIMESTAMP,
    PRIMARY KEY (id, group_id),
    CONSTRAINT fk_posts_group FOREIGN KEY (group_id) REFERENCES vk_group(id)
);

CREATE TABLE photo (
    id INT NOT NULL,
    album_id INT NOT NULL,
    owner_id INT NOT NULL,
    vk_url VARCHAR,
    minio_url VARCHAR,
    height INT,
    width INT,
    PRIMARY KEY (id, album_id, owner_id)
);

CREATE TABLE post_photos (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    post_id INT NOT NULL,
    photo_id INT NOT NULL,
    album_id INT NOT NULL,
    owner_id INT NOT NULL,
    CONSTRAINT fk_post_photos_group FOREIGN KEY (group_id) REFERENCES vk_group(id),
    CONSTRAINT fk_post_photos_post FOREIGN KEY (post_id, group_id) REFERENCES posts(id, group_id),
    CONSTRAINT fk_post_photos_photo FOREIGN KEY (photo_id, album_id, owner_id) REFERENCES photo(id, album_id, owner_id),
    CONSTRAINT uq_post_photos UNIQUE (id, group_id, post_id, photo_id)
);

