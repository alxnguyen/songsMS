CREATE TABLE `songs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `artist` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `released` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `songlists` (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_private` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


