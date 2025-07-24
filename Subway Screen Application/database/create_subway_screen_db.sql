CREATE DATABASE IF NOT EXISTS subway_screen;
USE subway_screen;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS advertisements;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS advertisements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    media_type ENUM('GIF', 'JPEG', 'BMP') NOT NULL,
    media_path VARCHAR(255) NOT NULL,
    display_duration INT
);

INSERT INTO advertisements (id, media_type, media_path, display_duration) VALUES
(1, 'GIF', 'ad1.gif', 10),
(2, 'GIF', 'ad2.gif', 10),
(3, 'GIF', 'ad3.gif', 10),
(4, 'GIF', 'ad4.gif', 10),
(5, 'GIF', 'ad5.gif', 10);

SELECT * FROM advertisements;