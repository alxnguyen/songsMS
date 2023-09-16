CREATE TABLE IF NOT EXISTS `songs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `artist` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `released` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `songlists` (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_private` bit(1) NOT NULL,
  `song_list_name` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

insert into songs (id, released, artist, label, title) values(1, 1968, 'Richard Harris', 'Dunhill Records', 'MacArthur Park');
insert into songs (id, released, artist, label, title) values(2, 1976, 'Starland Vocal Band', 'Windsong', 'Afternoon Delight');
insert into songs (id, released, artist, label, title) values(3, 1976, 'Captain and Tennille', 'A&M', 'Muskrat Love');
insert into songs (id, released, artist, label, title) values(4, 1985, 'Phil Collins', 'Virgin', 'Sussudio');
insert into songs (id, released, artist, label, title) values(5, 1985, 'Starship', 'Grunt/RCA', 'We Built This City');
insert into songs (id, released, artist, label, title) values(6, 1992, 'Billy Ray Cyrus', 'PolyGram Mercury', 'Achy Breaky Heart');
insert into songs (id, released, artist, label, title) values(7, 1993, '4 Non Blondes', 'Interscope', 'Whats Up?');
insert into songs (id, released, artist, label, title) values(8, 2000, 'Baha Men', 'S-Curve', 'Who Let the Dogs Out?');
insert into songs (id, released, artist, label, title) values(9, 2005, 'Black Eyed Peas', 'Universal Music', 'My Humps');
insert into songs (id, released, artist, label, title) values(10, 2013, 'Alison Gold', 'PMW Live', 'Chinese Food');

insert into songlists values(1, true, 'Maximes Public Playlist', 'maxime');
insert into songlists_songs values(1, 1);
insert into songlists_songs values(2, 1);
insert into songlists_songs values(3, 1);

insert into songlists values(2, false, 'Janes Public Playlist', 'jane');
insert into songlists_songs values(1, 2);
insert into songlists_songs values(4, 2);
insert into songlists_songs values(7, 2);