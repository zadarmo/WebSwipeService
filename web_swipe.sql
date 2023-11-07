-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: web_swipe
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `category_info`
--

DROP TABLE IF EXISTS `category_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_key` varchar(45) NOT NULL,
  `text` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_key_UNIQUE` (`category_key`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_info`
--

LOCK TABLES `category_info` WRITE;
/*!40000 ALTER TABLE `category_info` DISABLE KEYS */;
INSERT INTO `category_info` VALUES (1,'popular','热门'),(2,'food','美食'),(3,'sport','体育'),(4,'movie','影视'),(5,'music','音乐'),(6,'game','游戏'),(7,'dance','舞蹈'),(8,'life','生活'),(9,'knowledge','知识'),(10,'technology','科技');
/*!40000 ALTER TABLE `category_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(300) NOT NULL,
  `avatar_key` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (5,'wx','$2a$10$9jnbQst3jJ7uu4.vE9ylJeR2QYNvKtz77viOkL4J4yvl1rBf15BP2','Fsx4IwdOoozOQo2P6iSBpErBSC3o'),(6,'ys','$2a$10$nNWI/QJNgdiDY.KeFELD6OesAvzE3UJIeV7X.L5qYEHs30q1Pxea2','FgiM-RXt71eIMVgp8V7fGpNU-003'),(8,'ok','$2a$10$epGskHi3b0HMIGqrvJcvCeJvJtbkHTCLeDUml3xE3sUkC8kvrkR.u','FhdPyn9dLH3LfAk4r25gk-J1dKpY'),(9,'abc','$2a$10$cuJMEziBDp0uYZ4JXa3upuP1xk1sdK7I5BldzTSX0st/FQ6hotKAC','FrYRo0kMlLJ-d4FctI68GCD4e0Yj'),(10,'aaa','$2a$10$cbGBrgltwzXK610GWvMAM.9UdY0TFC1dj/BTg3sO3AEwyEbc6OSAO','Fkd5pNaEFD_Auxni2zNPdikLpixU'),(11,'bbb','$2a$10$dBN7fedz6lkjaHh2lNR6q.Uvlcr9H.GCir1h7me9QqShNQMcuUoBm','FqDUgaB6sdiHFBIQ8itIQ3TJ_XIg');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_interaction`
--

DROP TABLE IF EXISTS `user_interaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_interaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `video_id` int NOT NULL,
  `interaction_type` enum('thumb_up','collect') NOT NULL,
  `operate_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `video_id_idx` (`video_id`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`),
  CONSTRAINT `video_id` FOREIGN KEY (`video_id`) REFERENCES `video_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_interaction`
--

LOCK TABLES `user_interaction` WRITE;
/*!40000 ALTER TABLE `user_interaction` DISABLE KEYS */;
INSERT INTO `user_interaction` VALUES (6,5,45,'thumb_up','2023-11-07 21:33:54'),(7,5,45,'collect','2023-11-07 21:33:55'),(8,10,45,'thumb_up','2023-11-07 22:05:36'),(9,10,45,'collect','2023-11-07 22:05:37'),(10,11,48,'thumb_up','2023-11-07 22:24:01'),(11,11,45,'collect','2023-11-07 22:24:05');
/*!40000 ALTER TABLE `user_interaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_info`
--

DROP TABLE IF EXISTS `video_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `video_key` varchar(200) NOT NULL,
  `cover_key` varchar(200) NOT NULL,
  `uploader_id` int NOT NULL,
  `create_at` datetime NOT NULL,
  `duration` double NOT NULL,
  `categories` varchar(200) NOT NULL,
  `description` varchar(200) DEFAULT '无',
  PRIMARY KEY (`id`),
  KEY `uploader_id_idx` (`uploader_id`),
  CONSTRAINT `uploader_id` FOREIGN KEY (`uploader_id`) REFERENCES `user_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_info`
--

LOCK TABLES `video_info` WRITE;
/*!40000 ALTER TABLE `video_info` DISABLE KEYS */;
INSERT INTO `video_info` VALUES (45,'FiFFwiSbe6S0XGkGxAiCvnCJweiR','1699363755767',5,'2023-11-07 21:29:13',1,'sport','你围的住我梅西吗？'),(46,'lvAgC2QSW3pNmOm_x9RVKdSbndrr','1699363807015',5,'2023-11-07 21:29:59',1,'game','你看这个欧门吊不吊？'),(47,'FiTDh7kBV61_3HepV0i70SGNJLcZ','1699363910339',5,'2023-11-07 21:31:49',1,'food,life','塔斯汀yyds'),(48,'lnlAclSJ4dtY-cMj4KHMcc-lVKVK','1699365805168',10,'2023-11-07 22:03:23',1,'food,life','煎蛋好吃'),(49,'FrhcxQpSb_xPlPriqS-jjIVjLW56','1699366906999',11,'2023-11-07 22:21:46',1,'sport','打球');
/*!40000 ALTER TABLE `video_info` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-11-07 23:16:46
