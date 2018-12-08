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
-- Dumping data for table `account_feeds`
--

LOCK TABLES `account_feeds` WRITE;
/*!40000 ALTER TABLE `account_feeds` DISABLE KEYS */;
INSERT INTO `account_feeds` VALUES (1,1,0,43,20181208035749),(43,2,0,1,20181208035749),(43,3,0,1,20181208035749),(43,4,0,1,20181208035749),(43,5,0,1,20181208035749);
/*!40000 ALTER TABLE `account_feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `account_groups`
--

LOCK TABLES `account_groups` WRITE;
/*!40000 ALTER TABLE `account_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `account_messages`
--

LOCK TABLES `account_messages` WRITE;
/*!40000 ALTER TABLE `account_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `account_reports`
--

LOCK TABLES `account_reports` WRITE;
/*!40000 ALTER TABLE `account_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'aritomo_yamagata','aritomo_yamagata','f737bd52007ca0dc9abb86b5567e8372',1,1,'http://localhost:9000/mediums/28998af5-fa51-48eb-9b36-ec95c846d3fd',1,0,4,NULL,NULL,NULL,NULL,0,NULL),(2,'eisaku_sato','eisaku_sato','f737bd52007ca0dc9abb86b5567e8372',1,2,'http://localhost:9000/mediums/50e273b0-dec3-4285-bb6b-d0ff42153772',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(3,'giichi_tanaka','giichi_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,3,'http://localhost:9000/mediums/ef863ca8-bf39-415d-b302-d293e2711337',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(4,'gonbei_yamamoto','gonbei_yamamoto','f737bd52007ca0dc9abb86b5567e8372',1,4,'http://localhost:9000/mediums/8ce4f8d5-f947-4af4-9c5c-3db2d471e2b9',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(5,'hayato_ikeda','hayato_ikeda','f737bd52007ca0dc9abb86b5567e8372',1,5,'http://localhost:9000/mediums/2870bc72-8164-452d-9bf6-88441e776762',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(6,'hideki_tojo','hideki_tojo','f737bd52007ca0dc9abb86b5567e8372',1,6,'http://localhost:9000/mediums/5509d3bc-074b-423f-8395-7b18f676b493',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(7,'hirobumi_ito','hirobumi_ito','f737bd52007ca0dc9abb86b5567e8372',1,7,'http://localhost:9000/mediums/9d8b63ec-0e5c-4763-adce-5addfc47a35d',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(8,'hitoshi_ashida','hitoshi_ashida','f737bd52007ca0dc9abb86b5567e8372',1,8,'http://localhost:9000/mediums/e9eca641-0a2d-48ea-9c7a-846c857c492c',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(9,'ichiro_hatoyama','ichiro_hatoyama','f737bd52007ca0dc9abb86b5567e8372',1,9,'http://localhost:9000/mediums/ddb0aff9-4d05-4fe6-8dd0-a901dff1244f',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(10,'junichiro_koizumi','junichiro_koizumi','f737bd52007ca0dc9abb86b5567e8372',1,10,'http://localhost:9000/mediums/4df40e6d-e369-4b93-835b-0cf5ea245d1f',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(11,'kakuei_tanaka','kakuei_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,11,'http://localhost:9000/mediums/7a07ae29-d2c7-4ee7-af92-7d135b9b5bfa',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(12,'kantaro_suzuki','kantaro_suzuki','f737bd52007ca0dc9abb86b5567e8372',1,12,'http://localhost:9000/mediums/494b5e44-9f98-4ed3-8716-fb080717a74e',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(13,'keigo_kiyoura','keigo_kiyoura','f737bd52007ca0dc9abb86b5567e8372',1,13,'http://localhost:9000/mediums/d0b8316b-6e48-4d24-a1cb-8448ef0390ed',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(14,'keisuke_okada','keisuke_okada','f737bd52007ca0dc9abb86b5567e8372',1,14,'http://localhost:9000/mediums/2c068484-573c-430c-966e-80ec29961784',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(15,'keizo_obuchi','keizo_obuchi','f737bd52007ca0dc9abb86b5567e8372',1,15,'http://localhost:9000/mediums/326bec45-d499-4a4a-8310-a2917fda015a',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(16,'kiichi_miyazawa','kiichi_miyazawa','f737bd52007ca0dc9abb86b5567e8372',1,16,'http://localhost:9000/mediums/2e7fbc4f-4997-4673-b05e-78850f451cde',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(17,'kiichiro_hiranumra','kiichiro_hiranumra','f737bd52007ca0dc9abb86b5567e8372',1,17,'http://localhost:9000/mediums/bbe31bad-6bce-4a9b-ad85-c260b2e33981',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(18,'kijuro_shidehara','kijuro_shidehara','f737bd52007ca0dc9abb86b5567e8372',1,18,'http://localhost:9000/mediums/9786be67-a683-450f-823f-9476fb9de554',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(19,'kinmochi_saionji','kinmochi_saionji','f737bd52007ca0dc9abb86b5567e8372',1,19,'http://localhost:9000/mediums/b88527fb-c633-4e7e-9c24-bfc52a97e29a',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(20,'kiyotaka_kuroda','kiyotaka_kuroda','f737bd52007ca0dc9abb86b5567e8372',1,20,'http://localhost:9000/mediums/1198cd38-6deb-4b1b-a525-c76002d82c9b',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(21,'kohki_hirota','kohki_hirota','f737bd52007ca0dc9abb86b5567e8372',1,21,'http://localhost:9000/mediums/f6139c44-4aae-4b32-a1f7-ebe2ac3e6626',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(22,'korekiyo_takahashi','korekiyo_takahashi','f737bd52007ca0dc9abb86b5567e8372',1,22,'http://localhost:9000/mediums/f4576e45-7c09-43ba-8717-b908f40d2822',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(23,'kosai_uchida','kosai_uchida','f737bd52007ca0dc9abb86b5567e8372',1,23,'http://localhost:9000/mediums/b9bc139d-0872-45fb-bf01-466f8c5d2fbc',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(24,'kuniaki_koiso','kuniaki_koiso','f737bd52007ca0dc9abb86b5567e8372',1,24,'http://localhost:9000/mediums/a2b1a1fe-9688-4a39-b332-b8e0714daad7',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(25,'makoto_saito','makoto_saito','f737bd52007ca0dc9abb86b5567e8372',1,25,'http://localhost:9000/mediums/c9642ef2-63b9-4e1b-a492-258adb488213',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(26,'masayoshi_ito','masayoshi_ito','f737bd52007ca0dc9abb86b5567e8372',1,26,'http://localhost:9000/mediums/c698aad2-e04f-451f-a0f6-9614ce49eca2',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(27,'masayoshi_matsutaka','masayoshi_matsutaka','f737bd52007ca0dc9abb86b5567e8372',1,27,'http://localhost:9000/mediums/fc79d2a6-c521-4d1a-bae9-f1d2db34e651',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(28,'masayoshi_ohira','masayoshi_ohira','f737bd52007ca0dc9abb86b5567e8372',1,28,'http://localhost:9000/mediums/85d08941-9e12-4559-8fc9-a85718c7f956',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(29,'mitsumasa_yonai','mitsumasa_yonai','f737bd52007ca0dc9abb86b5567e8372',1,29,'http://localhost:9000/mediums/167692bf-77a2-476b-b791-376f405e20eb',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(30,'morihiro_hosokawa','morihiro_hosokawa','f737bd52007ca0dc9abb86b5567e8372',1,30,'http://localhost:9000/mediums/19c531fc-ca48-4f80-823c-e9d0cbc91366',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(31,'naoto_kan','naoto_kan','f737bd52007ca0dc9abb86b5567e8372',1,31,'http://localhost:9000/mediums/1164ea0f-a08c-4b13-a9b9-b23741e8881d',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(32,'naruhiko_higashikuni','naruhiko_higashikuni','f737bd52007ca0dc9abb86b5567e8372',1,32,'http://localhost:9000/mediums/cfea254d-260f-4af4-b20d-123f711f4831',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(33,'noboru_takeshita','noboru_takeshita','f737bd52007ca0dc9abb86b5567e8372',1,33,'http://localhost:9000/mediums/d7fb2f45-9087-43fa-a225-c6f04b9a2206',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(34,'nobusuke_kishi','nobusuke_kishi','f737bd52007ca0dc9abb86b5567e8372',1,34,'http://localhost:9000/mediums/9c8b7201-813f-4ff9-afce-aacc37d8242c',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(35,'nobuyuki_abe','nobuyuki_abe','f737bd52007ca0dc9abb86b5567e8372',1,35,'http://localhost:9000/mediums/1b76901f-20c0-4781-93b5-0d1ebeb0dc3f',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(36,'okuma_shigenobu','okuma_shigenobu','f737bd52007ca0dc9abb86b5567e8372',1,36,'http://localhost:9000/mediums/91265027-3465-47f4-b824-b5188b1a7323',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(37,'osachi_hamaguchi','osachi_hamaguchi','f737bd52007ca0dc9abb86b5567e8372',1,37,'http://localhost:9000/mediums/5c3f5cd9-c67d-42df-a04f-01100692aac4',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(38,'reijiro_wakatsuki','reijiro_wakatsuki','f737bd52007ca0dc9abb86b5567e8372',1,38,'http://localhost:9000/mediums/de82e58e-56e2-4b12-b4ca-891028c1d932',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(39,'ryutaro_hashimoto','ryutaro_hashimoto','f737bd52007ca0dc9abb86b5567e8372',1,39,'http://localhost:9000/mediums/16aec69a-a663-43db-ac51-77db9ad07d5b',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(40,'sanetomi_sanjo','sanetomi_sanjo','f737bd52007ca0dc9abb86b5567e8372',1,40,'http://localhost:9000/mediums/08785def-8fce-49a4-bc0a-3b9a51996334',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(41,'senjuro_hayashi','senjuro_hayashi','f737bd52007ca0dc9abb86b5567e8372',1,41,'http://localhost:9000/mediums/b8d92210-cd08-41ea-bfd9-4162e1bcb99b',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(42,'shigeru_yoshida','shigeru_yoshida','f737bd52007ca0dc9abb86b5567e8372',1,42,'http://localhost:9000/mediums/02beb355-19f6-43ac-9289-9161913e4ecc',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(43,'shinzo_abe','shinzo_abe','f737bd52007ca0dc9abb86b5567e8372',1,43,'http://localhost:9000/mediums/54bab104-fe20-4b99-9cea-81f5f6e66345',1,0,1,NULL,NULL,NULL,NULL,0,NULL),(44,'sosuke_uno','sosuke_uno','f737bd52007ca0dc9abb86b5567e8372',1,44,'http://localhost:9000/mediums/42babce2-66e6-4681-944d-54f234fb9712',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(45,'takaaki_kato','takaaki_kato','f737bd52007ca0dc9abb86b5567e8372',1,45,'http://localhost:9000/mediums/ac5b63ab-9daf-4a84-9e58-f46cd5e8360b',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(46,'takashi_hara','takashi_hara','f737bd52007ca0dc9abb86b5567e8372',1,46,'http://localhost:9000/mediums/8d216c42-6e7e-4cf3-90a0-8c39b3c8d754',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(47,'takeo_fukuda','takeo_fukuda','f737bd52007ca0dc9abb86b5567e8372',1,47,'http://localhost:9000/mediums/74121ad5-fa2e-4e4b-a96e-8e69d3b5869b',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(48,'takeo_miki','takeo_miki','f737bd52007ca0dc9abb86b5567e8372',1,48,'http://localhost:9000/mediums/325323b0-28cf-467c-bfea-7383fc150d18',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(49,'tanzan_ishibashi','tanzan_ishibashi','f737bd52007ca0dc9abb86b5567e8372',1,49,'http://localhost:9000/mediums/45795c3d-e514-4271-b7d2-a224d8dee40b',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(50,'taro_aso','taro_aso','f737bd52007ca0dc9abb86b5567e8372',1,50,'http://localhost:9000/mediums/0687b569-0008-4a00-9936-b6ef8bd0e1e2',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(51,'taro_katsura','taro_katsura','f737bd52007ca0dc9abb86b5567e8372',1,51,'http://localhost:9000/mediums/abcf6c16-dbf9-4dce-82f9-fd42251b2b6b',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(52,'terauchi_masatake','terauchi_masatake','f737bd52007ca0dc9abb86b5567e8372',1,52,'http://localhost:9000/mediums/6043f026-4b0b-4dcb-87cf-3332a91c1063',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(53,'tetsu_katayama','tetsu_katayama','f737bd52007ca0dc9abb86b5567e8372',1,53,'http://localhost:9000/mediums/c9deba06-5f46-471c-b771-f245da176b8e',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(54,'tomiichi_murayama','tomiichi_murayama','f737bd52007ca0dc9abb86b5567e8372',1,54,'http://localhost:9000/mediums/524ba2ee-2175-4f51-ab20-6808b0e568ff',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(55,'tomosaburo_kato','tomosaburo_kato','f737bd52007ca0dc9abb86b5567e8372',1,55,'http://localhost:9000/mediums/f0e3834f-70e2-4d40-ad79-3be1f3e88c6a',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(56,'toshiki_kaifu','toshiki_kaifu','f737bd52007ca0dc9abb86b5567e8372',1,56,'http://localhost:9000/mediums/acbadbb1-9835-4b93-a3e6-645aea31218d',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(57,'tsutomu_hata','tsutomu_hata','f737bd52007ca0dc9abb86b5567e8372',1,57,'http://localhost:9000/mediums/b386d476-13df-4926-8725-e44e024568fd',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(58,'tsuyoshi_inukai','tsuyoshi_inukai','f737bd52007ca0dc9abb86b5567e8372',1,58,'http://localhost:9000/mediums/602f8695-c0a7-4b4f-9b51-12b8046a1950',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(59,'yasuhiro_nakasone','yasuhiro_nakasone','f737bd52007ca0dc9abb86b5567e8372',2,59,'http://localhost:9000/mediums/e6345f69-e6cb-4163-ac78-6ea84e2feee4',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(60,'yasuo_fukuda','yasuo_fukuda','f737bd52007ca0dc9abb86b5567e8372',2,60,'http://localhost:9000/mediums/4742b9b8-7fd2-4723-8ef0-b48aad0900d0',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(61,'yoshihiko_noda','yoshihiko_noda','f737bd52007ca0dc9abb86b5567e8372',2,61,'http://localhost:9000/mediums/04d76fd9-9d05-4c98-8ea2-4da0ec29393e',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(62,'yoshiro_mori','yoshiro_mori','f737bd52007ca0dc9abb86b5567e8372',2,62,'http://localhost:9000/mediums/f99c726e-c351-4ba6-b751-745181b900cc',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(63,'yukio_hatoyama','yukio_hatoyama','f737bd52007ca0dc9abb86b5567e8372',2,63,'http://localhost:9000/mediums/84485f79-8d6c-4561-8a58-8159ce7437a9',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(64,'zenko_suzuki','zenko_suzuki','f737bd52007ca0dc9abb86b5567e8372',2,64,'http://localhost:9000/mediums/eb602c84-e587-414e-a388-330d570f3aa0',2,0,0,NULL,NULL,NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `advertisement_settings`
--

LOCK TABLES `advertisement_settings` WRITE;
/*!40000 ALTER TABLE `advertisement_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `advertisement_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `blocks`
--

LOCK TABLES `blocks` WRITE;
/*!40000 ALTER TABLE `blocks` DISABLE KEYS */;
/*!40000 ALTER TABLE `blocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `client_grant_types`
--

LOCK TABLES `client_grant_types` WRITE;
/*!40000 ALTER TABLE `client_grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_grant_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `comment_likes`
--

LOCK TABLES `comment_likes` WRITE;
/*!40000 ALTER TABLE `comment_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `comment_reports`
--

LOCK TABLES `comment_reports` WRITE;
/*!40000 ALTER TABLE `comment_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `devices`
--

LOCK TABLES `devices` WRITE;
/*!40000 ALTER TABLE `devices` DISABLE KEYS */;
INSERT INTO `devices` VALUES (1,1,'df747e76-ab1d-45a0-94fc-d61a472f9a98',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(2,2,'860659f1-efb1-45d7-8215-b4bf27dad10a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(3,3,'49ed35df-5a49-46a8-9bab-4cdc6028ec0d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(4,4,'ab4b0f34-046f-48c0-9b0a-5dad84b5d070',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(5,5,'f98a987d-c465-4859-a1d6-d2b3f25b0e5e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(6,6,'f1722ad5-0ee1-41a9-8a62-03f0135ce4e1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(7,7,'b223adfe-b6b6-429b-8156-a719853e6301',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(8,8,'e59075fd-cfb5-4e9f-8dde-8a5682b19c70',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(9,9,'c904158e-a84f-44e0-a3dc-f7cf6632c48a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(10,10,'c94f63d1-98a0-4f07-9784-efc0a7808a21',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(11,11,'a8d21e57-afbf-4613-a686-aa9eb443e27e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(12,12,'60f1f49c-e3b5-4e15-9311-0f3ffa1ced33',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(13,13,'56ce6c62-e9d8-4080-a169-97ccfec6799c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(14,14,'ecaea1e3-e831-47a4-bc0a-9437749fa91c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(15,15,'6ee27fbc-5603-48c5-9d0a-f3f6d993db84',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(16,16,'2a3ac936-8e5b-4bf9-9e85-4ac6a13beff7',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(17,17,'68b47a81-ec23-4cde-8682-9ad16e07636a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(18,18,'8849eca3-60fb-40a0-bf27-c787f8659827',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(19,19,'a726fca0-4a4b-422b-a676-164a5ae5ca92',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(20,20,'8b930c34-6f2d-4401-9c79-d282bc671383',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(21,21,'cc98ff3e-559a-4fd2-90f4-b3fae87e0d55',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(22,22,'55eabd36-8389-4f57-b38a-fe592ea73d6e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(23,23,'cc3b6a5f-a7c4-4c60-88e9-5370a7a142c0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(24,24,'95c571ef-9156-4719-a643-de00acfe8bf1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(25,25,'4db97144-4250-49ac-a049-2035ab59c3f8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(26,26,'bbad1d00-54c2-49bf-90e3-d656f46ab5ae',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(27,27,'48cec4cd-bc6a-4a5e-a57e-0e9b094dce6d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(28,28,'b65a21a4-7446-4254-90da-510fd3d6b221',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(29,29,'895f1e2b-54d1-4434-bb3d-dd4ae17c3a82',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(30,30,'4674b06c-96a9-4e06-aa84-61b27f0c143a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(31,31,'33ee004e-d656-4ad5-8a22-bb8ea10e7bdb',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(32,32,'8116b4c9-a0ea-4b0e-b2e4-65ec251b884b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(33,33,'36f6dfc9-3ff6-4615-a48d-02dd9c4c5526',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(34,34,'c4bc193f-ec00-46c8-94d1-f7156d56ec80',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(35,35,'43719dbb-2235-40ba-889a-0630a5b5cd5e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(36,36,'c6ea8296-569e-4299-85ec-2febb4eaa1fb',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(37,37,'87193bad-1af8-42a0-af6a-f760ae4fd62c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(38,38,'823e10e3-be6b-4633-9e78-cdb1adf16bcf',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(39,39,'dbc61311-a82b-4410-b1b7-65d9b9cdd786',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(40,40,'283b798e-d11b-4486-a6d1-b6d291aa3979',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(41,41,'fb7e4f6f-239c-4283-8fac-3c89da47bbe4',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(42,42,'e5e151ba-570d-497a-b337-ec095e869657',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(43,43,'a000a0be-965b-4ff6-ab2f-5a871d6eca39',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(44,44,'c354ec05-6861-4441-a558-90eff7f4cc77',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(45,45,'d707ee50-c7cb-400d-88f5-0f09c473bcfe',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(46,46,'a42835ad-0417-49a1-9cf1-3d133114062c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(47,47,'a9bc1e36-69d0-4ef6-89bf-e629a3757d58',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(48,48,'a4908426-300a-4ec9-8f38-ca1ddfe6ce6f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(49,49,'13648032-6f73-4f94-8760-4f90a114e93a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(50,50,'8924bb78-8b72-4f62-8a73-0c532d3caa22',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(51,51,'68766b5e-6392-4980-8ef5-edc3a2f8447c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(52,52,'33b70603-c8b6-439e-a134-080b1ed82915',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(53,53,'4d67c512-fcd3-4c63-a665-81f60c8c26de',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(54,54,'1cfa0e1c-a0a0-4bd2-b2d5-884daf780acf',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(55,55,'dd0066cb-a825-427d-a623-347f5723b1f2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(56,56,'40b88317-5e5e-4f21-9926-a11f15b0b98c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(57,57,'5dcf9bf1-3375-412e-bc99-7efbd0bfff4f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(58,58,'d1b6e34b-2cce-4b6e-afd8-324e451bdf8f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(59,59,'28b34b25-b374-474c-bad7-4cba3f2cebc3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(60,60,'704a11f8-530b-42da-962b-fe02c417c86b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(61,61,'492660c6-88f3-4925-8474-0be426049f33',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(62,62,'69ea53de-c3ce-42a0-bd48-d0ac54badf9d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(63,63,'1c909b69-c55b-47fc-9e67-05fe28464f2f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(64,64,'f7e74fc7-2fb3-4c26-8c38-c7fcd8d854c1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1');
/*!40000 ALTER TABLE `devices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feed_likes`
--

LOCK TABLES `feed_likes` WRITE;
/*!40000 ALTER TABLE `feed_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `feed_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feed_mediums`
--

LOCK TABLES `feed_mediums` WRITE;
/*!40000 ALTER TABLE `feed_mediums` DISABLE KEYS */;
INSERT INTO `feed_mediums` VALUES (1,65,0),(2,66,0),(3,67,0),(4,68,0),(5,69,0);
/*!40000 ALTER TABLE `feed_mediums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feed_reports`
--

LOCK TABLES `feed_reports` WRITE;
/*!40000 ALTER TABLE `feed_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `feed_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feed_tags`
--

LOCK TABLES `feed_tags` WRITE;
/*!40000 ALTER TABLE `feed_tags` DISABLE KEYS */;
INSERT INTO `feed_tags` VALUES (1,'daimyo',0),(1,'sengoku',1),(2,'daimyo',0),(2,'sengoku',1),(3,'daimyo',0),(3,'sengoku',1),(4,'daimyo',0),(4,'sengoku',1),(5,'samurai',0),(5,'sengoku',1);
/*!40000 ALTER TABLE `feed_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feeds`
--

LOCK TABLES `feeds` WRITE;
/*!40000 ALTER TABLE `feeds` DISABLE KEYS */;
INSERT INTO `feeds` VALUES (1,'Uesugi Kenshin (上杉 謙信, February 18, 1530 – April 19, 1578[1]) was a daimyō who was born as Nagao Kagetora,[2] and after the adoption into the Uesugi clan, ruled Echigo Province in the Sengoku period of Japan.[3] He was one of the most powerful daimyōs of the Sengoku period. While chiefly remembered for his prowess on the battlefield, Kenshin is also regarded as an extremely skillful administrator who fostered the growth of local industries and trade; his rule saw a marked rise in the standard of living of Echigo.',43,0,0,0,0,0,NULL,0,1544241469283),(2,'Oda Nobunaga (help·info), June 23, 1534 – June 21, 1582) was a powerful daimyō (feudal lord) of Japan in the late 16th century who attempted to unify Japan during the late Sengoku period, and successfully gained control over most of Honshu. Nobunaga is regarded as one of three unifiers of Japan along with his retainers Toyotomi Hideyoshi and Tokugawa Ieyasu. During his later life, Nobunaga was widely known for most brutal suppression of determined opponents, eliminating those who by principle refused to cooperate or yield to his demands. His reign was noted for innovative military tactics, fostering free trade, and encouraging the start of the Momoyama historical art period. He was killed when his retainer Akechi Mitsuhide rebelled against him at Honnō-ji.',1,0,0,0,0,0,NULL,0,1544241469437),(3,'Tokugawa Ieyasu (徳川家康, January 30, 1543 – June 1, 1616) was the founder and first shōgun of the Tokugawa shogunate of Japan, which effectively ruled Japan from the Battle of Sekigahara in 1600 until the Meiji Restoration in 1868. Ieyasu seized power in 1600, received appointment as shōgun in 1603, and abdicated from office in 1605, but remained in power until his death in 1616. His given name is sometimes spelled Iyeyasu,[1][2] according to the historical pronunciation of the kana character he. Ieyasu was posthumously enshrined at Nikkō Tōshō-gū with the name Tōshō Daigongen (東照大権現). He was one of the three unifiers of Japan, along with his former lord Nobunaga and Toyotomi Hideyoshi.',1,0,0,0,0,0,NULL,0,1544241469524),(4,'Toyotomi Hideyoshi (豊臣 秀吉, March 17, 1537 – September 18, 1598) was a preeminent daimyō, warrior, general, samurai, and politician of the Sengoku period[1] who is regarded as Japan\'s second \"great unifier\".[2] He succeeded his former liege lord, Oda Nobunaga, and brought an end to the Warring Lords period. The period of his rule is often called the Momoyama period, named after Hideyoshi\'s castle. After his death, his young son Hideyori was displaced by Tokugawa Ieyasu.',1,0,0,0,0,0,NULL,0,1544241469582),(5,'Yasuke (variously rendered as 弥助 or 弥介, 彌助 or 彌介 in different sources.[1]) (b. c. 1555–1590) was a Samurai of African origin who served under the Japanese hegemon and warlord Oda Nobunaga in 1581 and 1582.',1,0,0,2,0,0,NULL,0,1544241469634);
/*!40000 ALTER TABLE `feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `followers`
--

LOCK TABLES `followers` WRITE;
/*!40000 ALTER TABLE `followers` DISABLE KEYS */;
INSERT INTO `followers` VALUES (1,1,43,1544241466694),(2,2,43,1544241466750),(3,3,43,1544241466782),(4,4,43,1544241466833),(5,5,43,1544241466895),(6,6,43,1544241466925),(7,7,43,1544241466952),(8,8,43,1544241466976),(9,9,43,1544241467001),(10,10,43,1544241467036),(11,11,43,1544241467079),(12,12,43,1544241467126),(13,13,43,1544241467153),(14,14,43,1544241467179),(15,15,43,1544241467205),(16,16,43,1544241467243),(17,17,43,1544241467297),(18,18,43,1544241467346),(19,19,43,1544241467374),(20,20,43,1544241467400),(21,21,43,1544241467424),(22,22,43,1544241467467),(23,23,43,1544241467518),(24,24,43,1544241467553),(25,25,43,1544241467577),(26,26,43,1544241467603),(27,27,43,1544241467629),(28,28,43,1544241467656),(29,29,43,1544241467699),(30,30,43,1544241467755),(31,31,43,1544241467790),(32,32,43,1544241467828),(33,33,43,1544241467858),(34,34,43,1544241467887),(35,35,43,1544241467940),(36,36,43,1544241467994),(37,37,43,1544241468030),(38,38,43,1544241468060),(39,39,43,1544241468084),(40,40,43,1544241468108),(41,41,43,1544241468144),(42,42,43,1544241468191),(43,44,43,1544241468243),(44,45,43,1544241468272),(45,46,43,1544241468300),(46,47,43,1544241468323),(47,48,43,1544241468359),(48,49,43,1544241468404),(49,50,43,1544241468447),(50,51,43,1544241468475),(51,52,43,1544241468507),(52,53,43,1544241468533),(53,54,43,1544241468569),(54,55,43,1544241468617),(55,56,43,1544241468667),(56,57,43,1544241468695),(57,58,43,1544241468728),(58,59,43,1544241468756),(59,60,43,1544241468801),(60,61,43,1544241468855),(61,62,43,1544241468898),(62,63,43,1544241468924),(63,64,43,1544241468950),(64,59,1,1544241468981),(65,60,1,1544241469026),(66,61,1,1544241469070),(67,62,1,1544241469108),(68,63,1,1544241469130),(69,64,1,1544241469154),(70,43,1,1544241469177);
/*!40000 ALTER TABLE `followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `follows`
--

LOCK TABLES `follows` WRITE;
/*!40000 ALTER TABLE `follows` DISABLE KEYS */;
INSERT INTO `follows` VALUES (1,43,1,1544241466663),(2,43,2,1544241466740),(3,43,3,1544241466775),(4,43,4,1544241466823),(5,43,5,1544241466885),(6,43,6,1544241466921),(7,43,7,1544241466947),(8,43,8,1544241466972),(9,43,9,1544241466996),(10,43,10,1544241467031),(11,43,11,1544241467073),(12,43,12,1544241467120),(13,43,13,1544241467149),(14,43,14,1544241467174),(15,43,15,1544241467200),(16,43,16,1544241467233),(17,43,17,1544241467286),(18,43,18,1544241467340),(19,43,19,1544241467368),(20,43,20,1544241467396),(21,43,21,1544241467419),(22,43,22,1544241467457),(23,43,23,1544241467511),(24,43,24,1544241467549),(25,43,25,1544241467574),(26,43,26,1544241467599),(27,43,27,1544241467624),(28,43,28,1544241467650),(29,43,29,1544241467692),(30,43,30,1544241467741),(31,43,31,1544241467785),(32,43,32,1544241467823),(33,43,33,1544241467853),(34,43,34,1544241467881),(35,43,35,1544241467932),(36,43,36,1544241467986),(37,43,37,1544241468024),(38,43,38,1544241468055),(39,43,39,1544241468080),(40,43,40,1544241468104),(41,43,41,1544241468138),(42,43,42,1544241468184),(43,43,44,1544241468238),(44,43,45,1544241468267),(45,43,46,1544241468296),(46,43,47,1544241468319),(47,43,48,1544241468351),(48,43,49,1544241468392),(49,43,50,1544241468442),(50,43,51,1544241468470),(51,43,52,1544241468503),(52,43,53,1544241468528),(53,43,54,1544241468564),(54,43,55,1544241468606),(55,43,56,1544241468662),(56,43,57,1544241468690),(57,43,58,1544241468722),(58,43,59,1544241468750),(59,43,60,1544241468793),(60,43,61,1544241468847),(61,43,62,1544241468893),(62,43,63,1544241468920),(63,43,64,1544241468946),(64,1,59,1544241468975),(65,1,60,1544241469018),(66,1,61,1544241469063),(67,1,62,1544241469103),(68,1,63,1544241469125),(69,1,64,1544241469150),(70,1,43,1544241469174);
/*!40000 ALTER TABLE `follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `friend_requests`
--

LOCK TABLES `friend_requests` WRITE;
/*!40000 ALTER TABLE `friend_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `friends`
--

LOCK TABLES `friends` WRITE;
/*!40000 ALTER TABLE `friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `grant_types`
--

LOCK TABLES `grant_types` WRITE;
/*!40000 ALTER TABLE `grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `grant_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `group_invitations`
--

LOCK TABLES `group_invitations` WRITE;
/*!40000 ALTER TABLE `group_invitations` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_invitations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `group_reports`
--

LOCK TABLES `group_reports` WRITE;
/*!40000 ALTER TABLE `group_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `mediums`
--

LOCK TABLES `mediums` WRITE;
/*!40000 ALTER TABLE `mediums` DISABLE KEYS */;
INSERT INTO `mediums` VALUES (1,'28998af5-fa51-48eb-9b36-ec95c846d3fd','http://localhost:9000/mediums/28998af5-fa51-48eb-9b36-ec95c846d3fd',160,213,13192,NULL,0,1,0,0),(2,'50e273b0-dec3-4285-bb6b-d0ff42153772','http://localhost:9000/mediums/50e273b0-dec3-4285-bb6b-d0ff42153772',160,227,10510,NULL,0,2,0,0),(3,'ef863ca8-bf39-415d-b302-d293e2711337','http://localhost:9000/mediums/ef863ca8-bf39-415d-b302-d293e2711337',160,198,7181,NULL,0,3,0,0),(4,'8ce4f8d5-f947-4af4-9c5c-3db2d471e2b9','http://localhost:9000/mediums/8ce4f8d5-f947-4af4-9c5c-3db2d471e2b9',160,210,9566,NULL,0,4,0,0),(5,'2870bc72-8164-452d-9bf6-88441e776762','http://localhost:9000/mediums/2870bc72-8164-452d-9bf6-88441e776762',160,213,5901,NULL,0,5,0,0),(6,'5509d3bc-074b-423f-8395-7b18f676b493','http://localhost:9000/mediums/5509d3bc-074b-423f-8395-7b18f676b493',160,215,7435,NULL,0,6,0,0),(7,'9d8b63ec-0e5c-4763-adce-5addfc47a35d','http://localhost:9000/mediums/9d8b63ec-0e5c-4763-adce-5addfc47a35d',160,218,9141,NULL,0,7,0,0),(8,'e9eca641-0a2d-48ea-9c7a-846c857c492c','http://localhost:9000/mediums/e9eca641-0a2d-48ea-9c7a-846c857c492c',160,194,12280,NULL,0,8,0,0),(9,'ddb0aff9-4d05-4fe6-8dd0-a901dff1244f','http://localhost:9000/mediums/ddb0aff9-4d05-4fe6-8dd0-a901dff1244f',160,195,9804,NULL,0,9,0,0),(10,'4df40e6d-e369-4b93-835b-0cf5ea245d1f','http://localhost:9000/mediums/4df40e6d-e369-4b93-835b-0cf5ea245d1f',131,175,53624,NULL,0,10,0,0),(11,'7a07ae29-d2c7-4ee7-af92-7d135b9b5bfa','http://localhost:9000/mediums/7a07ae29-d2c7-4ee7-af92-7d135b9b5bfa',148,183,34578,NULL,0,11,0,0),(12,'494b5e44-9f98-4ed3-8716-fb080717a74e','http://localhost:9000/mediums/494b5e44-9f98-4ed3-8716-fb080717a74e',160,201,5381,NULL,0,12,0,0),(13,'d0b8316b-6e48-4d24-a1cb-8448ef0390ed','http://localhost:9000/mediums/d0b8316b-6e48-4d24-a1cb-8448ef0390ed',160,213,11265,NULL,0,13,0,0),(14,'2c068484-573c-430c-966e-80ec29961784','http://localhost:9000/mediums/2c068484-573c-430c-966e-80ec29961784',160,213,10460,NULL,0,14,0,0),(15,'326bec45-d499-4a4a-8310-a2917fda015a','http://localhost:9000/mediums/326bec45-d499-4a4a-8310-a2917fda015a',160,213,11264,NULL,0,15,0,0),(16,'2e7fbc4f-4997-4673-b05e-78850f451cde','http://localhost:9000/mediums/2e7fbc4f-4997-4673-b05e-78850f451cde',160,213,15500,NULL,0,16,0,0),(17,'bbe31bad-6bce-4a9b-ad85-c260b2e33981','http://localhost:9000/mediums/bbe31bad-6bce-4a9b-ad85-c260b2e33981',160,219,8780,NULL,0,17,0,0),(18,'9786be67-a683-450f-823f-9476fb9de554','http://localhost:9000/mediums/9786be67-a683-450f-823f-9476fb9de554',160,211,12099,NULL,0,18,0,0),(19,'b88527fb-c633-4e7e-9c24-bfc52a97e29a','http://localhost:9000/mediums/b88527fb-c633-4e7e-9c24-bfc52a97e29a',160,198,9571,NULL,0,19,0,0),(20,'1198cd38-6deb-4b1b-a525-c76002d82c9b','http://localhost:9000/mediums/1198cd38-6deb-4b1b-a525-c76002d82c9b',160,207,11504,NULL,0,20,0,0),(21,'f6139c44-4aae-4b32-a1f7-ebe2ac3e6626','http://localhost:9000/mediums/f6139c44-4aae-4b32-a1f7-ebe2ac3e6626',160,213,7291,NULL,0,21,0,0),(22,'f4576e45-7c09-43ba-8717-b908f40d2822','http://localhost:9000/mediums/f4576e45-7c09-43ba-8717-b908f40d2822',160,200,6941,NULL,0,22,0,0),(23,'b9bc139d-0872-45fb-bf01-466f8c5d2fbc','http://localhost:9000/mediums/b9bc139d-0872-45fb-bf01-466f8c5d2fbc',160,213,9038,NULL,0,23,0,0),(24,'a2b1a1fe-9688-4a39-b332-b8e0714daad7','http://localhost:9000/mediums/a2b1a1fe-9688-4a39-b332-b8e0714daad7',160,213,8478,NULL,0,24,0,0),(25,'c9642ef2-63b9-4e1b-a492-258adb488213','http://localhost:9000/mediums/c9642ef2-63b9-4e1b-a492-258adb488213',160,213,7271,NULL,0,25,0,0),(26,'c698aad2-e04f-451f-a0f6-9614ce49eca2','http://localhost:9000/mediums/c698aad2-e04f-451f-a0f6-9614ce49eca2',160,219,6479,NULL,0,26,0,0),(27,'fc79d2a6-c521-4d1a-bae9-f1d2db34e651','http://localhost:9000/mediums/fc79d2a6-c521-4d1a-bae9-f1d2db34e651',160,213,11710,NULL,0,27,0,0),(28,'85d08941-9e12-4559-8fc9-a85718c7f956','http://localhost:9000/mediums/85d08941-9e12-4559-8fc9-a85718c7f956',160,215,11323,NULL,0,28,0,0),(29,'167692bf-77a2-476b-b791-376f405e20eb','http://localhost:9000/mediums/167692bf-77a2-476b-b791-376f405e20eb',160,204,5076,NULL,0,29,0,0),(30,'19c531fc-ca48-4f80-823c-e9d0cbc91366','http://localhost:9000/mediums/19c531fc-ca48-4f80-823c-e9d0cbc91366',135,197,34210,NULL,0,30,0,0),(31,'1164ea0f-a08c-4b13-a9b9-b23741e8881d','http://localhost:9000/mediums/1164ea0f-a08c-4b13-a9b9-b23741e8881d',160,214,10468,NULL,0,31,0,0),(32,'cfea254d-260f-4af4-b20d-123f711f4831','http://localhost:9000/mediums/cfea254d-260f-4af4-b20d-123f711f4831',160,193,11366,NULL,0,32,0,0),(33,'d7fb2f45-9087-43fa-a225-c6f04b9a2206','http://localhost:9000/mediums/d7fb2f45-9087-43fa-a225-c6f04b9a2206',149,195,14112,NULL,0,33,0,0),(34,'9c8b7201-813f-4ff9-afce-aacc37d8242c','http://localhost:9000/mediums/9c8b7201-813f-4ff9-afce-aacc37d8242c',126,185,15176,NULL,0,34,0,0),(35,'1b76901f-20c0-4781-93b5-0d1ebeb0dc3f','http://localhost:9000/mediums/1b76901f-20c0-4781-93b5-0d1ebeb0dc3f',160,231,9779,NULL,0,35,0,0),(36,'91265027-3465-47f4-b824-b5188b1a7323','http://localhost:9000/mediums/91265027-3465-47f4-b824-b5188b1a7323',160,213,10802,NULL,0,36,0,0),(37,'5c3f5cd9-c67d-42df-a04f-01100692aac4','http://localhost:9000/mediums/5c3f5cd9-c67d-42df-a04f-01100692aac4',160,200,7364,NULL,0,37,0,0),(38,'de82e58e-56e2-4b12-b4ca-891028c1d932','http://localhost:9000/mediums/de82e58e-56e2-4b12-b4ca-891028c1d932',160,198,6621,NULL,0,38,0,0),(39,'16aec69a-a663-43db-ac51-77db9ad07d5b','http://localhost:9000/mediums/16aec69a-a663-43db-ac51-77db9ad07d5b',160,211,8375,NULL,0,39,0,0),(40,'08785def-8fce-49a4-bc0a-3b9a51996334','http://localhost:9000/mediums/08785def-8fce-49a4-bc0a-3b9a51996334',160,207,8942,NULL,0,40,0,0),(41,'b8d92210-cd08-41ea-bfd9-4162e1bcb99b','http://localhost:9000/mediums/b8d92210-cd08-41ea-bfd9-4162e1bcb99b',160,213,9667,NULL,0,41,0,0),(42,'02beb355-19f6-43ac-9289-9161913e4ecc','http://localhost:9000/mediums/02beb355-19f6-43ac-9289-9161913e4ecc',160,213,11620,NULL,0,42,0,0),(43,'54bab104-fe20-4b99-9cea-81f5f6e66345','http://localhost:9000/mediums/54bab104-fe20-4b99-9cea-81f5f6e66345',160,213,11012,NULL,0,43,0,0),(44,'42babce2-66e6-4681-944d-54f234fb9712','http://localhost:9000/mediums/42babce2-66e6-4681-944d-54f234fb9712',142,201,13812,NULL,0,44,0,0),(45,'ac5b63ab-9daf-4a84-9e58-f46cd5e8360b','http://localhost:9000/mediums/ac5b63ab-9daf-4a84-9e58-f46cd5e8360b',160,212,6955,NULL,0,45,0,0),(46,'8d216c42-6e7e-4cf3-90a0-8c39b3c8d754','http://localhost:9000/mediums/8d216c42-6e7e-4cf3-90a0-8c39b3c8d754',160,212,8517,NULL,0,46,0,0),(47,'74121ad5-fa2e-4e4b-a96e-8e69d3b5869b','http://localhost:9000/mediums/74121ad5-fa2e-4e4b-a96e-8e69d3b5869b',132,179,10872,NULL,0,47,0,0),(48,'325323b0-28cf-467c-bfea-7383fc150d18','http://localhost:9000/mediums/325323b0-28cf-467c-bfea-7383fc150d18',137,185,40279,NULL,0,48,0,0),(49,'45795c3d-e514-4271-b7d2-a224d8dee40b','http://localhost:9000/mediums/45795c3d-e514-4271-b7d2-a224d8dee40b',160,196,13115,NULL,0,49,0,0),(50,'0687b569-0008-4a00-9936-b6ef8bd0e1e2','http://localhost:9000/mediums/0687b569-0008-4a00-9936-b6ef8bd0e1e2',160,213,10657,NULL,0,50,0,0),(51,'abcf6c16-dbf9-4dce-82f9-fd42251b2b6b','http://localhost:9000/mediums/abcf6c16-dbf9-4dce-82f9-fd42251b2b6b',160,213,11514,NULL,0,51,0,0),(52,'6043f026-4b0b-4dcb-87cf-3332a91c1063','http://localhost:9000/mediums/6043f026-4b0b-4dcb-87cf-3332a91c1063',160,208,11127,NULL,0,52,0,0),(53,'c9deba06-5f46-471c-b771-f245da176b8e','http://localhost:9000/mediums/c9deba06-5f46-471c-b771-f245da176b8e',160,196,8235,NULL,0,53,0,0),(54,'524ba2ee-2175-4f51-ab20-6808b0e568ff','http://localhost:9000/mediums/524ba2ee-2175-4f51-ab20-6808b0e568ff',132,189,30215,NULL,0,54,0,0),(55,'f0e3834f-70e2-4d40-ad79-3be1f3e88c6a','http://localhost:9000/mediums/f0e3834f-70e2-4d40-ad79-3be1f3e88c6a',160,213,6824,NULL,0,55,0,0),(56,'acbadbb1-9835-4b93-a3e6-645aea31218d','http://localhost:9000/mediums/acbadbb1-9835-4b93-a3e6-645aea31218d',160,195,9269,NULL,0,56,0,0),(57,'b386d476-13df-4926-8725-e44e024568fd','http://localhost:9000/mediums/b386d476-13df-4926-8725-e44e024568fd',137,191,31719,NULL,0,57,0,0),(58,'602f8695-c0a7-4b4f-9b51-12b8046a1950','http://localhost:9000/mediums/602f8695-c0a7-4b4f-9b51-12b8046a1950',160,203,5640,NULL,0,58,0,0),(59,'e6345f69-e6cb-4163-ac78-6ea84e2feee4','http://localhost:9000/mediums/e6345f69-e6cb-4163-ac78-6ea84e2feee4',160,217,11583,NULL,0,59,0,0),(60,'4742b9b8-7fd2-4723-8ef0-b48aad0900d0','http://localhost:9000/mediums/4742b9b8-7fd2-4723-8ef0-b48aad0900d0',129,187,31413,NULL,0,60,0,0),(61,'04d76fd9-9d05-4c98-8ea2-4da0ec29393e','http://localhost:9000/mediums/04d76fd9-9d05-4c98-8ea2-4da0ec29393e',160,213,8076,NULL,0,61,0,0),(62,'f99c726e-c351-4ba6-b751-745181b900cc','http://localhost:9000/mediums/f99c726e-c351-4ba6-b751-745181b900cc',160,203,15200,NULL,0,62,0,0),(63,'84485f79-8d6c-4561-8a58-8159ce7437a9','http://localhost:9000/mediums/84485f79-8d6c-4561-8a58-8159ce7437a9',160,213,16380,NULL,0,63,0,0),(64,'eb602c84-e587-414e-a388-330d570f3aa0','http://localhost:9000/mediums/eb602c84-e587-414e-a388-330d570f3aa0',139,197,13934,NULL,0,64,0,0),(65,'9391bb1f-4543-4560-bd9a-1adc19da314c','http://localhost:9000/mediums/9391bb1f-4543-4560-bd9a-1adc19da314c',360,509,33140,NULL,0,43,0,0),(66,'c7eb1d3d-5fff-45ee-92d5-fd4d03780724','http://localhost:9000/mediums/c7eb1d3d-5fff-45ee-92d5-fd4d03780724',342,400,91703,NULL,0,1,0,0),(67,'11cae237-9aa9-4030-bf18-1779aeef4d5e','http://localhost:9000/mediums/11cae237-9aa9-4030-bf18-1779aeef4d5e',440,459,53830,NULL,0,1,0,0),(68,'80cf39e9-c219-4ce6-b46a-896607920b15','http://localhost:9000/mediums/80cf39e9-c219-4ce6-b46a-896607920b15',360,509,33140,NULL,0,1,0,0),(69,'1eccb22d-a2fd-4486-9abf-299f697697ab','http://localhost:9000/mediums/1eccb22d-a2fd-4486-9abf-299f697697ab',400,533,84335,NULL,0,1,0,0);
/*!40000 ALTER TABLE `mediums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `mutes`
--

LOCK TABLES `mutes` WRITE;
/*!40000 ALTER TABLE `mutes` DISABLE KEYS */;
/*!40000 ALTER TABLE `mutes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `push_notification_settings`
--

LOCK TABLES `push_notification_settings` WRITE;
/*!40000 ALTER TABLE `push_notification_settings` DISABLE KEYS */;
INSERT INTO `push_notification_settings` VALUES (1,1,1,1,1,1,1),(2,1,1,1,1,1,1),(3,1,1,1,1,1,1),(4,1,1,1,1,1,1),(5,1,1,1,1,1,1),(6,1,1,1,1,1,1),(7,1,1,1,1,1,1),(8,1,1,1,1,1,1),(9,1,1,1,1,1,1),(10,1,1,1,1,1,1),(11,1,1,1,1,1,1),(12,1,1,1,1,1,1),(13,1,1,1,1,1,1),(14,1,1,1,1,1,1),(15,1,1,1,1,1,1),(16,1,1,1,1,1,1),(17,1,1,1,1,1,1),(18,1,1,1,1,1,1),(19,1,1,1,1,1,1),(20,1,1,1,1,1,1),(21,1,1,1,1,1,1),(22,1,1,1,1,1,1),(23,1,1,1,1,1,1),(24,1,1,1,1,1,1),(25,1,1,1,1,1,1),(26,1,1,1,1,1,1),(27,1,1,1,1,1,1),(28,1,1,1,1,1,1),(29,1,1,1,1,1,1),(30,1,1,1,1,1,1),(31,1,1,1,1,1,1),(32,1,1,1,1,1,1),(33,1,1,1,1,1,1),(34,1,1,1,1,1,1),(35,1,1,1,1,1,1),(36,1,1,1,1,1,1),(37,1,1,1,1,1,1),(38,1,1,1,1,1,1),(39,1,1,1,1,1,1),(40,1,1,1,1,1,1),(41,1,1,1,1,1,1),(42,1,1,1,1,1,1),(43,1,1,1,1,1,1),(44,1,1,1,1,1,1),(45,1,1,1,1,1,1),(46,1,1,1,1,1,1),(47,1,1,1,1,1,1),(48,1,1,1,1,1,1),(49,1,1,1,1,1,1),(50,1,1,1,1,1,1),(51,1,1,1,1,1,1),(52,1,1,1,1,1,1),(53,1,1,1,1,1,1),(54,1,1,1,1,1,1),(55,1,1,1,1,1,1),(56,1,1,1,1,1,1),(57,1,1,1,1,1,1),(58,1,1,1,1,1,1),(59,1,1,1,1,1,1),(60,1,1,1,1,1,1),(61,1,1,1,1,1,1),(62,1,1,1,1,1,1),(63,1,1,1,1,1,1),(64,1,1,1,1,1,1);
/*!40000 ALTER TABLE `push_notification_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `relationships`
--

LOCK TABLES `relationships` WRITE;
/*!40000 ALTER TABLE `relationships` DISABLE KEYS */;
INSERT INTO `relationships` VALUES (1,43,NULL,1,0,0,1,0,0),(1,59,NULL,1,0,0,0,0,0),(1,60,NULL,1,0,0,0,0,0),(1,61,NULL,1,0,0,0,0,0),(1,62,NULL,1,0,0,0,0,0),(1,63,NULL,1,0,0,0,0,0),(1,64,NULL,1,0,0,0,0,0),(2,43,NULL,0,0,0,1,0,0),(3,43,NULL,0,0,0,1,0,0),(4,43,NULL,0,0,0,1,0,0),(5,43,NULL,0,0,0,1,0,0),(6,43,NULL,0,0,0,1,0,0),(7,43,NULL,0,0,0,1,0,0),(8,43,NULL,0,0,0,1,0,0),(9,43,NULL,0,0,0,1,0,0),(10,43,NULL,0,0,0,1,0,0),(11,43,NULL,0,0,0,1,0,0),(12,43,NULL,0,0,0,1,0,0),(13,43,NULL,0,0,0,1,0,0),(14,43,NULL,0,0,0,1,0,0),(15,43,NULL,0,0,0,1,0,0),(16,43,NULL,0,0,0,1,0,0),(17,43,NULL,0,0,0,1,0,0),(18,43,NULL,0,0,0,1,0,0),(19,43,NULL,0,0,0,1,0,0),(20,43,NULL,0,0,0,1,0,0),(21,43,NULL,0,0,0,1,0,0),(22,43,NULL,0,0,0,1,0,0),(23,43,NULL,0,0,0,1,0,0),(24,43,NULL,0,0,0,1,0,0),(25,43,NULL,0,0,0,1,0,0),(26,43,NULL,0,0,0,1,0,0),(27,43,NULL,0,0,0,1,0,0),(28,43,NULL,0,0,0,1,0,0),(29,43,NULL,0,0,0,1,0,0),(30,43,NULL,0,0,0,1,0,0),(31,43,NULL,0,0,0,1,0,0),(32,43,NULL,0,0,0,1,0,0),(33,43,NULL,0,0,0,1,0,0),(34,43,NULL,0,0,0,1,0,0),(35,43,NULL,0,0,0,1,0,0),(36,43,NULL,0,0,0,1,0,0),(37,43,NULL,0,0,0,1,0,0),(38,43,NULL,0,0,0,1,0,0),(39,43,NULL,0,0,0,1,0,0),(40,43,NULL,0,0,0,1,0,0),(41,43,NULL,0,0,0,1,0,0),(42,43,NULL,0,0,0,1,0,0),(43,1,NULL,1,0,0,1,0,0),(43,2,NULL,1,0,0,0,0,0),(43,3,NULL,1,0,0,0,0,0),(43,4,NULL,1,0,0,0,0,0),(43,5,NULL,1,0,0,0,0,0),(43,6,NULL,1,0,0,0,0,0),(43,7,NULL,1,0,0,0,0,0),(43,8,NULL,1,0,0,0,0,0),(43,9,NULL,1,0,0,0,0,0),(43,10,NULL,1,0,0,0,0,0),(43,11,NULL,1,0,0,0,0,0),(43,12,NULL,1,0,0,0,0,0),(43,13,NULL,1,0,0,0,0,0),(43,14,NULL,1,0,0,0,0,0),(43,15,NULL,1,0,0,0,0,0),(43,16,NULL,1,0,0,0,0,0),(43,17,NULL,1,0,0,0,0,0),(43,18,NULL,1,0,0,0,0,0),(43,19,NULL,1,0,0,0,0,0),(43,20,NULL,1,0,0,0,0,0),(43,21,NULL,1,0,0,0,0,0),(43,22,NULL,1,0,0,0,0,0),(43,23,NULL,1,0,0,0,0,0),(43,24,NULL,1,0,0,0,0,0),(43,25,NULL,1,0,0,0,0,0),(43,26,NULL,1,0,0,0,0,0),(43,27,NULL,1,0,0,0,0,0),(43,28,NULL,1,0,0,0,0,0),(43,29,NULL,1,0,0,0,0,0),(43,30,NULL,1,0,0,0,0,0),(43,31,NULL,1,0,0,0,0,0),(43,32,NULL,1,0,0,0,0,0),(43,33,NULL,1,0,0,0,0,0),(43,34,NULL,1,0,0,0,0,0),(43,35,NULL,1,0,0,0,0,0),(43,36,NULL,1,0,0,0,0,0),(43,37,NULL,1,0,0,0,0,0),(43,38,NULL,1,0,0,0,0,0),(43,39,NULL,1,0,0,0,0,0),(43,40,NULL,1,0,0,0,0,0),(43,41,NULL,1,0,0,0,0,0),(43,42,NULL,1,0,0,0,0,0),(43,44,NULL,1,0,0,0,0,0),(43,45,NULL,1,0,0,0,0,0),(43,46,NULL,1,0,0,0,0,0),(43,47,NULL,1,0,0,0,0,0),(43,48,NULL,1,0,0,0,0,0),(43,49,NULL,1,0,0,0,0,0),(43,50,NULL,1,0,0,0,0,0),(43,51,NULL,1,0,0,0,0,0),(43,52,NULL,1,0,0,0,0,0),(43,53,NULL,1,0,0,0,0,0),(43,54,NULL,1,0,0,0,0,0),(43,55,NULL,1,0,0,0,0,0),(43,56,NULL,1,0,0,0,0,0),(43,57,NULL,1,0,0,0,0,0),(43,58,NULL,1,0,0,0,0,0),(43,59,NULL,1,0,0,0,0,0),(43,60,NULL,1,0,0,0,0,0),(43,61,NULL,1,0,0,0,0,0),(43,62,NULL,1,0,0,0,0,0),(43,63,NULL,1,0,0,0,0,0),(43,64,NULL,1,0,0,0,0,0),(44,43,NULL,0,0,0,1,0,0),(45,43,NULL,0,0,0,1,0,0),(46,43,NULL,0,0,0,1,0,0),(47,43,NULL,0,0,0,1,0,0),(48,43,NULL,0,0,0,1,0,0),(49,43,NULL,0,0,0,1,0,0),(50,43,NULL,0,0,0,1,0,0),(51,43,NULL,0,0,0,1,0,0),(52,43,NULL,0,0,0,1,0,0),(53,43,NULL,0,0,0,1,0,0),(54,43,NULL,0,0,0,1,0,0),(55,43,NULL,0,0,0,1,0,0),(56,43,NULL,0,0,0,1,0,0),(57,43,NULL,0,0,0,1,0,0),(58,43,NULL,0,0,0,1,0,0),(59,1,NULL,0,0,0,1,0,0),(59,43,NULL,0,0,0,1,0,0),(60,1,NULL,0,0,0,1,0,0),(60,43,NULL,0,0,0,1,0,0),(61,1,NULL,0,0,0,1,0,0),(61,43,NULL,0,0,0,1,0,0),(62,1,NULL,0,0,0,1,0,0),(62,43,NULL,0,0,0,1,0,0),(63,1,NULL,0,0,0,1,0,0),(63,43,NULL,0,0,0,1,0,0),(64,1,NULL,0,0,0,1,0,0),(64,43,NULL,0,0,0,1,0,0);
/*!40000 ALTER TABLE `relationships` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-12-08 13:01:32
