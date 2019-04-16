drop table if exists oauth_client_details;
create table oauth_client_details
(
  client_id               VARCHAR(256) PRIMARY KEY,
  resource_ids            VARCHAR(256),
  client_secret           VARCHAR(256),
  scope                   VARCHAR(256),
  authorized_grant_types  VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities             VARCHAR(256),
  access_token_validity   INTEGER,
  refresh_token_validity  INTEGER,
  additional_information  VARCHAR(4096),
  autoapprove             VARCHAR(256)
);

drop table if exists oauth_client_token;
create table oauth_client_token
(
  token_id          VARCHAR(256),
  token             LONGVARBINARY,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name         VARCHAR(256),
  client_id         VARCHAR(256),
  constraint fk_oct_ocd foreign key (client_id) references oauth_client_details (client_id)
);

drop table if exists oauth_access_token;
create table oauth_access_token
(
  token_id          VARCHAR(256),
  token             LONGVARBINARY,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name         VARCHAR(256),
  client_id         VARCHAR(256),
  authentication    LONGVARBINARY,
  refresh_token     VARCHAR(256),
  constraint fk_oat_ocd foreign key (client_id) references oauth_client_details (client_id)
);

drop table if exists oauth_refresh_token;
create table oauth_refresh_token
(
  token_id       VARCHAR(256),
  token          LONGVARBINARY,
  authentication LONGVARBINARY
);

drop table if exists oauth_code;
create table oauth_code
(
  code           VARCHAR(256),
  authentication LONGVARBINARY
);

drop table if exists oauth_approvals;
create table oauth_approvals
(
  userId         VARCHAR(256),
  clientId       VARCHAR(256),
  scope          VARCHAR(256),
  status         VARCHAR(10),
  expiresAt      TIMESTAMP,
  lastModifiedAt TIMESTAMP
);

drop table if exists users;
create table users
(
  username VARCHAR(50) PRIMARY KEY,
  password VARCHAR(256) NOT NULL,
  enabled  BOOLEAN      NOT NULL DEFAULT true
);

drop table if exists authorities;
create table authorities
(
  username  VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  constraint fk_authorities_users foreign key (username) references users (username)
);

drop index if exists ux_username_authority;
create unique index ux_username_authority on authorities (username, authority);