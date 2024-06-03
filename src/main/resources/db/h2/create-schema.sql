CREATE TABLE events ( 
  id                    CHAR(32)     NOT NULL,     -- Event ID
  session_id            CHAR(32)     NOT NULL,     -- Variant session ID
  created_on            TIMESTAMP    NOT NULL,     -- Event creation timestamp.
  event_name            VARCHAR(64)  NOT NULL,     -- Event name
  CONSTRAINT events_pk PRIMARY KEY (id)
 );
 
CREATE TABLE event_attributes ( 
  event_id              CHAR(32) REFERENCES events(id) ON DELETE CASCADE,
  name                  VARCHAR(64) NOT NULL, 
  value                 VARCHAR(512) NOT NULL
 );

CREATE TABLE event_experiences ( 
  event_id              CHAR(32) REFERENCES events(id) ON DELETE CASCADE,
  variation_name        VARCHAR(512) NOT NULL, 
  experience_name       VARCHAR(512) NOT NULL, 
  is_control            BOOLEAN NOT NULL,     
  CONSTRAINT event_experiences_pk PRIMARY KEY (event_id, variation_name, experience_name)
 );

CREATE VIEW events_v AS
  SELECT e.*, ev.variation_name, ev.experience_name, ev.is_control,
         (SELECT string_agg('''' || name || '''=''' || value || '''', ',') FROM event_attributes where event_id = e.id) event_attributes
  FROM events e left outer join event_experiences ev ON e.id = ev.event_id
  ORDER BY event_id
;
