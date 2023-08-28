CREATE DATABASE IF NOT EXISTS `jcp_auth`;
CREATE USER 'jcp_auth'@'%' IDENTIFIED BY 'auth_jcp';
GRANT ALL PRIVILEGES ON jcp_auth.* TO 'jcp_auth'@'%';
