CREATE DATABASE IF NOT EXISTS `jcp_all`;
CREATE USER 'jcp_all'@'%' IDENTIFIED BY 'all_jcp';
GRANT ALL PRIVILEGES ON jcp_all.* TO 'jcp_all'@'%';
