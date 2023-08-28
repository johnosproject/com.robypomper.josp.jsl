CREATE DATABASE IF NOT EXISTS `jcp_fe`;
CREATE USER 'jcp_fe'@'%' IDENTIFIED BY 'fe_jcp';
GRANT ALL PRIVILEGES ON jcp_fe.* TO 'jcp_fe'@'%';
