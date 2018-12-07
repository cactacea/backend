-- MySQL dump 10.13  Distrib 8.0.13, for macos10.14 (x86_64)
--
-- Host: localhost    Database: cactacea
-- ------------------------------------------------------
-- Server version	8.0.3-rc-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for TABLE `${schema}`.`account_feeds`
--

LOCK TABLES `${schema}`.`account_feeds` WRITE;
/*!40000 ALTER TABLE `${schema}`.`account_feeds` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`account_feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`account_groups`
--

LOCK TABLES `${schema}`.`account_groups` WRITE;
/*!40000 ALTER TABLE `${schema}`.`account_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`account_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`account_messages`
--

LOCK TABLES `${schema}`.`account_messages` WRITE;
/*!40000 ALTER TABLE `${schema}`.`account_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`account_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`account_reports`
--

LOCK TABLES `${schema}`.`account_reports` WRITE;
/*!40000 ALTER TABLE `${schema}`.`account_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`account_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`accounts`
--

LOCK TABLES `${schema}`.`accounts` WRITE;
/*!40000 ALTER TABLE `${schema}`.`accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'ito_hirobumi','ito_hirobumi','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(2,'kuroda_kiyotaka','kuroda_kiyotaka','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(3,'sanjo_sanetomi','sanjo_sanetomi','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(4,'yamagata_aritomo','yamagata_aritomo','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(5,'matsutaka_masayoshi','matsutaka_masayoshi','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(6,'okuma_shigenobu','okuma_shigenobu','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(7,'saionji_kinmochi','saionji_kinmochi','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(8,'katsura_taro','katsura_taro','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(9,'terauchi_masatake','terauchi_masatake','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(10,'hara_takashi','hara_takashi','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(11,'uchida_kosai','uchida_kosai','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(12,'takahashi_korekiyo','takahashi_korekiyo','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(13,'kato_tomosaburo','kato_tomosaburo','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(14,'yamamoto_gonbei','yamamoto_gonbei','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(15,'kiyoura_keigo','kiyoura_keigo','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(16,'kato_takaaki','kato_takaaki','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(17,'wakatsuki_reijiro','wakatsuki_reijiro','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL),(18,'tanaka_giichi','tanaka_giichi','f737bd52007ca0dc9abb86b5567e8372',0,NULL,NULL,0,0,0,NULL,NULL,NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `${schema}`.`accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`advertisement_settings`
--

LOCK TABLES `${schema}`.`advertisement_settings` WRITE;
/*!40000 ALTER TABLE `${schema}`.`advertisement_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`advertisement_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`blocks`
--

LOCK TABLES `${schema}`.`blocks` WRITE;
/*!40000 ALTER TABLE `${schema}`.`blocks` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`blocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`client_grant_types`
--

LOCK TABLES `${schema}`.`client_grant_types` WRITE;
/*!40000 ALTER TABLE `${schema}`.`client_grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`client_grant_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`clients`
--

LOCK TABLES `${schema}`.`clients` WRITE;
/*!40000 ALTER TABLE `${schema}`.`clients` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`comment_likes`
--

LOCK TABLES `${schema}`.`comment_likes` WRITE;
/*!40000 ALTER TABLE `${schema}`.`comment_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`comment_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`comment_reports`
--

LOCK TABLES `${schema}`.`comment_reports` WRITE;
/*!40000 ALTER TABLE `${schema}`.`comment_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`comment_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`comments`
--

LOCK TABLES `${schema}`.`comments` WRITE;
/*!40000 ALTER TABLE `${schema}`.`comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`devices`
--

LOCK TABLES `${schema}`.`devices` WRITE;
/*!40000 ALTER TABLE `${schema}`.`devices` DISABLE KEYS */;
INSERT INTO `devices` VALUES (1,1,'bce3547d-d217-4236-b286-e06f01339eea',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(2,2,'99d90d3a-bf8f-4de8-8221-45d897408808',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(3,3,'062336dc-13c3-4d15-84e5-ea36550213b8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(4,4,'419ccf8e-3deb-45aa-88f8-f220e34445c1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(5,5,'9c104ad9-b563-46ba-abf5-e3d79a116a5f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(6,6,'621c5d81-c23e-477b-9868-38ae42b69f04',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(7,7,'0e46b29b-25fc-4ade-9d59-7acde3b25129',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(8,8,'54be02e9-bc5a-48cd-b24d-5d0bfc8c5bd5',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(9,9,'d54c9078-c819-4d98-b8a3-4f00dfc4a1f2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(10,10,'e14f32cc-2193-42e8-b137-e7ee5ab6a908',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(11,11,'112ab1bc-90bb-4e60-93f7-a53c366f3e6e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(12,12,'fc4c3a54-c8c7-4fd3-a550-2b57972a42a6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(13,13,'d071eb0d-38c3-4c9b-8de9-4a284c26a39f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(14,14,'aff249a6-cb6c-400d-a9b9-92ba96f2960a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(15,15,'7d118f14-f8d7-420c-bed0-e0837cbd8083',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(16,16,'9780c763-a6a1-43e4-9acd-24f62f9b6e18',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(17,17,'59cd38db-1d76-4abf-b7d4-a6a576576ce8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(18,18,'36a0c411-48d0-408a-bc6c-e69a75639cc0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1');
/*!40000 ALTER TABLE `${schema}`.`devices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`feed_likes`
--

LOCK TABLES `${schema}`.`feed_likes` WRITE;
/*!40000 ALTER TABLE `${schema}`.`feed_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`feed_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`feed_mediums`
--

LOCK TABLES `${schema}`.`feed_mediums` WRITE;
/*!40000 ALTER TABLE `${schema}`.`feed_mediums` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`feed_mediums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`feed_reports`
--

LOCK TABLES `${schema}`.`feed_reports` WRITE;
/*!40000 ALTER TABLE `${schema}`.`feed_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`feed_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`feed_tags`
--

LOCK TABLES `${schema}`.`feed_tags` WRITE;
/*!40000 ALTER TABLE `${schema}`.`feed_tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`feed_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`feeds`
--

LOCK TABLES `${schema}`.`feeds` WRITE;
/*!40000 ALTER TABLE `${schema}`.`feeds` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`followers`
--

LOCK TABLES `${schema}`.`followers` WRITE;
/*!40000 ALTER TABLE `${schema}`.`followers` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`follows`
--

LOCK TABLES `${schema}`.`follows` WRITE;
/*!40000 ALTER TABLE `${schema}`.`follows` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`friend_requests`
--

LOCK TABLES `${schema}`.`friend_requests` WRITE;
/*!40000 ALTER TABLE `${schema}`.`friend_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`friend_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`friends`
--

LOCK TABLES `${schema}`.`friends` WRITE;
/*!40000 ALTER TABLE `${schema}`.`friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`grant_types`
--

LOCK TABLES `${schema}`.`grant_types` WRITE;
/*!40000 ALTER TABLE `${schema}`.`grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`grant_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`group_invitations`
--

LOCK TABLES `${schema}`.`group_invitations` WRITE;
/*!40000 ALTER TABLE `${schema}`.`group_invitations` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`group_invitations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`group_reports`
--

LOCK TABLES `${schema}`.`group_reports` WRITE;
/*!40000 ALTER TABLE `${schema}`.`group_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`group_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`groups`
--

LOCK TABLES `${schema}`.`groups` WRITE;
/*!40000 ALTER TABLE `${schema}`.`groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`mediums`
--

LOCK TABLES `${schema}`.`mediums` WRITE;
/*!40000 ALTER TABLE `${schema}`.`mediums` DISABLE KEYS */;
INSERT INTO `mediums` VALUES (1,'src/main/resources/demo/images/45566754-cb4c-448e-b657-a9b71229dadc','http://10.0.1.3:9000/mediums/45566754-cb4c-448e-b657-a9b71229dadc',160,218,9141,NULL,0,1,0,0),(2,'src/main/resources/demo/images/206456be-a513-496c-9ae2-836684c1c81d','http://10.0.1.3:9000/mediums/206456be-a513-496c-9ae2-836684c1c81d',160,207,11504,NULL,0,2,0,0),(3,'src/main/resources/demo/images/cc7a87e1-2ce9-45fb-b568-cb1eb2f9eeb4','http://10.0.1.3:9000/mediums/cc7a87e1-2ce9-45fb-b568-cb1eb2f9eeb4',160,207,8942,NULL,0,3,0,0),(4,'src/main/resources/demo/images/64f68344-be79-42e4-8b58-43f80894b931','http://10.0.1.3:9000/mediums/64f68344-be79-42e4-8b58-43f80894b931',160,213,13192,NULL,0,4,0,0),(5,'src/main/resources/demo/images/c54123c1-32d7-4dab-b543-08b85d8f86fa','http://10.0.1.3:9000/mediums/c54123c1-32d7-4dab-b543-08b85d8f86fa',160,213,11710,NULL,0,5,0,0),(6,'src/main/resources/demo/images/87c25023-76ac-4bfa-9c53-ebab75633de7','http://10.0.1.3:9000/mediums/87c25023-76ac-4bfa-9c53-ebab75633de7',160,213,10802,NULL,0,6,0,0),(7,'src/main/resources/demo/images/1fdf070a-050c-4ea2-bb7f-6495a4688b93','http://10.0.1.3:9000/mediums/1fdf070a-050c-4ea2-bb7f-6495a4688b93',160,198,9571,NULL,0,7,0,0),(8,'src/main/resources/demo/images/96512e1b-cd2b-4482-a82a-51b19949c1c4','http://10.0.1.3:9000/mediums/96512e1b-cd2b-4482-a82a-51b19949c1c4',160,213,11514,NULL,0,8,0,0),(9,'src/main/resources/demo/images/9ee77a3e-1449-421e-bff5-647baca8ea4e','http://10.0.1.3:9000/mediums/9ee77a3e-1449-421e-bff5-647baca8ea4e',160,208,11127,NULL,0,9,0,0),(10,'src/main/resources/demo/images/28ffa40d-f3c0-4073-884d-b9dca6fd1dab','http://10.0.1.3:9000/mediums/28ffa40d-f3c0-4073-884d-b9dca6fd1dab',160,212,8517,NULL,0,10,0,0),(11,'src/main/resources/demo/images/2e6b95c1-3cf6-4159-8062-56d266e1a4aa','http://10.0.1.3:9000/mediums/2e6b95c1-3cf6-4159-8062-56d266e1a4aa',160,213,9038,NULL,0,11,0,0),(12,'src/main/resources/demo/images/b423a05f-fdba-4d01-b1b9-c9fafa2aa3c9','http://10.0.1.3:9000/mediums/b423a05f-fdba-4d01-b1b9-c9fafa2aa3c9',160,200,6941,NULL,0,12,0,0),(13,'src/main/resources/demo/images/2ed13b5e-cb62-47e3-ba70-130f543a24fe','http://10.0.1.3:9000/mediums/2ed13b5e-cb62-47e3-ba70-130f543a24fe',160,213,6824,NULL,0,13,0,0),(14,'src/main/resources/demo/images/f1c7e173-356c-4878-ad74-8f16f2a00f14','http://10.0.1.3:9000/mediums/f1c7e173-356c-4878-ad74-8f16f2a00f14',160,210,9566,NULL,0,14,0,0),(15,'src/main/resources/demo/images/7f9bf9b6-e6f3-40fc-9c16-b77155e5254e','http://10.0.1.3:9000/mediums/7f9bf9b6-e6f3-40fc-9c16-b77155e5254e',160,213,11265,NULL,0,15,0,0),(16,'src/main/resources/demo/images/9efb70ef-3411-4e6b-8f61-1f78f0de5644','http://10.0.1.3:9000/mediums/9efb70ef-3411-4e6b-8f61-1f78f0de5644',160,212,6955,NULL,0,16,0,0),(17,'src/main/resources/demo/images/7184a553-18df-4ddd-b617-3b143a2658a3','http://10.0.1.3:9000/mediums/7184a553-18df-4ddd-b617-3b143a2658a3',160,198,6621,NULL,0,17,0,0),(18,'src/main/resources/demo/images/768e73b3-b78d-407c-a07b-20314fa4c4f9','http://10.0.1.3:9000/mediums/768e73b3-b78d-407c-a07b-20314fa4c4f9',160,198,7181,NULL,0,18,0,0);
/*!40000 ALTER TABLE `${schema}`.`mediums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`messages`
--

LOCK TABLES `${schema}`.`messages` WRITE;
/*!40000 ALTER TABLE `${schema}`.`messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`mutes`
--

LOCK TABLES `${schema}`.`mutes` WRITE;
/*!40000 ALTER TABLE `${schema}`.`mutes` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`mutes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`notifications`
--

LOCK TABLES `${schema}`.`notifications` WRITE;
/*!40000 ALTER TABLE `${schema}`.`notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`push_notification_settings`
--

LOCK TABLES `${schema}`.`push_notification_settings` WRITE;
/*!40000 ALTER TABLE `${schema}`.`push_notification_settings` DISABLE KEYS */;
INSERT INTO `push_notification_settings` VALUES (1,1,1,1,1,1,1),(2,1,1,1,1,1,1),(3,1,1,1,1,1,1),(4,1,1,1,1,1,1),(5,1,1,1,1,1,1),(6,1,1,1,1,1,1),(7,1,1,1,1,1,1),(8,1,1,1,1,1,1),(9,1,1,1,1,1,1),(10,1,1,1,1,1,1),(11,1,1,1,1,1,1),(12,1,1,1,1,1,1),(13,1,1,1,1,1,1),(14,1,1,1,1,1,1),(15,1,1,1,1,1,1),(16,1,1,1,1,1,1),(17,1,1,1,1,1,1),(18,1,1,1,1,1,1);
/*!40000 ALTER TABLE `${schema}`.`push_notification_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for TABLE `${schema}`.`relationships`
--

LOCK TABLES `${schema}`.`relationships` WRITE;
/*!40000 ALTER TABLE `${schema}`.`relationships` DISABLE KEYS */;
/*!40000 ALTER TABLE `${schema}`.`relationships` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-12-07 17:11:27
