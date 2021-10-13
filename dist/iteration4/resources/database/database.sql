CREATE TABLE IF NOT EXISTS "User" (
	"Id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"Username"	TEXT NOT NULL UNIQUE,
	"Email"	TEXT NOT NULL UNIQUE,
	"LastName"	TEXT NOT NULL,
	"FirstName"	TEXT NOT NULL,
	"Password"	TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS "Project" (
	"Id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"Title"	TEXT NOT NULL,
	"Description"	TEXT NOT NULL,
	"StartDate"	INTEGER NOT NULL,
	"EndDate"	INTEGER NOT NULL,
	"InitialDuration"	INTEGER NOT NULL,
	"Author"	TEXT NOT NULL,
	"ParentProject"	INTEGER,
    "Color" INTEGER NOT NULL,
	FOREIGN KEY("ParentProject") REFERENCES "Project"("Id") on delete cascade,
	FOREIGN KEY("Author") REFERENCES "User"("username") on update cascade
);
CREATE TABLE IF NOT EXISTS "Tag" (
	"Id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"Description"	TEXT NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS  "ProjectTag" (
	"ProjectId"	INTEGER NOT NULL,
	"TagId"	INTEGER NOT NULL,
	FOREIGN KEY("ProjectId") REFERENCES "Project"("Id") on delete cascade,
	FOREIGN KEY("TagId") REFERENCES "Tag"("Id") on delete cascade,
	PRIMARY KEY("ProjectId","TagId")
);
CREATE TABLE IF NOT EXISTS "Task" (
	"Id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"Description"	TEXT NOT NULL,
	"StartDate"	INTEGER NOT NULL,
	"EndDate"	INTEGER NOT NULL,
	"ProjectId"	INTEGER NOT NULL,
	FOREIGN KEY("ProjectId") REFERENCES "Project"("Id") ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "ProjectCollaboration" (
	"ProjectId"	INTEGER NOT NULL,
	"UserId"	INTEGER NOT NULL,
	"Accepted"	INTEGER,
	"InvitationRead"	INTEGER,
	FOREIGN KEY("UserId") REFERENCES "User"("Id") ON DELETE CASCADE,
	FOREIGN KEY("ProjectId") REFERENCES "Project"("Id") ON DELETE CASCADE,
	PRIMARY KEY("ProjectId","UserId")
);
create table IF NOT EXISTS "CollaboratorTask" (
	"UserId"	int NOT NULL,
	"TaskId"	int NOT NULL,
	FOREIGN KEY("UserId") REFERENCES "User"("Id") ON DELETE CASCADE,
	PRIMARY KEY("UserId","TaskId"),
	FOREIGN KEY("TaskId") REFERENCES "Task"("Id") ON DELETE CASCADE
);