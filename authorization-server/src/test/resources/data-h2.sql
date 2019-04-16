-- 'password'
INSERT INTO oauth_client_details
(client_id, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES ('clientx',
        '$2a$10$TYaGY7tqqM9kwdBJg9GOresSf/orSMGU5dy.Dmu75ARASx7L0LovG', 'read,write',
        'password,authorization_code,refresh_token,client_credentials,implicit', 'https://localhost:8080/oauth',
        'read,write', 28800, 86400, null, false);


-- 'password'
INSERT INTO oauth_client_details
(client_id, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES ('clienty',
        '$2a$10$TYaGY7tqqM9kwdBJg9GOresSf/orSMGU5dy.Dmu75ARASx7L0LovG', 'read,write',
        'password,authorization_code,refresh_token,client_credentials,implicit', 'https://localhost:8080/oauth',
        'read,write', 28800, 86400, null, false);


-- empty password
INSERT INTO oauth_client_details
(client_id, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES ('clientz',
        '$2a$10$OLDOz3q7Cs.cEPH6rUK0u.SkjYbJSB8zgXAEyGiGNKxG1ZKXW5Bgi', 'payments,accounts',
        'authorization_code,refresh_token', 'https://localhost:8080/code',
        'read,write', 36000, 36000, null, true);

INSERT INTO users(`username`, `password`, `enabled`)
VALUES ('userx', '$2a$10$TYaGY7tqqM9kwdBJg9GOresSf/orSMGU5dy.Dmu75ARASx7L0LovG', true);
INSERT INTO users(`username`, `password`, `enabled`)
VALUES ('usery', '$2a$10$TYaGY7tqqM9kwdBJg9GOresSf/orSMGU5dy.Dmu75ARASx7L0LovG', true);

INSERT INTO authorities (`username`, `authority`)
VALUES ('userx', 'ROLE_ADMIN');
INSERT INTO authorities (`username`, `authority`)
VALUES ('userx', 'ROLE_USER');
INSERT INTO authorities (`username`, `authority`)
VALUES ('usery', 'ROLE_ADMIN');
