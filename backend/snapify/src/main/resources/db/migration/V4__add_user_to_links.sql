ALTER TABLE links
    ADD COLUMN user_id BIGINT;

ALTER TABLE links
    ADD CONSTRAINT fk_links_user
        FOREIGN KEY (user_id)
        REFERENCES users(id);