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
INSERT INTO `accounts` VALUES (1,'aritomo_yamagata','aritomo_yamagata','f737bd52007ca0dc9abb86b5567e8372',1,1,'http://localhost:9000/mediums/aritomo_yamagata.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(2,'eisaku_sato','eisaku_sato','f737bd52007ca0dc9abb86b5567e8372',1,2,'http://localhost:9000/mediums/eisaku_sato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(3,'giichi_tanaka','giichi_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,3,'http://localhost:9000/mediums/giichi_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(4,'gonbei_yamamoto','gonbei_yamamoto','f737bd52007ca0dc9abb86b5567e8372',1,4,'http://localhost:9000/mediums/gonbei_yamamoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(5,'hayato_ikeda','hayato_ikeda','f737bd52007ca0dc9abb86b5567e8372',1,5,'http://localhost:9000/mediums/hayato_ikeda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(6,'hideki_tojo','hideki_tojo','f737bd52007ca0dc9abb86b5567e8372',1,6,'http://localhost:9000/mediums/hideki_tojo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(7,'hirobumi_ito','hirobumi_ito','f737bd52007ca0dc9abb86b5567e8372',1,7,'http://localhost:9000/mediums/hirobumi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(8,'hitoshi_ashida','hitoshi_ashida','f737bd52007ca0dc9abb86b5567e8372',1,8,'http://localhost:9000/mediums/hitoshi_ashida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(9,'ichiro_hatoyama','ichiro_hatoyama','f737bd52007ca0dc9abb86b5567e8372',1,9,'http://localhost:9000/mediums/ichiro_hatoyama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(10,'junichiro_koizumi','junichiro_koizumi','f737bd52007ca0dc9abb86b5567e8372',1,10,'http://localhost:9000/mediums/junichiro_koizumi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(11,'kakuei_tanaka','kakuei_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,11,'http://localhost:9000/mediums/kakuei_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(12,'kantaro_suzuki','kantaro_suzuki','f737bd52007ca0dc9abb86b5567e8372',1,12,'http://localhost:9000/mediums/kantaro_suzuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(13,'keigo_kiyoura','keigo_kiyoura','f737bd52007ca0dc9abb86b5567e8372',1,13,'http://localhost:9000/mediums/keigo_kiyoura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(14,'keisuke_okada','keisuke_okada','f737bd52007ca0dc9abb86b5567e8372',1,14,'http://localhost:9000/mediums/keisuke_okada.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(15,'keizo_obuchi','keizo_obuchi','f737bd52007ca0dc9abb86b5567e8372',1,15,'http://localhost:9000/mediums/keizo_obuchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(16,'kiichi_miyazawa','kiichi_miyazawa','f737bd52007ca0dc9abb86b5567e8372',1,16,'http://localhost:9000/mediums/kiichi_miyazawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(17,'kiichiro_hiranumra','kiichiro_hiranumra','f737bd52007ca0dc9abb86b5567e8372',1,17,'http://localhost:9000/mediums/kiichiro_hiranumra.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(18,'kijuro_shidehara','kijuro_shidehara','f737bd52007ca0dc9abb86b5567e8372',1,18,'http://localhost:9000/mediums/kijuro_shidehara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(19,'kinmochi_saionji','kinmochi_saionji','f737bd52007ca0dc9abb86b5567e8372',1,19,'http://localhost:9000/mediums/kinmochi_saionji.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(20,'kiyotaka_kuroda','kiyotaka_kuroda','f737bd52007ca0dc9abb86b5567e8372',1,20,'http://localhost:9000/mediums/kiyotaka_kuroda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(21,'kohki_hirota','kohki_hirota','f737bd52007ca0dc9abb86b5567e8372',1,21,'http://localhost:9000/mediums/kohki_hirota.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(22,'korekiyo_takahashi','korekiyo_takahashi','f737bd52007ca0dc9abb86b5567e8372',1,22,'http://localhost:9000/mediums/korekiyo_takahashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(23,'kosai_uchida','kosai_uchida','f737bd52007ca0dc9abb86b5567e8372',1,23,'http://localhost:9000/mediums/kosai_uchida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(24,'kuniaki_koiso','kuniaki_koiso','f737bd52007ca0dc9abb86b5567e8372',1,24,'http://localhost:9000/mediums/kuniaki_koiso.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(25,'makoto_saito','makoto_saito','f737bd52007ca0dc9abb86b5567e8372',1,25,'http://localhost:9000/mediums/makoto_saito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(26,'masayoshi_ito','masayoshi_ito','f737bd52007ca0dc9abb86b5567e8372',1,26,'http://localhost:9000/mediums/masayoshi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(27,'masayoshi_matsutaka','masayoshi_matsutaka','f737bd52007ca0dc9abb86b5567e8372',1,27,'http://localhost:9000/mediums/masayoshi_matsutaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(28,'masayoshi_ohira','masayoshi_ohira','f737bd52007ca0dc9abb86b5567e8372',1,28,'http://localhost:9000/mediums/masayoshi_ohira.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(29,'mitsumasa_yonai','mitsumasa_yonai','f737bd52007ca0dc9abb86b5567e8372',1,29,'http://localhost:9000/mediums/mitsumasa_yonai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(30,'morihiro_hosokawa','morihiro_hosokawa','f737bd52007ca0dc9abb86b5567e8372',1,30,'http://localhost:9000/mediums/morihiro_hosokawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(31,'naoto_kan','naoto_kan','f737bd52007ca0dc9abb86b5567e8372',1,31,'http://localhost:9000/mediums/naoto_kan.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(32,'naruhiko_higashikuni','naruhiko_higashikuni','f737bd52007ca0dc9abb86b5567e8372',1,32,'http://localhost:9000/mediums/naruhiko_higashikuni.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(33,'noboru_takeshita','noboru_takeshita','f737bd52007ca0dc9abb86b5567e8372',1,33,'http://localhost:9000/mediums/noboru_takeshita.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(34,'nobusuke_kishi','nobusuke_kishi','f737bd52007ca0dc9abb86b5567e8372',1,34,'http://localhost:9000/mediums/nobusuke_kishi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(35,'nobuyuki_abe','nobuyuki_abe','f737bd52007ca0dc9abb86b5567e8372',1,35,'http://localhost:9000/mediums/nobuyuki_abe.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(36,'okuma_shigenobu','okuma_shigenobu','f737bd52007ca0dc9abb86b5567e8372',1,36,'http://localhost:9000/mediums/okuma_shigenobu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(37,'osachi_hamaguchi','osachi_hamaguchi','f737bd52007ca0dc9abb86b5567e8372',1,37,'http://localhost:9000/mediums/osachi_hamaguchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(38,'reijiro_wakatsuki','reijiro_wakatsuki','f737bd52007ca0dc9abb86b5567e8372',1,38,'http://localhost:9000/mediums/reijiro_wakatsuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(39,'ryutaro_hashimoto','ryutaro_hashimoto','f737bd52007ca0dc9abb86b5567e8372',1,39,'http://localhost:9000/mediums/ryutaro_hashimoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(40,'sanetomi_sanjo','sanetomi_sanjo','f737bd52007ca0dc9abb86b5567e8372',1,40,'http://localhost:9000/mediums/sanetomi_sanjo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(41,'senjuro_hayashi','senjuro_hayashi','f737bd52007ca0dc9abb86b5567e8372',1,41,'http://localhost:9000/mediums/senjuro_hayashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(42,'shigeru_yoshida','shigeru_yoshida','f737bd52007ca0dc9abb86b5567e8372',1,42,'http://localhost:9000/mediums/shigeru_yoshida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(43,'shinzo_abe','shinzo_abe','f737bd52007ca0dc9abb86b5567e8372',0,43,'http://localhost:9000/mediums/shinzo_abe.jpg',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(44,'sosuke_uno','sosuke_uno','f737bd52007ca0dc9abb86b5567e8372',1,44,'http://localhost:9000/mediums/sosuke_uno.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(45,'takaaki_kato','takaaki_kato','f737bd52007ca0dc9abb86b5567e8372',1,45,'http://localhost:9000/mediums/takaaki_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(46,'takashi_hara','takashi_hara','f737bd52007ca0dc9abb86b5567e8372',1,46,'http://localhost:9000/mediums/takashi_hara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(47,'takeo_fukuda','takeo_fukuda','f737bd52007ca0dc9abb86b5567e8372',1,47,'http://localhost:9000/mediums/takeo_fukuda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(48,'takeo_miki','takeo_miki','f737bd52007ca0dc9abb86b5567e8372',1,48,'http://localhost:9000/mediums/takeo_miki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(49,'tanzan_ishibashi','tanzan_ishibashi','f737bd52007ca0dc9abb86b5567e8372',1,49,'http://localhost:9000/mediums/tanzan_ishibashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(50,'taro_aso','taro_aso','f737bd52007ca0dc9abb86b5567e8372',1,50,'http://localhost:9000/mediums/taro_aso.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(51,'taro_katsura','taro_katsura','f737bd52007ca0dc9abb86b5567e8372',1,51,'http://localhost:9000/mediums/taro_katsura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(52,'terauchi_masatake','terauchi_masatake','f737bd52007ca0dc9abb86b5567e8372',1,52,'http://localhost:9000/mediums/terauchi_masatake.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(53,'tetsu_katayama','tetsu_katayama','f737bd52007ca0dc9abb86b5567e8372',1,53,'http://localhost:9000/mediums/tetsu_katayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(54,'tomiichi_murayama','tomiichi_murayama','f737bd52007ca0dc9abb86b5567e8372',1,54,'http://localhost:9000/mediums/tomiichi_murayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(55,'tomosaburo_kato','tomosaburo_kato','f737bd52007ca0dc9abb86b5567e8372',1,55,'http://localhost:9000/mediums/tomosaburo_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(56,'toshiki_kaifu','toshiki_kaifu','f737bd52007ca0dc9abb86b5567e8372',1,56,'http://localhost:9000/mediums/toshiki_kaifu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(57,'tsutomu_hata','tsutomu_hata','f737bd52007ca0dc9abb86b5567e8372',1,57,'http://localhost:9000/mediums/tsutomu_hata.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(58,'tsuyoshi_inukai','tsuyoshi_inukai','f737bd52007ca0dc9abb86b5567e8372',1,58,'http://localhost:9000/mediums/tsuyoshi_inukai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(59,'yasuhiro_nakasone','yasuhiro_nakasone','f737bd52007ca0dc9abb86b5567e8372',1,59,'http://localhost:9000/mediums/yasuhiro_nakasone.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(60,'yasuo_fukuda','yasuo_fukuda','f737bd52007ca0dc9abb86b5567e8372',1,60,'http://localhost:9000/mediums/yasuo_fukuda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(61,'yoshihiko_noda','yoshihiko_noda','f737bd52007ca0dc9abb86b5567e8372',1,61,'http://localhost:9000/mediums/yoshihiko_noda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(62,'yoshiro_mori','yoshiro_mori','f737bd52007ca0dc9abb86b5567e8372',1,62,'http://localhost:9000/mediums/yoshiro_mori.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(63,'yukio_hatoyama','yukio_hatoyama','f737bd52007ca0dc9abb86b5567e8372',1,63,'http://localhost:9000/mediums/yukio_hatoyama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(64,'zenko_suzuki','zenko_suzuki','f737bd52007ca0dc9abb86b5567e8372',1,64,'http://localhost:9000/mediums/zenko_suzuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL);
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
INSERT INTO `devices` VALUES (1,1,'d06c2b0b-10f5-4afc-acb6-6ba48ae958ee',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(2,2,'dc37b13a-78cb-43a5-9889-fa464dbb3c8c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(3,3,'83597bee-79d8-4a5a-834b-c0731ddf07f5',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(4,4,'850c6ae1-a5f9-4e6e-95b2-004649949ef2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(5,5,'761912f6-2bbc-4542-8a1c-c5ff34069028',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(6,6,'644b190d-824e-49c8-a123-299602ab66f6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(7,7,'88c8917e-6b25-41c0-bff6-7b43a40d5620',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(8,8,'21cc4ce7-e399-429e-8731-a6c5bb922989',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(9,9,'f30770b1-9a26-4b9b-b814-2e861aec8db5',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(10,10,'8e04dabc-a4df-46ec-951d-d98ebe24b926',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(11,11,'4bceef64-a069-42dc-bf6f-51f18fe92474',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(12,12,'f7bcdaab-eb9b-4583-848c-ee588d034aff',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(13,13,'37668b11-990b-4fff-8adc-9370b3b575af',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(14,14,'b0bd5622-8dda-42a1-8c74-1a9326e379df',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(15,15,'e5f606d7-50d8-4a10-abef-e66d3fc4a195',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(16,16,'1982f4b3-11d4-492e-8a24-4dbb3a930449',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(17,17,'680a0809-4911-433e-86e0-54282797bd3e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(18,18,'73a86eca-dd90-44bc-a3bb-dd2b58f878e1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(19,19,'6b7aa41c-9cc8-4692-adc8-278fd660b385',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(20,20,'f83a4718-0499-4398-9f7f-413d578c8907',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(21,21,'47200a43-3c6c-4ab8-96b5-270bf23735ca',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(22,22,'6db02d79-b3ba-4b2b-a21a-a4db2a68b23d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(23,23,'e25a95c7-1df6-4e9f-bbf8-45162966103f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(24,24,'54eb829f-1efc-412b-8e89-8e7effe39bbc',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(25,25,'39d00e1a-7d72-452d-a8c6-aaf873a446fd',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(26,26,'d55d8492-d6cf-4a34-8110-d1ff1a544100',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(27,27,'009bc6b5-cb34-442b-a354-ca1b732ad721',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(28,28,'ca75d067-ab7f-468a-8332-48cd54dab04c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(29,29,'55e04c95-dc4c-4b06-85b8-242a572aab06',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(30,30,'94b61a0f-074b-4bb1-9b0f-d945fd4133b3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(31,31,'0258e9c9-13ac-47be-af33-6cd22414d2f4',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(32,32,'b7e59af4-528f-4af9-a664-90c13c68a00d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(33,33,'2abec716-5919-4926-8607-2bd011bc217a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(34,34,'6522e968-08d5-4b9c-af20-08ce1e9ba07d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(35,35,'1eb458ef-3c23-4714-a003-2933ab7fff1d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(36,36,'30f0ba4b-de88-4008-8dcf-748faf7e5160',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(37,37,'55b4ddac-8544-42a4-8894-8f53e0974607',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(38,38,'70663f55-02ea-4a7f-8e91-a9d652507750',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(39,39,'4975e3f6-3a3c-45e8-9ea9-e53b7e957aae',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(40,40,'6a9d661f-c801-4bac-83d5-cad9df35eafe',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(41,41,'415d8c6e-07ef-4f22-96d6-c36053d7705f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(42,42,'7271826b-f11f-48d6-91d3-fcfc1290ed71',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(43,43,'d7a15ec9-087f-4645-913c-9d903b3e296e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(44,44,'941317f2-bb74-415a-9d9d-4c4530fdf448',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(45,45,'34f80569-551d-4176-98b2-2f777c7e46e0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(46,46,'f65b5b45-2fd6-4a0a-a40b-a8dc9a3ec3e8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(47,47,'4262d1dd-9ada-4508-a08c-220daa134496',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(48,48,'be0a7d3f-7cd9-4c13-b033-c197633f5e9e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(49,49,'79d9faef-3283-4b9f-9d98-a8df1adaf099',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(50,50,'f4b1e0c4-0ac9-47b8-8640-e7266316e9bf',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(51,51,'6f620dc1-0d16-48b8-ba3b-f62cb0c820a3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(52,52,'6be0d978-505a-41f6-ae89-21b05a8a1a6c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(53,53,'f2ccb3e9-b342-4be7-92ed-446d99e95ce6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(54,54,'a23ca3a8-705f-4613-9ad5-7dcac1a7235c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(55,55,'d5b22ebe-8abe-4c81-9bc0-ef72128bd366',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(56,56,'673e426d-147b-48c4-a00d-018d558a3707',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(57,57,'8a3d90f5-81f7-49df-8338-a6e26c50dcec',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(58,58,'ee9cd702-cf34-4884-a362-1f7f720c25ab',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(59,59,'468a69af-8c61-4aac-973f-c15f51e2bbf9',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(60,60,'bc38ad61-c1eb-4c27-a19a-ed5a08fa07ad',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(61,61,'c8f0f16a-17d0-4795-a232-fe2bd3f4399e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(62,62,'521e3598-5b18-4abd-8a09-94048b0c6038',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(63,63,'8da045d1-578b-456d-a190-26691f2d0bcc',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(64,64,'e7d9c5ab-87b4-4fa9-b116-16bfc6edb584',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1');
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
/*!40000 ALTER TABLE `feed_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feeds`
--

LOCK TABLES `feeds` WRITE;
/*!40000 ALTER TABLE `feeds` DISABLE KEYS */;
/*!40000 ALTER TABLE `feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `followers`
--

LOCK TABLES `followers` WRITE;
/*!40000 ALTER TABLE `followers` DISABLE KEYS */;
INSERT INTO `followers` VALUES (1,1,43,1544195544595),(2,2,43,1544195544650),(3,3,43,1544195544683),(4,4,43,1544195544708),(5,5,43,1544195544730),(6,6,43,1544195544755),(7,7,43,1544195544833),(8,8,43,1544195544859),(9,9,43,1544195544884),(10,10,43,1544195544910),(11,11,43,1544195544932),(12,12,43,1544195544957),(13,13,43,1544195544979),(14,14,43,1544195545004),(15,15,43,1544195545027),(16,16,43,1544195545056),(17,17,43,1544195545085),(18,18,43,1544195545108),(19,19,43,1544195545132),(20,20,43,1544195545161),(21,21,43,1544195545186),(22,22,43,1544195545212),(23,23,43,1544195545241),(24,24,43,1544195545265),(25,25,43,1544195545290),(26,26,43,1544195545316),(27,27,43,1544195545340),(28,28,43,1544195545365),(29,29,43,1544195545395),(30,30,43,1544195545426),(31,31,43,1544195545457),(32,32,43,1544195545489),(33,33,43,1544195545517),(34,34,43,1544195545543),(35,35,43,1544195545572),(36,36,43,1544195545597),(37,37,43,1544195545621),(38,38,43,1544195545645),(39,39,43,1544195545668),(40,40,43,1544195545691),(41,41,43,1544195545714),(42,42,43,1544195545750),(43,44,43,1544195545790),(44,45,43,1544195545822),(45,46,43,1544195545846),(46,47,43,1544195545875),(47,48,43,1544195545901),(48,49,43,1544195545931),(49,50,43,1544195545956),(50,51,43,1544195545981),(51,52,43,1544195546012),(52,53,43,1544195546038),(53,54,43,1544195546066),(54,55,43,1544195546099),(55,56,43,1544195546143),(56,57,43,1544195546182),(57,58,43,1544195546215),(58,59,43,1544195546250),(59,60,43,1544195546288),(60,61,43,1544195546327),(61,62,43,1544195546363),(62,63,43,1544195546400),(63,64,43,1544195546433);
/*!40000 ALTER TABLE `followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `follows`
--

LOCK TABLES `follows` WRITE;
/*!40000 ALTER TABLE `follows` DISABLE KEYS */;
INSERT INTO `follows` VALUES (1,43,1,1544195544573),(2,43,2,1544195544640),(3,43,3,1544195544678),(4,43,4,1544195544703),(5,43,5,1544195544727),(6,43,6,1544195544750),(7,43,7,1544195544829),(8,43,8,1544195544855),(9,43,9,1544195544879),(10,43,10,1544195544905),(11,43,11,1544195544929),(12,43,12,1544195544953),(13,43,13,1544195544975),(14,43,14,1544195545000),(15,43,15,1544195545023),(16,43,16,1544195545050),(17,43,17,1544195545081),(18,43,18,1544195545104),(19,43,19,1544195545128),(20,43,20,1544195545156),(21,43,21,1544195545182),(22,43,22,1544195545207),(23,43,23,1544195545237),(24,43,24,1544195545261),(25,43,25,1544195545286),(26,43,26,1544195545312),(27,43,27,1544195545336),(28,43,28,1544195545360),(29,43,29,1544195545389),(30,43,30,1544195545420),(31,43,31,1544195545452),(32,43,32,1544195545484),(33,43,33,1544195545513),(34,43,34,1544195545538),(35,43,35,1544195545566),(36,43,36,1544195545594),(37,43,37,1544195545617),(38,43,38,1544195545641),(39,43,39,1544195545664),(40,43,40,1544195545688),(41,43,41,1544195545710),(42,43,42,1544195545741),(43,43,44,1544195545784),(44,43,45,1544195545818),(45,43,46,1544195545841),(46,43,47,1544195545871),(47,43,48,1544195545897),(48,43,49,1544195545926),(49,43,50,1544195545953),(50,43,51,1544195545976),(51,43,52,1544195546007),(52,43,53,1544195546034),(53,43,54,1544195546062),(54,43,55,1544195546093),(55,43,56,1544195546129),(56,43,57,1544195546177),(57,43,58,1544195546209),(58,43,59,1544195546245),(59,43,60,1544195546282),(60,43,61,1544195546320),(61,43,62,1544195546357),(62,43,63,1544195546394),(63,43,64,1544195546427);
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
INSERT INTO `mediums` VALUES (1,'aritomo_yamagata.jpg','http://localhost:9000/mediums/aritomo_yamagata.jpg',160,213,13192,NULL,0,1,0,0),(2,'eisaku_sato.jpg','http://localhost:9000/mediums/eisaku_sato.jpg',160,227,10510,NULL,0,2,0,0),(3,'giichi_tanaka.jpg','http://localhost:9000/mediums/giichi_tanaka.jpg',160,198,7181,NULL,0,3,0,0),(4,'gonbei_yamamoto.jpg','http://localhost:9000/mediums/gonbei_yamamoto.jpg',160,210,9566,NULL,0,4,0,0),(5,'hayato_ikeda.jpg','http://localhost:9000/mediums/hayato_ikeda.jpg',160,213,5901,NULL,0,5,0,0),(6,'hideki_tojo.jpg','http://localhost:9000/mediums/hideki_tojo.jpg',160,215,7435,NULL,0,6,0,0),(7,'hirobumi_ito.jpg','http://localhost:9000/mediums/hirobumi_ito.jpg',160,218,9141,NULL,0,7,0,0),(8,'hitoshi_ashida.jpg','http://localhost:9000/mediums/hitoshi_ashida.jpg',160,194,12280,NULL,0,8,0,0),(9,'ichiro_hatoyama.jpg','http://localhost:9000/mediums/ichiro_hatoyama.jpg',160,195,9804,NULL,0,9,0,0),(10,'junichiro_koizumi.jpg','http://localhost:9000/mediums/junichiro_koizumi.jpg',131,175,53624,NULL,0,10,0,0),(11,'kakuei_tanaka.jpg','http://localhost:9000/mediums/kakuei_tanaka.jpg',148,183,34578,NULL,0,11,0,0),(12,'kantaro_suzuki.jpg','http://localhost:9000/mediums/kantaro_suzuki.jpg',160,201,5381,NULL,0,12,0,0),(13,'keigo_kiyoura.jpg','http://localhost:9000/mediums/keigo_kiyoura.jpg',160,213,11265,NULL,0,13,0,0),(14,'keisuke_okada.jpg','http://localhost:9000/mediums/keisuke_okada.jpg',160,213,10460,NULL,0,14,0,0),(15,'keizo_obuchi.jpg','http://localhost:9000/mediums/keizo_obuchi.jpg',160,213,11264,NULL,0,15,0,0),(16,'kiichi_miyazawa.jpg','http://localhost:9000/mediums/kiichi_miyazawa.jpg',160,213,15500,NULL,0,16,0,0),(17,'kiichiro_hiranumra.jpg','http://localhost:9000/mediums/kiichiro_hiranumra.jpg',160,219,8780,NULL,0,17,0,0),(18,'kijuro_shidehara.jpg','http://localhost:9000/mediums/kijuro_shidehara.jpg',160,211,12099,NULL,0,18,0,0),(19,'kinmochi_saionji.jpg','http://localhost:9000/mediums/kinmochi_saionji.jpg',160,198,9571,NULL,0,19,0,0),(20,'kiyotaka_kuroda.jpg','http://localhost:9000/mediums/kiyotaka_kuroda.jpg',160,207,11504,NULL,0,20,0,0),(21,'kohki_hirota.jpg','http://localhost:9000/mediums/kohki_hirota.jpg',160,213,7291,NULL,0,21,0,0),(22,'korekiyo_takahashi.jpg','http://localhost:9000/mediums/korekiyo_takahashi.jpg',160,200,6941,NULL,0,22,0,0),(23,'kosai_uchida.jpg','http://localhost:9000/mediums/kosai_uchida.jpg',160,213,9038,NULL,0,23,0,0),(24,'kuniaki_koiso.jpg','http://localhost:9000/mediums/kuniaki_koiso.jpg',160,213,8478,NULL,0,24,0,0),(25,'makoto_saito.jpg','http://localhost:9000/mediums/makoto_saito.jpg',160,213,7271,NULL,0,25,0,0),(26,'masayoshi_ito.jpg','http://localhost:9000/mediums/masayoshi_ito.jpg',160,219,6479,NULL,0,26,0,0),(27,'masayoshi_matsutaka.jpg','http://localhost:9000/mediums/masayoshi_matsutaka.jpg',160,213,11710,NULL,0,27,0,0),(28,'masayoshi_ohira.jpg','http://localhost:9000/mediums/masayoshi_ohira.jpg',160,215,11323,NULL,0,28,0,0),(29,'mitsumasa_yonai.jpg','http://localhost:9000/mediums/mitsumasa_yonai.jpg',160,204,5076,NULL,0,29,0,0),(30,'morihiro_hosokawa.jpg','http://localhost:9000/mediums/morihiro_hosokawa.jpg',135,197,34210,NULL,0,30,0,0),(31,'naoto_kan.jpg','http://localhost:9000/mediums/naoto_kan.jpg',160,214,10468,NULL,0,31,0,0),(32,'naruhiko_higashikuni.jpg','http://localhost:9000/mediums/naruhiko_higashikuni.jpg',160,193,11366,NULL,0,32,0,0),(33,'noboru_takeshita.jpg','http://localhost:9000/mediums/noboru_takeshita.jpg',149,195,14112,NULL,0,33,0,0),(34,'nobusuke_kishi.jpg','http://localhost:9000/mediums/nobusuke_kishi.jpg',126,185,15176,NULL,0,34,0,0),(35,'nobuyuki_abe.jpg','http://localhost:9000/mediums/nobuyuki_abe.jpg',160,231,9779,NULL,0,35,0,0),(36,'okuma_shigenobu.jpg','http://localhost:9000/mediums/okuma_shigenobu.jpg',160,213,10802,NULL,0,36,0,0),(37,'osachi_hamaguchi.jpg','http://localhost:9000/mediums/osachi_hamaguchi.jpg',160,200,7364,NULL,0,37,0,0),(38,'reijiro_wakatsuki.jpg','http://localhost:9000/mediums/reijiro_wakatsuki.jpg',160,198,6621,NULL,0,38,0,0),(39,'ryutaro_hashimoto.jpg','http://localhost:9000/mediums/ryutaro_hashimoto.jpg',160,211,8375,NULL,0,39,0,0),(40,'sanetomi_sanjo.jpg','http://localhost:9000/mediums/sanetomi_sanjo.jpg',160,207,8942,NULL,0,40,0,0),(41,'senjuro_hayashi.jpg','http://localhost:9000/mediums/senjuro_hayashi.jpg',160,213,9667,NULL,0,41,0,0),(42,'shigeru_yoshida.jpg','http://localhost:9000/mediums/shigeru_yoshida.jpg',160,213,11620,NULL,0,42,0,0),(43,'shinzo_abe.jpg','http://localhost:9000/mediums/shinzo_abe.jpg',160,213,11012,NULL,0,43,0,0),(44,'sosuke_uno.jpg','http://localhost:9000/mediums/sosuke_uno.jpg',142,201,13812,NULL,0,44,0,0),(45,'takaaki_kato.jpg','http://localhost:9000/mediums/takaaki_kato.jpg',160,212,6955,NULL,0,45,0,0),(46,'takashi_hara.jpg','http://localhost:9000/mediums/takashi_hara.jpg',160,212,8517,NULL,0,46,0,0),(47,'takeo_fukuda.jpg','http://localhost:9000/mediums/takeo_fukuda.jpg',132,179,10872,NULL,0,47,0,0),(48,'takeo_miki.jpg','http://localhost:9000/mediums/takeo_miki.jpg',137,185,40279,NULL,0,48,0,0),(49,'tanzan_ishibashi.jpg','http://localhost:9000/mediums/tanzan_ishibashi.jpg',160,196,13115,NULL,0,49,0,0),(50,'taro_aso.jpg','http://localhost:9000/mediums/taro_aso.jpg',160,213,10657,NULL,0,50,0,0),(51,'taro_katsura.jpg','http://localhost:9000/mediums/taro_katsura.jpg',160,213,11514,NULL,0,51,0,0),(52,'terauchi_masatake.jpg','http://localhost:9000/mediums/terauchi_masatake.jpg',160,208,11127,NULL,0,52,0,0),(53,'tetsu_katayama.jpg','http://localhost:9000/mediums/tetsu_katayama.jpg',160,196,8235,NULL,0,53,0,0),(54,'tomiichi_murayama.jpg','http://localhost:9000/mediums/tomiichi_murayama.jpg',132,189,30215,NULL,0,54,0,0),(55,'tomosaburo_kato.jpg','http://localhost:9000/mediums/tomosaburo_kato.jpg',160,213,6824,NULL,0,55,0,0),(56,'toshiki_kaifu.jpg','http://localhost:9000/mediums/toshiki_kaifu.jpg',160,195,9269,NULL,0,56,0,0),(57,'tsutomu_hata.jpg','http://localhost:9000/mediums/tsutomu_hata.jpg',137,191,31719,NULL,0,57,0,0),(58,'tsuyoshi_inukai.jpg','http://localhost:9000/mediums/tsuyoshi_inukai.jpg',160,203,5640,NULL,0,58,0,0),(59,'yasuhiro_nakasone.jpg','http://localhost:9000/mediums/yasuhiro_nakasone.jpg',160,217,11583,NULL,0,59,0,0),(60,'yasuo_fukuda.jpg','http://localhost:9000/mediums/yasuo_fukuda.jpg',129,187,31413,NULL,0,60,0,0),(61,'yoshihiko_noda.jpg','http://localhost:9000/mediums/yoshihiko_noda.jpg',160,213,8076,NULL,0,61,0,0),(62,'yoshiro_mori.jpg','http://localhost:9000/mediums/yoshiro_mori.jpg',160,203,15200,NULL,0,62,0,0),(63,'yukio_hatoyama.jpg','http://localhost:9000/mediums/yukio_hatoyama.jpg',160,213,16380,NULL,0,63,0,0),(64,'zenko_suzuki.jpg','http://localhost:9000/mediums/zenko_suzuki.jpg',139,197,13934,NULL,0,64,0,0);
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
INSERT INTO `relationships` VALUES (1,43,NULL,0,0,0,1,0,0),(2,43,NULL,0,0,0,1,0,0),(3,43,NULL,0,0,0,1,0,0),(4,43,NULL,0,0,0,1,0,0),(5,43,NULL,0,0,0,1,0,0),(6,43,NULL,0,0,0,1,0,0),(7,43,NULL,0,0,0,1,0,0),(8,43,NULL,0,0,0,1,0,0),(9,43,NULL,0,0,0,1,0,0),(10,43,NULL,0,0,0,1,0,0),(11,43,NULL,0,0,0,1,0,0),(12,43,NULL,0,0,0,1,0,0),(13,43,NULL,0,0,0,1,0,0),(14,43,NULL,0,0,0,1,0,0),(15,43,NULL,0,0,0,1,0,0),(16,43,NULL,0,0,0,1,0,0),(17,43,NULL,0,0,0,1,0,0),(18,43,NULL,0,0,0,1,0,0),(19,43,NULL,0,0,0,1,0,0),(20,43,NULL,0,0,0,1,0,0),(21,43,NULL,0,0,0,1,0,0),(22,43,NULL,0,0,0,1,0,0),(23,43,NULL,0,0,0,1,0,0),(24,43,NULL,0,0,0,1,0,0),(25,43,NULL,0,0,0,1,0,0),(26,43,NULL,0,0,0,1,0,0),(27,43,NULL,0,0,0,1,0,0),(28,43,NULL,0,0,0,1,0,0),(29,43,NULL,0,0,0,1,0,0),(30,43,NULL,0,0,0,1,0,0),(31,43,NULL,0,0,0,1,0,0),(32,43,NULL,0,0,0,1,0,0),(33,43,NULL,0,0,0,1,0,0),(34,43,NULL,0,0,0,1,0,0),(35,43,NULL,0,0,0,1,0,0),(36,43,NULL,0,0,0,1,0,0),(37,43,NULL,0,0,0,1,0,0),(38,43,NULL,0,0,0,1,0,0),(39,43,NULL,0,0,0,1,0,0),(40,43,NULL,0,0,0,1,0,0),(41,43,NULL,0,0,0,1,0,0),(42,43,NULL,0,0,0,1,0,0),(43,1,NULL,1,0,0,0,0,0),(43,2,NULL,1,0,0,0,0,0),(43,3,NULL,1,0,0,0,0,0),(43,4,NULL,1,0,0,0,0,0),(43,5,NULL,1,0,0,0,0,0),(43,6,NULL,1,0,0,0,0,0),(43,7,NULL,1,0,0,0,0,0),(43,8,NULL,1,0,0,0,0,0),(43,9,NULL,1,0,0,0,0,0),(43,10,NULL,1,0,0,0,0,0),(43,11,NULL,1,0,0,0,0,0),(43,12,NULL,1,0,0,0,0,0),(43,13,NULL,1,0,0,0,0,0),(43,14,NULL,1,0,0,0,0,0),(43,15,NULL,1,0,0,0,0,0),(43,16,NULL,1,0,0,0,0,0),(43,17,NULL,1,0,0,0,0,0),(43,18,NULL,1,0,0,0,0,0),(43,19,NULL,1,0,0,0,0,0),(43,20,NULL,1,0,0,0,0,0),(43,21,NULL,1,0,0,0,0,0),(43,22,NULL,1,0,0,0,0,0),(43,23,NULL,1,0,0,0,0,0),(43,24,NULL,1,0,0,0,0,0),(43,25,NULL,1,0,0,0,0,0),(43,26,NULL,1,0,0,0,0,0),(43,27,NULL,1,0,0,0,0,0),(43,28,NULL,1,0,0,0,0,0),(43,29,NULL,1,0,0,0,0,0),(43,30,NULL,1,0,0,0,0,0),(43,31,NULL,1,0,0,0,0,0),(43,32,NULL,1,0,0,0,0,0),(43,33,NULL,1,0,0,0,0,0),(43,34,NULL,1,0,0,0,0,0),(43,35,NULL,1,0,0,0,0,0),(43,36,NULL,1,0,0,0,0,0),(43,37,NULL,1,0,0,0,0,0),(43,38,NULL,1,0,0,0,0,0),(43,39,NULL,1,0,0,0,0,0),(43,40,NULL,1,0,0,0,0,0),(43,41,NULL,1,0,0,0,0,0),(43,42,NULL,1,0,0,0,0,0),(43,44,NULL,1,0,0,0,0,0),(43,45,NULL,1,0,0,0,0,0),(43,46,NULL,1,0,0,0,0,0),(43,47,NULL,1,0,0,0,0,0),(43,48,NULL,1,0,0,0,0,0),(43,49,NULL,1,0,0,0,0,0),(43,50,NULL,1,0,0,0,0,0),(43,51,NULL,1,0,0,0,0,0),(43,52,NULL,1,0,0,0,0,0),(43,53,NULL,1,0,0,0,0,0),(43,54,NULL,1,0,0,0,0,0),(43,55,NULL,1,0,0,0,0,0),(43,56,NULL,1,0,0,0,0,0),(43,57,NULL,1,0,0,0,0,0),(43,58,NULL,1,0,0,0,0,0),(43,59,NULL,1,0,0,0,0,0),(43,60,NULL,1,0,0,0,0,0),(43,61,NULL,1,0,0,0,0,0),(43,62,NULL,1,0,0,0,0,0),(43,63,NULL,1,0,0,0,0,0),(43,64,NULL,1,0,0,0,0,0),(44,43,NULL,0,0,0,1,0,0),(45,43,NULL,0,0,0,1,0,0),(46,43,NULL,0,0,0,1,0,0),(47,43,NULL,0,0,0,1,0,0),(48,43,NULL,0,0,0,1,0,0),(49,43,NULL,0,0,0,1,0,0),(50,43,NULL,0,0,0,1,0,0),(51,43,NULL,0,0,0,1,0,0),(52,43,NULL,0,0,0,1,0,0),(53,43,NULL,0,0,0,1,0,0),(54,43,NULL,0,0,0,1,0,0),(55,43,NULL,0,0,0,1,0,0),(56,43,NULL,0,0,0,1,0,0),(57,43,NULL,0,0,0,1,0,0),(58,43,NULL,0,0,0,1,0,0),(59,43,NULL,0,0,0,1,0,0),(60,43,NULL,0,0,0,1,0,0),(61,43,NULL,0,0,0,1,0,0),(62,43,NULL,0,0,0,1,0,0),(63,43,NULL,0,0,0,1,0,0),(64,43,NULL,0,0,0,1,0,0);
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

-- Dump completed on 2018-12-08  0:13:25
