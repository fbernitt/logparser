
CREATE SEQUENCE hosts_id_seq;
CREATE TABLE hosts (
  id BIGINT NOT NULL DEFAULT NEXT VALUE FOR hosts_id_seq,
  ip VARCHAR(15) NOT NULL,
  hostname VARCHAR(30) NOT NULL,
  fqdn VARCHAR(100) NOT NULL,

  PRIMARY KEY (id)
);

CREATE SEQUENCE logfiles_id_seq;
CREATE TABLE logfiles (
  id BIGINT NOT NULL DEFAULT NEXT VALUE FOR logfiles_id_seq,
  host_id INTEGER NOT NULL,
  file_name TEXT NOT NULL,

  UNIQUE (host_id, file_name),
  FOREIGN KEY (host_id) REFERENCES hosts(id),
  PRIMARY KEY (id)
);

CREATE TABLE apache_log_events (
  ip VARCHAR(15) NOT NULL,
  log_time TIMESTAMP NOT NULL,
  method VARCHAR(10) NOT NULL,
  status_code INTEGER NOT NULL,
  uri TEXT NOT NULL,
  referer TEXT NOT NULL,
  browser TEXT NOT NULL,
  logfile_id INTEGER NOT NULL,

  FOREIGN KEY (logfile_id) REFERENCES logfiles(id)
);
CREATE INDEX apache_log_events_uri_idx ON apache_log_events (uri);
CREATE INDEX apache_log_events_log_time_idx ON apache_log_events (log_time);

CREATE SEQUENCE tomcat_log_events_id_seq;
CREATE TABLE tomcat_log_events (
  id BIGINT NOT NULL DEFAULT NEXT VALUE FOR tomcat_log_events_id_seq,
  logfile_id INTEGER NOT NULL,
  log_time TIMESTAMP NOT NULL,
  level VARCHAR(10) NOT NULL,
  message TEXT NOT NULL,
  raw_msg TEXT NOT NULL,
  is_exception BOOLEAN NOT NULL,
  exception_class TEXT,
  our_first_line TEXT,

  FOREIGN KEY (logfile_id) REFERENCES logfiles(id),
  PRIMARY KEY (id)
);
