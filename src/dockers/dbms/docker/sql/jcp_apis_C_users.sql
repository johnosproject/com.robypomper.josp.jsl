CREATE DATABASE IF NOT EXISTS `jcp_apis`;
CREATE USER 'jcp_apis'@'%' IDENTIFIED BY 'apis_jcp';
GRANT ALL PRIVILEGES ON jcp_apis.* TO 'jcp_apis'@'%';
