CREATE TABLE IF NOT EXISTS `oauth_client_details`
(
  `client_id`               varchar(256) NOT NULL,
  `resource_ids`            varchar(256)  DEFAULT NULL,
  `client_secret`           varchar(256)  DEFAULT NULL,
  `scope`                   varchar(256)  DEFAULT NULL,
  `authorized_grant_types`  varchar(256)  DEFAULT NULL,
  `web_server_redirect_uri` varchar(256)  DEFAULT NULL,
  `authorities`             varchar(256)  DEFAULT NULL,
  `access_token_validity`   int(11)       DEFAULT NULL,
  `refresh_token_validity`  int(11)       DEFAULT NULL,
  `additional_information`  varchar(4096) DEFAULT NULL,
  `autoapprove`             varchar(256)  DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `oauth_client_token`
(
  `token_id`          varchar(256) DEFAULT NULL,
  `token`             MEDIUMBLOB,
  `authentication_id` varchar(256) NOT NULL,
  `user_name`         varchar(256) DEFAULT NULL,
  `client_id`         varchar(256) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`),
  KEY                 `fk_oct_ocd` (`client_id`),
  CONSTRAINT `fk_oct_ocd` FOREIGN KEY (`client_id`) REFERENCES `oauth_client_details` (`client_id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `oauth_access_token`
(
  `token_id`          varchar(256) DEFAULT NULL,
  `token`             MEDIUMBLOB,
  `authentication_id` varchar(256) NOT NULL,
  `user_name`         varchar(256) DEFAULT NULL,
  `client_id`         varchar(256) DEFAULT NULL,
  `authentication`    MEDIUMBLOB,
  `refresh_token`     varchar(256) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `oauth_approvals`
(
  `userId`         varchar(256)   DEFAULT NULL,
  `clientId`       varchar(256)   DEFAULT NULL,
  `scope`          varchar(256)   DEFAULT NULL,
  `status`         varchar(10)    DEFAULT NULL,
  `expiresAt`      timestamp NULL DEFAULT NULL,
  `lastModifiedAt` timestamp NULL DEFAULT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `oauth_code`
(
  `code`           varchar(256) DEFAULT NULL,
  `authentication` MEDIUMBLOB
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `oauth_refresh_token`
(
  `token_id`       varchar(256) DEFAULT NULL,
  `token`          MEDIUMBLOB,
  `authentication` MEDIUMBLOB
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `users`
(
  `username` varchar(50)  NOT NULL,
  `password` varchar(256) NOT NULL,
  `enabled`  tinyint(1)   NOT NULL DEFAULT '1',
  PRIMARY KEY (`username`)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `authorities`
(
  `username`  varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `ux_username_authority` (`username`, `authority`),
  CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE
) ENGINE = InnoDB;
