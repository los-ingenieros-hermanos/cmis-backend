CREATE DATABASE  IF NOT EXISTS `community_directory`;
USE `community_directory`;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `community`;

CREATE TABLE `community` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `info` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;