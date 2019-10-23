-- MySQL dump 10.11
--
-- Host: 192.168.71.76    Database: db_td_config
-- ------------------------------------------------------
-- Server version	5.0.51a-24+lenny2-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_web_email_template_content`
--

DROP TABLE IF EXISTS `t_web_email_template_content`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `t_web_email_template_content` (
  `email_template_content_id` int(11) NOT NULL auto_increment COMMENT '主键，自增ID',
  `content_type` enum('MIXED','TABLE','MULTI_GAME_TABLE','EXCEL') NOT NULL default 'MIXED' COMMENT 'MIXED-å›¾+è¯´æ˜Žï¼ŒTABLE-å•æ¸¸æˆè¡¨æ ¼, MULTI_GAME_TABLE-å¤š æ¸¸æˆè¡¨æ ¼, EXCEL-excelå½¢å¼',
  `order` smallint(6) NOT NULL COMMENT '顺序',
  `content_title` varchar(255) NOT NULL default '' COMMENT '内容标题',
  `email_template_id` int(11) NOT NULL,
  PRIMARY KEY  (`email_template_content_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8 COMMENT='邮件模板内容配置表';
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `t_web_email_template_content`
--

LOCK TABLES `t_web_email_template_content` WRITE;
/*!40000 ALTER TABLE `t_web_email_template_content` DISABLE KEYS */;
INSERT INTO `t_web_email_template_content` VALUES (1,'MIXED',1,'',1),(2,'MIXED',7,'',1),(3,'MIXED',6,'',1),(4,'MIXED',19,'',1),(5,'MIXED',20,'',1),(6,'MIXED',21,'',1),(7,'MIXED',8,'',1),(8,'MIXED',4,'',1),(9,'MIXED',16,'',1),(10,'MIXED',17,'',1),(11,'MIXED',18,'',1),(12,'MIXED',2,'',1),(13,'MIXED',13,'',1),(14,'MIXED',11,'',1),(15,'MIXED',3,'',1),(16,'MIXED',14,'',1),(17,'MIXED',12,'',1),(18,'MIXED',9,'',1),(19,'MIXED',10,'',1),(20,'TABLE',0,'',1),(21,'MIXED',15,'',1),(23,'MIXED',5,'',1),(24,'EXCEL',1,'付费情况|基本情况',2),(25,'EXCEL',2,'付费情况|包月',2),(26,'EXCEL',3,'付费情况|按条',2),(27,'EXCEL',4,'付费情况|用户类型',2),(28,'EXCEL',5,'付费情况|付费率',2),(29,'EXCEL',6,'付费情况|分类用户占比',2),(30,'EXCEL',7,'付费情况|分类用户付费额',2),(31,'EXCEL',8,'付费情况|ARPPU值',2),(32,'EXCEL',9,'活跃情况|基本情况',2),(33,'EXCEL',10,'活跃情况|用户占比',2),(34,'EXCEL',11,'活跃情况|流失用户',2),(35,'EXCEL',12,'活跃情况|流失率',2),(36,'EXCEL',13,'活跃情况|在线情况',2),(37,'EXCEL',14,'活跃情况|在线时长',2),(38,'EXCEL',15,'活跃情况|登陆天数',2),(39,'MULTI_GAME_TABLE',1,'付费情况',2),(40,'MULTI_GAME_TABLE',2,'活跃情况',2);
/*!40000 ALTER TABLE `t_web_email_template_content` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-01  2:26:52
