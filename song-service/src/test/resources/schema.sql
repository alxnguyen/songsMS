create table songs(
  id int not null AUTO_INCREMENT,
  title varchar(45) not null,
  artist varchar(45) not null,
  label varchar(45) not null,
  released int not null,
  PRIMARY KEY ( id )
);