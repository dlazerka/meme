-- // Create image, meme, caption tables.

CREATE TABLE image (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  blobkey VARCHAR(255) NOT NULL,
  filename VARCHAR(255) NOT NULL,
  size INTEGER NOT NULL,
  width INTEGER,
  height INTEGER
);

CREATE TABLE meme (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,

  created_by VARCHAR(256) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  image_id BIGINT NOT NULL,

  FOREIGN KEY(image_id) REFERENCES image(id)
    ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE caption (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,

  meme_id BIGINT NOT NULL,

  `text` VARCHAR(255), -- See Caption.TEXT_MAX_LENGTH
  top_px INTEGER,

  FOREIGN KEY(meme_id) REFERENCES meme(id)
    ON UPDATE CASCADE ON DELETE CASCADE
);



-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE caption;
DROP TABLE meme;
DROP TABLE image;
