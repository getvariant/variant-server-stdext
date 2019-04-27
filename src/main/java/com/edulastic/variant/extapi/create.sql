CREATE TABLE variant_durable_targeting ( 
  id               VARCHAR(128) NOT NULL,
  variation        VARCHAR(256) NOT NULL,
  experience       VARCHAR(256) NOT NULL,
  created_on       TIMESTAMP    NOT NULL,
  PRIMARY KEY (id, variation)
 );