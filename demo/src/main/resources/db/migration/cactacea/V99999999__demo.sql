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
INSERT INTO `account_feeds` VALUES (1,1,0,43,20181208050256),(43,2,0,1,20181208050256),(43,3,0,1,20181208050256),(43,4,0,1,20181208050256),(43,5,0,1,20181208050256);
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
INSERT INTO `accounts` VALUES (1,'aritomo_yamagata','aritomo_yamagata','f737bd52007ca0dc9abb86b5567e8372',1,1,'http://localhost:9000/mediums/aritomo_yamagata.jpg',1,0,4,NULL,NULL,NULL,NULL,0,NULL),(2,'eisaku_sato','eisaku_sato','f737bd52007ca0dc9abb86b5567e8372',1,2,'http://localhost:9000/mediums/eisaku_sato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(3,'giichi_tanaka','giichi_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,3,'http://localhost:9000/mediums/giichi_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(4,'gonbei_yamamoto','gonbei_yamamoto','f737bd52007ca0dc9abb86b5567e8372',1,4,'http://localhost:9000/mediums/gonbei_yamamoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(5,'hayato_ikeda','hayato_ikeda','f737bd52007ca0dc9abb86b5567e8372',1,5,'http://localhost:9000/mediums/hayato_ikeda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(6,'hideki_tojo','hideki_tojo','f737bd52007ca0dc9abb86b5567e8372',1,6,'http://localhost:9000/mediums/hideki_tojo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(7,'hirobumi_ito','hirobumi_ito','f737bd52007ca0dc9abb86b5567e8372',1,7,'http://localhost:9000/mediums/hirobumi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(8,'hitoshi_ashida','hitoshi_ashida','f737bd52007ca0dc9abb86b5567e8372',1,8,'http://localhost:9000/mediums/hitoshi_ashida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(9,'ichiro_hatoyama','ichiro_hatoyama','f737bd52007ca0dc9abb86b5567e8372',1,9,'http://localhost:9000/mediums/ichiro_hatoyama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(10,'junichiro_koizumi','junichiro_koizumi','f737bd52007ca0dc9abb86b5567e8372',1,10,'http://localhost:9000/mediums/junichiro_koizumi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(11,'kakuei_tanaka','kakuei_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,11,'http://localhost:9000/mediums/kakuei_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(12,'kantaro_suzuki','kantaro_suzuki','f737bd52007ca0dc9abb86b5567e8372',1,12,'http://localhost:9000/mediums/kantaro_suzuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(13,'keigo_kiyoura','keigo_kiyoura','f737bd52007ca0dc9abb86b5567e8372',1,13,'http://localhost:9000/mediums/keigo_kiyoura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(14,'keisuke_okada','keisuke_okada','f737bd52007ca0dc9abb86b5567e8372',1,14,'http://localhost:9000/mediums/keisuke_okada.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(15,'keizo_obuchi','keizo_obuchi','f737bd52007ca0dc9abb86b5567e8372',1,15,'http://localhost:9000/mediums/keizo_obuchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(16,'kiichi_miyazawa','kiichi_miyazawa','f737bd52007ca0dc9abb86b5567e8372',1,16,'http://localhost:9000/mediums/kiichi_miyazawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(17,'kiichiro_hiranumra','kiichiro_hiranumra','f737bd52007ca0dc9abb86b5567e8372',1,17,'http://localhost:9000/mediums/kiichiro_hiranumra.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(18,'kijuro_shidehara','kijuro_shidehara','f737bd52007ca0dc9abb86b5567e8372',1,18,'http://localhost:9000/mediums/kijuro_shidehara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(19,'kinmochi_saionji','kinmochi_saionji','f737bd52007ca0dc9abb86b5567e8372',1,19,'http://localhost:9000/mediums/kinmochi_saionji.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(20,'kiyotaka_kuroda','kiyotaka_kuroda','f737bd52007ca0dc9abb86b5567e8372',1,20,'http://localhost:9000/mediums/kiyotaka_kuroda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(21,'kohki_hirota','kohki_hirota','f737bd52007ca0dc9abb86b5567e8372',1,21,'http://localhost:9000/mediums/kohki_hirota.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(22,'korekiyo_takahashi','korekiyo_takahashi','f737bd52007ca0dc9abb86b5567e8372',1,22,'http://localhost:9000/mediums/korekiyo_takahashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(23,'kosai_uchida','kosai_uchida','f737bd52007ca0dc9abb86b5567e8372',1,23,'http://localhost:9000/mediums/kosai_uchida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(24,'kuniaki_koiso','kuniaki_koiso','f737bd52007ca0dc9abb86b5567e8372',1,24,'http://localhost:9000/mediums/kuniaki_koiso.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(25,'makoto_saito','makoto_saito','f737bd52007ca0dc9abb86b5567e8372',1,25,'http://localhost:9000/mediums/makoto_saito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(26,'masayoshi_ito','masayoshi_ito','f737bd52007ca0dc9abb86b5567e8372',1,26,'http://localhost:9000/mediums/masayoshi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(27,'masayoshi_matsutaka','masayoshi_matsutaka','f737bd52007ca0dc9abb86b5567e8372',1,27,'http://localhost:9000/mediums/masayoshi_matsutaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(28,'masayoshi_ohira','masayoshi_ohira','f737bd52007ca0dc9abb86b5567e8372',1,28,'http://localhost:9000/mediums/masayoshi_ohira.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(29,'mitsumasa_yonai','mitsumasa_yonai','f737bd52007ca0dc9abb86b5567e8372',1,29,'http://localhost:9000/mediums/mitsumasa_yonai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(30,'morihiro_hosokawa','morihiro_hosokawa','f737bd52007ca0dc9abb86b5567e8372',1,30,'http://localhost:9000/mediums/morihiro_hosokawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(31,'naoto_kan','naoto_kan','f737bd52007ca0dc9abb86b5567e8372',1,31,'http://localhost:9000/mediums/naoto_kan.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(32,'naruhiko_higashikuni','naruhiko_higashikuni','f737bd52007ca0dc9abb86b5567e8372',1,32,'http://localhost:9000/mediums/naruhiko_higashikuni.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(33,'noboru_takeshita','noboru_takeshita','f737bd52007ca0dc9abb86b5567e8372',1,33,'http://localhost:9000/mediums/noboru_takeshita.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(34,'nobusuke_kishi','nobusuke_kishi','f737bd52007ca0dc9abb86b5567e8372',1,34,'http://localhost:9000/mediums/nobusuke_kishi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(35,'nobuyuki_abe','nobuyuki_abe','f737bd52007ca0dc9abb86b5567e8372',1,35,'http://localhost:9000/mediums/nobuyuki_abe.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(36,'okuma_shigenobu','okuma_shigenobu','f737bd52007ca0dc9abb86b5567e8372',1,36,'http://localhost:9000/mediums/okuma_shigenobu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(37,'osachi_hamaguchi','osachi_hamaguchi','f737bd52007ca0dc9abb86b5567e8372',1,37,'http://localhost:9000/mediums/osachi_hamaguchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(38,'reijiro_wakatsuki','reijiro_wakatsuki','f737bd52007ca0dc9abb86b5567e8372',1,38,'http://localhost:9000/mediums/reijiro_wakatsuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(39,'ryutaro_hashimoto','ryutaro_hashimoto','f737bd52007ca0dc9abb86b5567e8372',1,39,'http://localhost:9000/mediums/ryutaro_hashimoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(40,'sanetomi_sanjo','sanetomi_sanjo','f737bd52007ca0dc9abb86b5567e8372',1,40,'http://localhost:9000/mediums/sanetomi_sanjo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(41,'senjuro_hayashi','senjuro_hayashi','f737bd52007ca0dc9abb86b5567e8372',1,41,'http://localhost:9000/mediums/senjuro_hayashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(42,'shigeru_yoshida','shigeru_yoshida','f737bd52007ca0dc9abb86b5567e8372',1,42,'http://localhost:9000/mediums/shigeru_yoshida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(43,'shinzo_abe','shinzo_abe','f737bd52007ca0dc9abb86b5567e8372',1,43,'http://localhost:9000/mediums/shinzo_abe.jpg',1,0,1,NULL,NULL,NULL,NULL,0,NULL),(44,'sosuke_uno','sosuke_uno','f737bd52007ca0dc9abb86b5567e8372',1,44,'http://localhost:9000/mediums/sosuke_uno.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(45,'takaaki_kato','takaaki_kato','f737bd52007ca0dc9abb86b5567e8372',1,45,'http://localhost:9000/mediums/takaaki_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(46,'takashi_hara','takashi_hara','f737bd52007ca0dc9abb86b5567e8372',1,46,'http://localhost:9000/mediums/takashi_hara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(47,'takeo_fukuda','takeo_fukuda','f737bd52007ca0dc9abb86b5567e8372',1,47,'http://localhost:9000/mediums/takeo_fukuda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(48,'takeo_miki','takeo_miki','f737bd52007ca0dc9abb86b5567e8372',1,48,'http://localhost:9000/mediums/takeo_miki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(49,'tanzan_ishibashi','tanzan_ishibashi','f737bd52007ca0dc9abb86b5567e8372',1,49,'http://localhost:9000/mediums/tanzan_ishibashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(50,'taro_aso','taro_aso','f737bd52007ca0dc9abb86b5567e8372',1,50,'http://localhost:9000/mediums/taro_aso.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(51,'taro_katsura','taro_katsura','f737bd52007ca0dc9abb86b5567e8372',1,51,'http://localhost:9000/mediums/taro_katsura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(52,'terauchi_masatake','terauchi_masatake','f737bd52007ca0dc9abb86b5567e8372',1,52,'http://localhost:9000/mediums/terauchi_masatake.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(53,'tetsu_katayama','tetsu_katayama','f737bd52007ca0dc9abb86b5567e8372',1,53,'http://localhost:9000/mediums/tetsu_katayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(54,'tomiichi_murayama','tomiichi_murayama','f737bd52007ca0dc9abb86b5567e8372',1,54,'http://localhost:9000/mediums/tomiichi_murayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(55,'tomosaburo_kato','tomosaburo_kato','f737bd52007ca0dc9abb86b5567e8372',1,55,'http://localhost:9000/mediums/tomosaburo_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(56,'toshiki_kaifu','toshiki_kaifu','f737bd52007ca0dc9abb86b5567e8372',1,56,'http://localhost:9000/mediums/toshiki_kaifu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(57,'tsutomu_hata','tsutomu_hata','f737bd52007ca0dc9abb86b5567e8372',1,57,'http://localhost:9000/mediums/tsutomu_hata.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(58,'tsuyoshi_inukai','tsuyoshi_inukai','f737bd52007ca0dc9abb86b5567e8372',1,58,'http://localhost:9000/mediums/tsuyoshi_inukai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(59,'yasuhiro_nakasone','yasuhiro_nakasone','f737bd52007ca0dc9abb86b5567e8372',2,59,'http://localhost:9000/mediums/yasuhiro_nakasone.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(60,'yasuo_fukuda','yasuo_fukuda','f737bd52007ca0dc9abb86b5567e8372',2,60,'http://localhost:9000/mediums/yasuo_fukuda.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(61,'yoshihiko_noda','yoshihiko_noda','f737bd52007ca0dc9abb86b5567e8372',2,61,'http://localhost:9000/mediums/yoshihiko_noda.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(62,'yoshiro_mori','yoshiro_mori','f737bd52007ca0dc9abb86b5567e8372',2,62,'http://localhost:9000/mediums/yoshiro_mori.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(63,'yukio_hatoyama','yukio_hatoyama','f737bd52007ca0dc9abb86b5567e8372',2,63,'http://localhost:9000/mediums/yukio_hatoyama.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(64,'zenko_suzuki','zenko_suzuki','f737bd52007ca0dc9abb86b5567e8372',2,64,'http://localhost:9000/mediums/zenko_suzuki.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL);
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
INSERT INTO `devices` VALUES (1,1,'e48913ce-54d0-406e-a176-2d522e0c9b60',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(2,2,'fd7524f1-3387-4e9f-92e2-813d7e61343e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(3,3,'242df2fd-c11c-41e6-a404-d840eb6a7d6c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(4,4,'52a860c3-e25e-473d-b299-a9b90f72b6e9',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(5,5,'299d12d3-a1ab-4422-a82b-6243159d9341',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(6,6,'5454b6a9-2ce4-438e-b3d8-60a7254b0d25',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(7,7,'bf126b0c-4105-4341-b0da-917d420083c3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(8,8,'d4ce95bc-11a9-4993-8f9b-72666098e2a3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(9,9,'a841aceb-8273-4a53-88a1-f9d15c9a6e22',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(10,10,'da8a3771-c8df-4033-a908-491338afb9f1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(11,11,'8dae327e-f1e1-4996-b593-b50440dbc3d3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(12,12,'778fb15c-558c-418b-9d06-598a9af0bda3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(13,13,'8bce4350-7943-428e-9e00-17a2bb5e6502',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(14,14,'06426479-6a49-4c8e-b8a4-92afd477e005',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(15,15,'4d72dc3d-c4a1-44bf-b374-8c88f038ef45',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(16,16,'7bbb8084-e29e-477b-8bde-1f44aff63768',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(17,17,'1ce176b1-8eab-40de-8bd4-1c1aa2273bb8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(18,18,'8fc24bc4-9297-42f3-a3a6-be52fe71f3e1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(19,19,'d10478ca-0af9-43d5-9158-5637c850bbee',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(20,20,'f984c761-4dfe-4e91-89d3-da9ab92dd2f2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(21,21,'beac7fc1-8907-4778-a9ae-cc1dfb1d531f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(22,22,'64b955c7-160f-405e-8bb7-27ec9fcd83d6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(23,23,'d05bc37e-0070-48c6-9f3e-7ad9abc7ba85',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(24,24,'4e67ec1f-468c-4b6c-9fe2-2ea4a0c92c89',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(25,25,'802146e7-0497-4155-a835-ab759f38b75c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(26,26,'71dd2cdb-fbb5-4b30-8e14-2ebec6fea1fe',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(27,27,'927c1e49-584d-4079-8353-b379d8137169',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(28,28,'922cecad-c004-438f-8734-731aecb01b1f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(29,29,'855df565-908c-4bbd-bc13-73896722ed44',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(30,30,'2d0439e4-1a3e-467e-8bb5-63103f39105b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(31,31,'c82a864e-23be-42e4-b40d-2cc63b50d50b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(32,32,'17715464-fb1b-494e-9cab-ea3771f43c7d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(33,33,'29fbaf2c-d292-44a2-a97e-d04b3fd9d357',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(34,34,'a228a813-e337-4891-9575-33cd8636e2e6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(35,35,'f1175b46-ffc2-4730-a5d6-7f341ccc0b97',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(36,36,'dc349bce-1cfe-4c78-ac82-c3d415f077c2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(37,37,'66bb3441-8653-46f5-a02e-93d7afd55e0c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(38,38,'4cad72f9-cc03-4268-affb-e4acfdbf0ff2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(39,39,'1e0db833-2437-42be-9fc6-020cc5f24686',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(40,40,'29f62199-f43b-4922-af7d-c4bd3a74872d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(41,41,'6f9da218-61da-43d6-8e3f-49c37a2cefa0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(42,42,'ccca4df0-8031-4c53-b959-c2277dc1acca',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(43,43,'a0dfd614-3f84-4047-89d9-b6531bbac3a0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(44,44,'bd915137-d7a9-4fb9-b9df-ff2bb0583fa5',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(45,45,'416655bb-ab6c-43e0-8dce-eb4f25822aa9',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(46,46,'e4fec3aa-e0e7-4a5f-b6ce-607c8839d98a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(47,47,'5f36a72c-a54e-4503-9fa9-ed1422ef21bd',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(48,48,'b12192a5-6e2b-4d36-96bf-83bb8367a29e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(49,49,'26856a45-5d0f-40c0-9eed-57566a6e87e3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(50,50,'78d760f8-cf4f-4293-8915-548c81e9d951',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(51,51,'86e7e626-b722-4974-8aaf-978e584f96de',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(52,52,'9abe6ec5-69d0-455a-a4d3-6ba9d4a508cd',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(53,53,'731129b3-da5c-439e-a41b-fde746fa3fb0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(54,54,'c6b35489-b71c-42d9-b319-9af5df4e8f9e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(55,55,'ff59e551-314c-4865-9aa7-50577d99d35d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(56,56,'258a197c-93e5-437c-80e3-634b4a6971e8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(57,57,'921ab3cf-1f52-436b-b391-98087f44d00a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(58,58,'83e732f3-22c6-43ec-9de1-af3b404c6f4e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(59,59,'ef45ea44-3f7a-47a8-8278-6d0ee1990f1a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(60,60,'856bbc80-ed3b-45e9-9b57-8e8664f918e9',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(61,61,'f8720bcc-a6b4-4322-b6a8-ffb6c0916f76',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(62,62,'5a6cd13e-959c-4b6a-80b5-dbb9b44d2611',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(63,63,'420b4d25-8fe5-45c4-b3ae-c52636b64db1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(64,64,'3141f814-249a-4c52-8ecc-7ce44370e198',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1');
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
INSERT INTO `feeds` VALUES (1,'Uesugi Kenshin (上杉 謙信, February 18, 1530 – April 19, 1578[1]) was a daimyō who was born as Nagao Kagetora,[2] and after the adoption into the Uesugi clan, ruled Echigo Province in the Sengoku period of Japan.[3] He was one of the most powerful daimyōs of the Sengoku period. While chiefly remembered for his prowess on the battlefield, Kenshin is also regarded as an extremely skillful administrator who fostered the growth of local industries and trade; his rule saw a marked rise in the standard of living of Echigo.',43,0,0,0,0,0,NULL,0,1544245376105),(2,'Oda Nobunaga (help·info), June 23, 1534 – June 21, 1582) was a powerful daimyō (feudal lord) of Japan in the late 16th century who attempted to unify Japan during the late Sengoku period, and successfully gained control over most of Honshu. Nobunaga is regarded as one of three unifiers of Japan along with his retainers Toyotomi Hideyoshi and Tokugawa Ieyasu. During his later life, Nobunaga was widely known for most brutal suppression of determined opponents, eliminating those who by principle refused to cooperate or yield to his demands. His reign was noted for innovative military tactics, fostering free trade, and encouraging the start of the Momoyama historical art period. He was killed when his retainer Akechi Mitsuhide rebelled against him at Honnō-ji.',1,0,0,0,0,0,NULL,0,1544245376342),(3,'Tokugawa Ieyasu (徳川家康, January 30, 1543 – June 1, 1616) was the founder and first shōgun of the Tokugawa shogunate of Japan, which effectively ruled Japan from the Battle of Sekigahara in 1600 until the Meiji Restoration in 1868. Ieyasu seized power in 1600, received appointment as shōgun in 1603, and abdicated from office in 1605, but remained in power until his death in 1616. His given name is sometimes spelled Iyeyasu,[1][2] according to the historical pronunciation of the kana character he. Ieyasu was posthumously enshrined at Nikkō Tōshō-gū with the name Tōshō Daigongen (東照大権現). He was one of the three unifiers of Japan, along with his former lord Nobunaga and Toyotomi Hideyoshi.',1,0,0,0,0,0,NULL,0,1544245376425),(4,'Toyotomi Hideyoshi (豊臣 秀吉, March 17, 1537 – September 18, 1598) was a preeminent daimyō, warrior, general, samurai, and politician of the Sengoku period[1] who is regarded as Japan\'s second \"great unifier\".[2] He succeeded his former liege lord, Oda Nobunaga, and brought an end to the Warring Lords period. The period of his rule is often called the Momoyama period, named after Hideyoshi\'s castle. After his death, his young son Hideyori was displaced by Tokugawa Ieyasu.',1,0,0,0,0,0,NULL,0,1544245376487),(5,'Yasuke (variously rendered as 弥助 or 弥介, 彌助 or 彌介 in different sources.[1]) (b. c. 1555–1590) was a Samurai of African origin who served under the Japanese hegemon and warlord Oda Nobunaga in 1581 and 1582.',1,0,0,2,0,0,NULL,0,1544245376593);
/*!40000 ALTER TABLE `feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `followers`
--

LOCK TABLES `followers` WRITE;
/*!40000 ALTER TABLE `followers` DISABLE KEYS */;
INSERT INTO `followers` VALUES (1,1,43,1544245373104),(2,2,43,1544245373216),(3,3,43,1544245373254),(4,4,43,1544245373288),(5,5,43,1544245373318),(6,6,43,1544245373344),(7,7,43,1544245373373),(8,8,43,1544245373416),(9,9,43,1544245373471),(10,10,43,1544245373509),(11,11,43,1544245373537),(12,12,43,1544245373568),(13,13,43,1544245373600),(14,14,43,1544245373652),(15,15,43,1544245373703),(16,16,43,1544245373739),(17,17,43,1544245373766),(18,18,43,1544245373791),(19,19,43,1544245373818),(20,20,43,1544245373861),(21,21,43,1544245373919),(22,22,43,1544245373957),(23,23,43,1544245373985),(24,24,43,1544245374016),(25,25,43,1544245374061),(26,26,43,1544245374147),(27,27,43,1544245374189),(28,28,43,1544245374221),(29,29,43,1544245374308),(30,30,43,1544245374374),(31,31,43,1544245374406),(32,32,43,1544245374441),(33,33,43,1544245374494),(34,34,43,1544245374561),(35,35,43,1544245374608),(36,36,43,1544245374636),(37,37,43,1544245374665),(38,38,43,1544245374725),(39,39,43,1544245374780),(40,40,43,1544245374811),(41,41,43,1544245374837),(42,42,43,1544245374874),(43,44,43,1544245374915),(44,45,43,1544245374988),(45,46,43,1544245375040),(46,47,43,1544245375072),(47,48,43,1544245375109),(48,49,43,1544245375143),(49,50,43,1544245375204),(50,51,43,1544245375262),(51,52,43,1544245375295),(52,53,43,1544245375324),(53,54,43,1544245375350),(54,55,43,1544245375379),(55,56,43,1544245375433),(56,57,43,1544245375485),(57,58,43,1544245375513),(58,59,43,1544245375539),(59,60,43,1544245375569),(60,61,43,1544245375597),(61,62,43,1544245375640),(62,63,43,1544245375695),(63,64,43,1544245375735),(64,59,1,1544245375762),(65,60,1,1544245375784),(66,61,1,1544245375807),(67,62,1,1544245375843),(68,63,1,1544245375931),(69,64,1,1544245375959),(70,43,1,1544245375983);
/*!40000 ALTER TABLE `followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `follows`
--

LOCK TABLES `follows` WRITE;
/*!40000 ALTER TABLE `follows` DISABLE KEYS */;
INSERT INTO `follows` VALUES (1,43,1,1544245373071),(2,43,2,1544245373203),(3,43,3,1544245373249),(4,43,4,1544245373278),(5,43,5,1544245373314),(6,43,6,1544245373340),(7,43,7,1544245373368),(8,43,8,1544245373408),(9,43,9,1544245373460),(10,43,10,1544245373504),(11,43,11,1544245373532),(12,43,12,1544245373560),(13,43,13,1544245373594),(14,43,14,1544245373645),(15,43,15,1544245373694),(16,43,16,1544245373733),(17,43,17,1544245373762),(18,43,18,1544245373787),(19,43,19,1544245373814),(20,43,20,1544245373851),(21,43,21,1544245373909),(22,43,22,1544245373952),(23,43,23,1544245373980),(24,43,24,1544245374011),(25,43,25,1544245374051),(26,43,26,1544245374132),(27,43,27,1544245374183),(28,43,28,1544245374214),(29,43,29,1544245374285),(30,43,30,1544245374367),(31,43,31,1544245374401),(32,43,32,1544245374435),(33,43,33,1544245374485),(34,43,34,1544245374546),(35,43,35,1544245374601),(36,43,36,1544245374631),(37,43,37,1544245374660),(38,43,38,1544245374715),(39,43,39,1544245374774),(40,43,40,1544245374806),(41,43,41,1544245374832),(42,43,42,1544245374869),(43,43,44,1544245374905),(44,43,45,1544245374971),(45,43,46,1544245375034),(46,43,47,1544245375066),(47,43,48,1544245375100),(48,43,49,1544245375136),(49,43,50,1544245375192),(50,43,51,1544245375256),(51,43,52,1544245375290),(52,43,53,1544245375320),(53,43,54,1544245375346),(54,43,55,1544245375373),(55,43,56,1544245375425),(56,43,57,1544245375477),(57,43,58,1544245375508),(58,43,59,1544245375533),(59,43,60,1544245375561),(60,43,61,1544245375592),(61,43,62,1544245375630),(62,43,63,1544245375685),(63,43,64,1544245375730),(64,1,59,1544245375757),(65,1,60,1544245375781),(66,1,61,1544245375803),(67,1,62,1544245375837),(68,1,63,1544245375909),(69,1,64,1544245375955),(70,1,43,1544245375978);
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
INSERT INTO `mediums` VALUES (1,'aritomo_yamagata.jpg','http://localhost:9000/mediums/aritomo_yamagata.jpg',160,213,13192,'http://localhost:9000/mediums/aritomo_yamagata.jpg',0,1,0,0),(2,'eisaku_sato.jpg','http://localhost:9000/mediums/eisaku_sato.jpg',160,227,10510,'http://localhost:9000/mediums/eisaku_sato.jpg',0,2,0,0),(3,'giichi_tanaka.jpg','http://localhost:9000/mediums/giichi_tanaka.jpg',160,198,7181,'http://localhost:9000/mediums/giichi_tanaka.jpg',0,3,0,0),(4,'gonbei_yamamoto.jpg','http://localhost:9000/mediums/gonbei_yamamoto.jpg',160,210,9566,'http://localhost:9000/mediums/gonbei_yamamoto.jpg',0,4,0,0),(5,'hayato_ikeda.jpg','http://localhost:9000/mediums/hayato_ikeda.jpg',160,213,5901,'http://localhost:9000/mediums/hayato_ikeda.jpg',0,5,0,0),(6,'hideki_tojo.jpg','http://localhost:9000/mediums/hideki_tojo.jpg',160,215,7435,'http://localhost:9000/mediums/hideki_tojo.jpg',0,6,0,0),(7,'hirobumi_ito.jpg','http://localhost:9000/mediums/hirobumi_ito.jpg',160,218,9141,'http://localhost:9000/mediums/hirobumi_ito.jpg',0,7,0,0),(8,'hitoshi_ashida.jpg','http://localhost:9000/mediums/hitoshi_ashida.jpg',160,194,12280,'http://localhost:9000/mediums/hitoshi_ashida.jpg',0,8,0,0),(9,'ichiro_hatoyama.jpg','http://localhost:9000/mediums/ichiro_hatoyama.jpg',160,195,9804,'http://localhost:9000/mediums/ichiro_hatoyama.jpg',0,9,0,0),(10,'junichiro_koizumi.jpg','http://localhost:9000/mediums/junichiro_koizumi.jpg',131,175,53624,'http://localhost:9000/mediums/junichiro_koizumi.jpg',0,10,0,0),(11,'kakuei_tanaka.jpg','http://localhost:9000/mediums/kakuei_tanaka.jpg',148,183,34578,'http://localhost:9000/mediums/kakuei_tanaka.jpg',0,11,0,0),(12,'kantaro_suzuki.jpg','http://localhost:9000/mediums/kantaro_suzuki.jpg',160,201,5381,'http://localhost:9000/mediums/kantaro_suzuki.jpg',0,12,0,0),(13,'keigo_kiyoura.jpg','http://localhost:9000/mediums/keigo_kiyoura.jpg',160,213,11265,'http://localhost:9000/mediums/keigo_kiyoura.jpg',0,13,0,0),(14,'keisuke_okada.jpg','http://localhost:9000/mediums/keisuke_okada.jpg',160,213,10460,'http://localhost:9000/mediums/keisuke_okada.jpg',0,14,0,0),(15,'keizo_obuchi.jpg','http://localhost:9000/mediums/keizo_obuchi.jpg',160,213,11264,'http://localhost:9000/mediums/keizo_obuchi.jpg',0,15,0,0),(16,'kiichi_miyazawa.jpg','http://localhost:9000/mediums/kiichi_miyazawa.jpg',160,213,15500,'http://localhost:9000/mediums/kiichi_miyazawa.jpg',0,16,0,0),(17,'kiichiro_hiranumra.jpg','http://localhost:9000/mediums/kiichiro_hiranumra.jpg',160,219,8780,'http://localhost:9000/mediums/kiichiro_hiranumra.jpg',0,17,0,0),(18,'kijuro_shidehara.jpg','http://localhost:9000/mediums/kijuro_shidehara.jpg',160,211,12099,'http://localhost:9000/mediums/kijuro_shidehara.jpg',0,18,0,0),(19,'kinmochi_saionji.jpg','http://localhost:9000/mediums/kinmochi_saionji.jpg',160,198,9571,'http://localhost:9000/mediums/kinmochi_saionji.jpg',0,19,0,0),(20,'kiyotaka_kuroda.jpg','http://localhost:9000/mediums/kiyotaka_kuroda.jpg',160,207,11504,'http://localhost:9000/mediums/kiyotaka_kuroda.jpg',0,20,0,0),(21,'kohki_hirota.jpg','http://localhost:9000/mediums/kohki_hirota.jpg',160,213,7291,'http://localhost:9000/mediums/kohki_hirota.jpg',0,21,0,0),(22,'korekiyo_takahashi.jpg','http://localhost:9000/mediums/korekiyo_takahashi.jpg',160,200,6941,'http://localhost:9000/mediums/korekiyo_takahashi.jpg',0,22,0,0),(23,'kosai_uchida.jpg','http://localhost:9000/mediums/kosai_uchida.jpg',160,213,9038,'http://localhost:9000/mediums/kosai_uchida.jpg',0,23,0,0),(24,'kuniaki_koiso.jpg','http://localhost:9000/mediums/kuniaki_koiso.jpg',160,213,8478,'http://localhost:9000/mediums/kuniaki_koiso.jpg',0,24,0,0),(25,'makoto_saito.jpg','http://localhost:9000/mediums/makoto_saito.jpg',160,213,7271,'http://localhost:9000/mediums/makoto_saito.jpg',0,25,0,0),(26,'masayoshi_ito.jpg','http://localhost:9000/mediums/masayoshi_ito.jpg',160,219,6479,'http://localhost:9000/mediums/masayoshi_ito.jpg',0,26,0,0),(27,'masayoshi_matsutaka.jpg','http://localhost:9000/mediums/masayoshi_matsutaka.jpg',160,213,11710,'http://localhost:9000/mediums/masayoshi_matsutaka.jpg',0,27,0,0),(28,'masayoshi_ohira.jpg','http://localhost:9000/mediums/masayoshi_ohira.jpg',160,215,11323,'http://localhost:9000/mediums/masayoshi_ohira.jpg',0,28,0,0),(29,'mitsumasa_yonai.jpg','http://localhost:9000/mediums/mitsumasa_yonai.jpg',160,204,5076,'http://localhost:9000/mediums/mitsumasa_yonai.jpg',0,29,0,0),(30,'morihiro_hosokawa.jpg','http://localhost:9000/mediums/morihiro_hosokawa.jpg',135,197,34210,'http://localhost:9000/mediums/morihiro_hosokawa.jpg',0,30,0,0),(31,'naoto_kan.jpg','http://localhost:9000/mediums/naoto_kan.jpg',160,214,10468,'http://localhost:9000/mediums/naoto_kan.jpg',0,31,0,0),(32,'naruhiko_higashikuni.jpg','http://localhost:9000/mediums/naruhiko_higashikuni.jpg',160,193,11366,'http://localhost:9000/mediums/naruhiko_higashikuni.jpg',0,32,0,0),(33,'noboru_takeshita.jpg','http://localhost:9000/mediums/noboru_takeshita.jpg',149,195,14112,'http://localhost:9000/mediums/noboru_takeshita.jpg',0,33,0,0),(34,'nobusuke_kishi.jpg','http://localhost:9000/mediums/nobusuke_kishi.jpg',126,185,15176,'http://localhost:9000/mediums/nobusuke_kishi.jpg',0,34,0,0),(35,'nobuyuki_abe.jpg','http://localhost:9000/mediums/nobuyuki_abe.jpg',160,231,9779,'http://localhost:9000/mediums/nobuyuki_abe.jpg',0,35,0,0),(36,'okuma_shigenobu.jpg','http://localhost:9000/mediums/okuma_shigenobu.jpg',160,213,10802,'http://localhost:9000/mediums/okuma_shigenobu.jpg',0,36,0,0),(37,'osachi_hamaguchi.jpg','http://localhost:9000/mediums/osachi_hamaguchi.jpg',160,200,7364,'http://localhost:9000/mediums/osachi_hamaguchi.jpg',0,37,0,0),(38,'reijiro_wakatsuki.jpg','http://localhost:9000/mediums/reijiro_wakatsuki.jpg',160,198,6621,'http://localhost:9000/mediums/reijiro_wakatsuki.jpg',0,38,0,0),(39,'ryutaro_hashimoto.jpg','http://localhost:9000/mediums/ryutaro_hashimoto.jpg',160,211,8375,'http://localhost:9000/mediums/ryutaro_hashimoto.jpg',0,39,0,0),(40,'sanetomi_sanjo.jpg','http://localhost:9000/mediums/sanetomi_sanjo.jpg',160,207,8942,'http://localhost:9000/mediums/sanetomi_sanjo.jpg',0,40,0,0),(41,'senjuro_hayashi.jpg','http://localhost:9000/mediums/senjuro_hayashi.jpg',160,213,9667,'http://localhost:9000/mediums/senjuro_hayashi.jpg',0,41,0,0),(42,'shigeru_yoshida.jpg','http://localhost:9000/mediums/shigeru_yoshida.jpg',160,213,11620,'http://localhost:9000/mediums/shigeru_yoshida.jpg',0,42,0,0),(43,'shinzo_abe.jpg','http://localhost:9000/mediums/shinzo_abe.jpg',160,213,11012,'http://localhost:9000/mediums/shinzo_abe.jpg',0,43,0,0),(44,'sosuke_uno.jpg','http://localhost:9000/mediums/sosuke_uno.jpg',142,201,13812,'http://localhost:9000/mediums/sosuke_uno.jpg',0,44,0,0),(45,'takaaki_kato.jpg','http://localhost:9000/mediums/takaaki_kato.jpg',160,212,6955,'http://localhost:9000/mediums/takaaki_kato.jpg',0,45,0,0),(46,'takashi_hara.jpg','http://localhost:9000/mediums/takashi_hara.jpg',160,212,8517,'http://localhost:9000/mediums/takashi_hara.jpg',0,46,0,0),(47,'takeo_fukuda.jpg','http://localhost:9000/mediums/takeo_fukuda.jpg',132,179,10872,'http://localhost:9000/mediums/takeo_fukuda.jpg',0,47,0,0),(48,'takeo_miki.jpg','http://localhost:9000/mediums/takeo_miki.jpg',137,185,40279,'http://localhost:9000/mediums/takeo_miki.jpg',0,48,0,0),(49,'tanzan_ishibashi.jpg','http://localhost:9000/mediums/tanzan_ishibashi.jpg',160,196,13115,'http://localhost:9000/mediums/tanzan_ishibashi.jpg',0,49,0,0),(50,'taro_aso.jpg','http://localhost:9000/mediums/taro_aso.jpg',160,213,10657,'http://localhost:9000/mediums/taro_aso.jpg',0,50,0,0),(51,'taro_katsura.jpg','http://localhost:9000/mediums/taro_katsura.jpg',160,213,11514,'http://localhost:9000/mediums/taro_katsura.jpg',0,51,0,0),(52,'terauchi_masatake.jpg','http://localhost:9000/mediums/terauchi_masatake.jpg',160,208,11127,'http://localhost:9000/mediums/terauchi_masatake.jpg',0,52,0,0),(53,'tetsu_katayama.jpg','http://localhost:9000/mediums/tetsu_katayama.jpg',160,196,8235,'http://localhost:9000/mediums/tetsu_katayama.jpg',0,53,0,0),(54,'tomiichi_murayama.jpg','http://localhost:9000/mediums/tomiichi_murayama.jpg',132,189,30215,'http://localhost:9000/mediums/tomiichi_murayama.jpg',0,54,0,0),(55,'tomosaburo_kato.jpg','http://localhost:9000/mediums/tomosaburo_kato.jpg',160,213,6824,'http://localhost:9000/mediums/tomosaburo_kato.jpg',0,55,0,0),(56,'toshiki_kaifu.jpg','http://localhost:9000/mediums/toshiki_kaifu.jpg',160,195,9269,'http://localhost:9000/mediums/toshiki_kaifu.jpg',0,56,0,0),(57,'tsutomu_hata.jpg','http://localhost:9000/mediums/tsutomu_hata.jpg',137,191,31719,'http://localhost:9000/mediums/tsutomu_hata.jpg',0,57,0,0),(58,'tsuyoshi_inukai.jpg','http://localhost:9000/mediums/tsuyoshi_inukai.jpg',160,203,5640,'http://localhost:9000/mediums/tsuyoshi_inukai.jpg',0,58,0,0),(59,'yasuhiro_nakasone.jpg','http://localhost:9000/mediums/yasuhiro_nakasone.jpg',160,217,11583,'http://localhost:9000/mediums/yasuhiro_nakasone.jpg',0,59,0,0),(60,'yasuo_fukuda.jpg','http://localhost:9000/mediums/yasuo_fukuda.jpg',129,187,31413,'http://localhost:9000/mediums/yasuo_fukuda.jpg',0,60,0,0),(61,'yoshihiko_noda.jpg','http://localhost:9000/mediums/yoshihiko_noda.jpg',160,213,8076,'http://localhost:9000/mediums/yoshihiko_noda.jpg',0,61,0,0),(62,'yoshiro_mori.jpg','http://localhost:9000/mediums/yoshiro_mori.jpg',160,203,15200,'http://localhost:9000/mediums/yoshiro_mori.jpg',0,62,0,0),(63,'yukio_hatoyama.jpg','http://localhost:9000/mediums/yukio_hatoyama.jpg',160,213,16380,'http://localhost:9000/mediums/yukio_hatoyama.jpg',0,63,0,0),(64,'zenko_suzuki.jpg','http://localhost:9000/mediums/zenko_suzuki.jpg',139,197,13934,'http://localhost:9000/mediums/zenko_suzuki.jpg',0,64,0,0),(65,'hideyosi_toyotomi.jpg','http://localhost:9000/mediums/hideyosi_toyotomi.jpg',360,509,33140,'http://localhost:9000/mediums/hideyosi_toyotomi.jpg',0,43,0,0),(66,'nobunaga_oda.jpg','http://localhost:9000/mediums/nobunaga_oda.jpg',342,400,91703,'http://localhost:9000/mediums/nobunaga_oda.jpg',0,1,0,0),(67,'ieyasu_tokugawa.jpg','http://localhost:9000/mediums/ieyasu_tokugawa.jpg',440,459,53830,'http://localhost:9000/mediums/ieyasu_tokugawa.jpg',0,1,0,0),(68,'hideyosi_toyotomi.jpg','http://localhost:9000/mediums/hideyosi_toyotomi.jpg',360,509,33140,'http://localhost:9000/mediums/hideyosi_toyotomi.jpg',0,1,0,0),(69,'yasuke.jpg','http://localhost:9000/mediums/yasuke.jpg',400,533,84335,'http://localhost:9000/mediums/yasuke.jpg',0,1,0,0);
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

-- Dump completed on 2018-12-08 14:03:10
