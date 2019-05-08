CREATE TABLE events ( 
  id                    CHAR(32)     NOT NULL,
  session_id            CHAR(32)     NOT NULL,
  created_on            TIMESTAMP    NOT NULL,
  event_name            VARCHAR(256)  NOT NULL,
  CONSTRAINT events_pk PRIMARY KEY (id)
 );

 
CREATE TABLE event_attributes ( 
  event_id              CHAR(32) REFERENCES events (id) ON DELETE CASCADE,
  name                  VARCHAR(256) NOT NULL,
  value                 VARCHAR(256) NOT NULL
 );


CREATE TABLE event_experiences (
  event_id              CHAR(32) REFERENCES events(id) ON DELETE CASCADE,
  variation_name        VARCHAR(256) NOT NULL,
  experience_name       VARCHAR(256) NOT NULL,
  is_control            BOOLEAN NOT NULL,
  CONSTRAINT event_experiences_pk PRIMARY KEY (event_id, variation_name, experience_name)
 );


CREATE VIEW events_v AS
  SELECT e.id event_id, e.session_id, e.created_on, e.event_name, ev.variation_name, ev.experience_name, ev.is_control,
         (SELECT group_concat('''' || name || '''=''' || value || '''', ',') FROM event_attributes where event_id = e.id) event_attributes
  FROM events e left outer join event_experiences ev ON e.id = ev.event_id
  ORDER BY event_id;
