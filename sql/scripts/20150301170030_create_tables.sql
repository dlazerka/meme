-- // create tables image, meme, caption
-- Migration SQL that makes the change goes here.

CREATE TABLE image (
  id INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT 'Don''t rely on order',
  blobkey VARCHAR(767) NOT NULL UNIQUE,
  filename VARCHAR(255) NOT NULL,
  size INTEGER NOT NULL,
  width INTEGER,
  height INTEGER
);

CREATE TABLE meme (
  id INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT 'Don''t rely on order',

  created_by VARCHAR(767) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  image_id INTEGER NOT NULL,

  FOREIGN KEY(image_id) REFERENCES image(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX(created_by)
);

CREATE TABLE caption (
  id INTEGER PRIMARY KEY AUTO_INCREMENT COMMENT 'Don''t rely on order',

  meme_id INTEGER NOT NULL,

  `text` VARCHAR(255) COMMENT 'Don''t rely on order', -- See Caption.TEXT_MAX_LENGTH
  top_px INTEGER,

  FOREIGN KEY(meme_id) REFERENCES meme(id)
    ON UPDATE CASCADE ON DELETE CASCADE
);

-- //@UNDO
-- SQL to undo the change goes here.

DROP TABLE caption;
DROP TABLE meme;
DROP TABLE image;
