DROP SCHEMA IF EXISTS `cmis-db`;

CREATE SCHEMA `cmis-db`;

use `cmis-db`;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)	

)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;




