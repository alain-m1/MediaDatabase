CREATE SCHEMA MediaDatabase;
USE MediaDatabase;

CREATE TABLE DIRECTOR(
	dID INT PRIMARY KEY,
	name VARCHAR(30)
);

CREATE TABLE MEDIA(
	Title VARCHAR(30),
	Year INT,
	dID INT,
	Description VARCHAR(500),
PRIMARY KEY(Title, Year),
FOREIGN KEY(dID)
	REFERENCES DIRECTOR(dID)
	ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE GENRE(
	Title VARCHAR(30),
	Year INT,
	Genre VARCHAR(30),
	PRIMARY KEY(Title, Year, Genre),
	FOREIGN KEY(Title, Year)
		REFERENCES MEDIA(Title, Year)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE MOVIE(
	Title VARCHAR(30),
	Year INT,
	MPA_Rating VARCHAR(5),
	PRIMARY KEY (Title, Year),
	Foreign Key(Title, Year)
REFERENCES MEDIA(Title, Year)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `SHOW`(
	Title VARCHAR(30),
	Year INT,
	PRIMARY KEY(Title, Year),
	FOREIGN KEY(Title, Year)
		REFERENCES MEDIA(Title, Year)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE SEASONS(
Title VARCHAR(30),
	Year INT,
	numberOfEpisodes INT,
	seasonNumber INT,
PRIMARY KEY(Title, Year, seasonNumber),
FOREIGN KEY(Title, Year)
	REFERENCES `SHOW`(Title, Year)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE USERS(
Username VARCHAR(30) PRIMARY KEY,
Name VARCHAR(30),
Password VARCHAR(30)
);

CREATE TABLE ACTOR(
	aID INT PRIMARY KEY,
	name VARCHAR(30)
);

CREATE TABLE ADMIN(
	Username VARCHAR(30) PRIMARY KEY,
	Password VARCHAR(30)
);

CREATE TABLE USER_PLAYLIST(
	Username VARCHAR(30),
	Title VARCHAR(30),
	Year INT,
	PlaylistName VARCHAR(30),
	PRIMARY KEY(Username, Title, Year, PlaylistName),
	FOREIGN KEY(Title, Year)
		REFERENCES MEDIA(Title, Year)
	ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY(Username)
REFERENCES USERS(Username)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE MANAGES(
Title VARCHAR(30),
Year INT,
Username VARCHAR(30),
PRIMARY KEY(Title, Year, username),
FOREIGN KEY(Title, Year)
	REFERENCES MEDIA(Title, Year)
	ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (Username)
	REFERENCES ADMIN(Username)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE APPEARS_IN(
	aID INT,
	Title VARCHAR(30),
	Year INT,
	PRIMARY KEY(aID, Title, Year),
	FOREIGN KEY(Title, Year)
		REFERENCES MEDIA(Title, Year)
	ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY(aID)
		REFERENCES ACTOR(aID)
	ON DELETE CASCADE ON UPDATE CASCADE
);


-- DIRECTOR
INSERT INTO DIRECTOR (dID, name) VALUES
(1, 'Christopher Nolan'),
(2, 'Greta Gerwig'),
(3, 'David Fincher'),
(4, 'Vince Gilligan'),
(5, 'Shonda Rhimes');

-- MEDIA
INSERT INTO MEDIA (Title, Year, dID, Description) VALUES
('Inception', 2010, 1, 'A thief who steals corporate secrets through dream-sharing technology.'),
('Oppenheimer', 2023, 1, 'The story of J. Robert Oppenheimer and the creation of the atomic bomb.'),
('Barbie', 2023, 2, 'Barbie embarks on a journey of self-discovery in the real world.'),
('Breaking Bad', 2008, 4, 'A chemistry teacher turns to making meth to secure his family’s future.'),
('Grey''s Anatomy', 2005, 5, 'Doctors navigate love and medicine at a Seattle hospital.');

-- GENRE
INSERT INTO GENRE (Title, Year, Genre) VALUES
('Inception', 2010, 'Sci-Fi'),
('Inception', 2010, 'Thriller'),
('Oppenheimer', 2023, 'Drama'),
('Barbie', 2023, 'Comedy'),
('Breaking Bad', 2008, 'Crime'),
('Breaking Bad', 2008, 'Drama'),
('Grey''s Anatomy', 2005, 'Drama');

-- MOVIE
INSERT INTO MOVIE (Title, Year, MPA_Rating) VALUES
('Inception', 2010, 'PG-13'),
('Oppenheimer', 2023, 'R'),
('Barbie', 2023, 'PG-13');

-- SHOW
INSERT INTO `SHOW` (Title, Year) VALUES
('Breaking Bad', 2008),
('Grey''s Anatomy', 2005);

-- SEASONS
INSERT INTO SEASONS (Title, Year, numberOfEpisodes, seasonNumber) VALUES
('Breaking Bad', 2008, 7, 1),
('Breaking Bad', 2008, 13, 2),
('Breaking Bad', 2008, 16, 3),
('Grey''s Anatomy', 2005, 9, 1),
('Grey''s Anatomy', 2005, 24, 2);

-- USERS
INSERT INTO USERS (Username, Name, Password) VALUES
('alex123', 'Alex Johnson', 'pass123'),
('mariaG', 'Maria Gomez', 'password'),
('jakeP', 'Jake Parker', 'qwerty!'),
('saraS', 'Sara Smith', 'Smith123'),
('leoK', 'Leo Kim', 'SecurePassword');

-- ACTOR
INSERT INTO ACTOR (aID, name) VALUES
(1, 'Leonardo DiCaprio'),
(2, 'Cillian Murphy'),
(3, 'Margot Robbie'),
(4, 'Bryan Cranston'),
(5, 'Ellen Pompeo');

-- ADMIN
INSERT INTO ADMIN (Username, Password) VALUES
('admin1', 'rootpass'),
('admin2', 'secret'),
('admin3', 'superuser'),
('admin4', 'admin123'),
('admin5', 'pass123');

-- USER_PLAYLIST 
INSERT INTO USER_PLAYLIST (Username, Title, Year, PlaylistName) VALUES
('alex123', 'Inception', 2010, 'Favorites'),
('alex123', 'Oppenheimer', 2023, 'Favorites'),
('mariaG', 'Barbie', 2023, 'Feel Good'),
('jakeP', 'Breaking Bad', 2008, 'Binge Watch'),
('jakeP', 'Barbie', 2023, 'Favorites'),
('saraS', 'Oppenheimer', 2023, 'Top Picks'),
('leoK', 'Grey''s Anatomy', 2005, 'Drama Zone');

-- MANAGES
INSERT INTO MANAGES (Title, Year, Username) VALUES
('Inception', 2010, 'admin1'),
('Barbie', 2023, 'admin2'),
('Oppenheimer', 2023, 'admin3'),
('Breaking Bad', 2008, 'admin4'),
('Grey''s Anatomy', 2005, 'admin5');

-- APPEARS_IN
INSERT INTO APPEARS_IN (aID, Title, Year) VALUES
(1, 'Inception', 2010),
(2, 'Oppenheimer', 2023),
(3, 'Barbie', 2023),
(4, 'Breaking Bad', 2008),
(5, 'Grey''s Anatomy', 2005);
