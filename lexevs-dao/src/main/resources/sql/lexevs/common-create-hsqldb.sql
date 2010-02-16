CREATE TABLE @PREFIX@registry ( 
	registryGuid varchar(36) NOT NULL,
	resourceURI varchar(250),
	resourceVersion varchar(50),
	resourceType varchar(50),
	dbURI varchar(250),
	dbName varchar(50),
	prefix varchar(20),
	status varchar(50),
	tag varchar(50),
	lastUpdateDate timestamp,
	activationDate timestamp,
	deactivationDate timestamp,
	baseRevision varchar(50),
	fixedAtRevision varchar(50),
	isLocked char(1),
	dbSchemaVersion varchar(50),
	dbSchemaDescription varchar(255)
)
;

CREATE TABLE @PREFIX@registryMetaData ( 
	id char(1),
	lastUpdateTime timestamp,
	lastUsedDBIdentifer varchar(4),
	lastUsedHistoryIdentifer varchar(4)
)
;


CREATE TABLE @PREFIX@revision ( 
	revisionGuid varchar(36) NOT NULL,
	releaseGuid varchar(36),
	revisionId varchar(50) NOT NULL,
	changeAgent varchar(50),
	revisionDate timestamp,
	revAppliedDate timestamp NOT NULL,
	editOrder decimal(18),
	changeInstructions varchar,
	description varchar
)
;

CREATE TABLE @PREFIX@systemRelease ( 
	releaseGuid varchar(36) NOT NULL,
	releaseURI varchar(250) NOT NULL,
	releaseId varchar(50),
	releaseDate timestamp NOT NULL,
	basedOnRelease varchar(250),
	releaseAgency varchar(250),
	description varchar
)
;

ALTER TABLE @PREFIX@registryMetadata ADD CONSTRAINT PK_registryMetadata 
	PRIMARY KEY (id)
;

INSERT INTO @PREFIX@registryMetadata VALUES ('0', CURTIME(), 'aaa', 'aaa');

ALTER TABLE @PREFIX@registry ADD CONSTRAINT PK_registry 
	PRIMARY KEY (registryGuid)
;



ALTER TABLE @PREFIX@registry
	ADD CONSTRAINT UQ_registry UNIQUE (resourceURI, resourceVersion, resourceType)
;
