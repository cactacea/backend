-- MySQL dump 10.13  Distrib 8.0.13, for macos10.14 (x86_64)
--
-- Host: ${hostName}    Database: cactacea
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
INSERT INTO `account_feeds` VALUES (1,1,0,43,20181208144646),(50,1,0,43,20181208144646),(43,2,0,1,20181208144646),(43,3,0,50,20181208144646),(43,4,0,1,20181208144646),(43,5,0,1,20181208144646);
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
INSERT INTO `accounts` VALUES (1,'aritomo_yamagata','aritomo_yamagata','f737bd52007ca0dc9abb86b5567e8372',1,1,'http://${hostName}:9000/mediums/aritomo_yamagata.jpg',1,0,3,NULL,NULL,NULL,NULL,0,NULL),(2,'eisaku_sato','eisaku_sato','f737bd52007ca0dc9abb86b5567e8372',1,2,'http://${hostName}:9000/mediums/eisaku_sato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(3,'giichi_tanaka','giichi_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,3,'http://${hostName}:9000/mediums/giichi_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(4,'gonbei_yamamoto','gonbei_yamamoto','f737bd52007ca0dc9abb86b5567e8372',1,4,'http://${hostName}:9000/mediums/gonbei_yamamoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(5,'hayato_ikeda','hayato_ikeda','f737bd52007ca0dc9abb86b5567e8372',1,5,'http://${hostName}:9000/mediums/hayato_ikeda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(6,'hideki_tojo','hideki_tojo','f737bd52007ca0dc9abb86b5567e8372',1,6,'http://${hostName}:9000/mediums/hideki_tojo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(7,'hirobumi_ito','hirobumi_ito','f737bd52007ca0dc9abb86b5567e8372',1,7,'http://${hostName}:9000/mediums/hirobumi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(8,'hitoshi_ashida','hitoshi_ashida','f737bd52007ca0dc9abb86b5567e8372',1,8,'http://${hostName}:9000/mediums/hitoshi_ashida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(9,'ichiro_hatoyama','ichiro_hatoyama','f737bd52007ca0dc9abb86b5567e8372',1,9,'http://${hostName}:9000/mediums/ichiro_hatoyama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(10,'junichiro_koizumi','junichiro_koizumi','f737bd52007ca0dc9abb86b5567e8372',1,10,'http://${hostName}:9000/mediums/junichiro_koizumi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(11,'kakuei_tanaka','kakuei_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,11,'http://${hostName}:9000/mediums/kakuei_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(12,'kantaro_suzuki','kantaro_suzuki','f737bd52007ca0dc9abb86b5567e8372',1,12,'http://${hostName}:9000/mediums/kantaro_suzuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(13,'keigo_kiyoura','keigo_kiyoura','f737bd52007ca0dc9abb86b5567e8372',1,13,'http://${hostName}:9000/mediums/keigo_kiyoura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(14,'keisuke_okada','keisuke_okada','f737bd52007ca0dc9abb86b5567e8372',1,14,'http://${hostName}:9000/mediums/keisuke_okada.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(15,'keizo_obuchi','keizo_obuchi','f737bd52007ca0dc9abb86b5567e8372',1,15,'http://${hostName}:9000/mediums/keizo_obuchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(16,'kiichi_miyazawa','kiichi_miyazawa','f737bd52007ca0dc9abb86b5567e8372',1,16,'http://${hostName}:9000/mediums/kiichi_miyazawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(17,'kiichiro_hiranumra','kiichiro_hiranumra','f737bd52007ca0dc9abb86b5567e8372',1,17,'http://${hostName}:9000/mediums/kiichiro_hiranumra.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(18,'kijuro_shidehara','kijuro_shidehara','f737bd52007ca0dc9abb86b5567e8372',1,18,'http://${hostName}:9000/mediums/kijuro_shidehara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(19,'kinmochi_saionji','kinmochi_saionji','f737bd52007ca0dc9abb86b5567e8372',1,19,'http://${hostName}:9000/mediums/kinmochi_saionji.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(20,'kiyotaka_kuroda','kiyotaka_kuroda','f737bd52007ca0dc9abb86b5567e8372',1,20,'http://${hostName}:9000/mediums/kiyotaka_kuroda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(21,'kohki_hirota','kohki_hirota','f737bd52007ca0dc9abb86b5567e8372',1,21,'http://${hostName}:9000/mediums/kohki_hirota.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(22,'korekiyo_takahashi','korekiyo_takahashi','f737bd52007ca0dc9abb86b5567e8372',1,22,'http://${hostName}:9000/mediums/korekiyo_takahashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(23,'kosai_uchida','kosai_uchida','f737bd52007ca0dc9abb86b5567e8372',1,23,'http://${hostName}:9000/mediums/kosai_uchida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(24,'kuniaki_koiso','kuniaki_koiso','f737bd52007ca0dc9abb86b5567e8372',1,24,'http://${hostName}:9000/mediums/kuniaki_koiso.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(25,'makoto_saito','makoto_saito','f737bd52007ca0dc9abb86b5567e8372',1,25,'http://${hostName}:9000/mediums/makoto_saito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(26,'masayoshi_ito','masayoshi_ito','f737bd52007ca0dc9abb86b5567e8372',1,26,'http://${hostName}:9000/mediums/masayoshi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(27,'masayoshi_matsutaka','masayoshi_matsutaka','f737bd52007ca0dc9abb86b5567e8372',1,27,'http://${hostName}:9000/mediums/masayoshi_matsutaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(28,'masayoshi_ohira','masayoshi_ohira','f737bd52007ca0dc9abb86b5567e8372',1,28,'http://${hostName}:9000/mediums/masayoshi_ohira.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(29,'mitsumasa_yonai','mitsumasa_yonai','f737bd52007ca0dc9abb86b5567e8372',1,29,'http://${hostName}:9000/mediums/mitsumasa_yonai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(30,'morihiro_hosokawa','morihiro_hosokawa','f737bd52007ca0dc9abb86b5567e8372',1,30,'http://${hostName}:9000/mediums/morihiro_hosokawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(31,'naoto_kan','naoto_kan','f737bd52007ca0dc9abb86b5567e8372',1,31,'http://${hostName}:9000/mediums/naoto_kan.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(32,'naruhiko_higashikuni','naruhiko_higashikuni','f737bd52007ca0dc9abb86b5567e8372',1,32,'http://${hostName}:9000/mediums/naruhiko_higashikuni.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(33,'noboru_takeshita','noboru_takeshita','f737bd52007ca0dc9abb86b5567e8372',1,33,'http://${hostName}:9000/mediums/noboru_takeshita.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(34,'nobusuke_kishi','nobusuke_kishi','f737bd52007ca0dc9abb86b5567e8372',1,34,'http://${hostName}:9000/mediums/nobusuke_kishi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(35,'nobuyuki_abe','nobuyuki_abe','f737bd52007ca0dc9abb86b5567e8372',1,35,'http://${hostName}:9000/mediums/nobuyuki_abe.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(36,'okuma_shigenobu','okuma_shigenobu','f737bd52007ca0dc9abb86b5567e8372',1,36,'http://${hostName}:9000/mediums/okuma_shigenobu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(37,'osachi_hamaguchi','osachi_hamaguchi','f737bd52007ca0dc9abb86b5567e8372',1,37,'http://${hostName}:9000/mediums/osachi_hamaguchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(38,'reijiro_wakatsuki','reijiro_wakatsuki','f737bd52007ca0dc9abb86b5567e8372',1,38,'http://${hostName}:9000/mediums/reijiro_wakatsuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(39,'ryutaro_hashimoto','ryutaro_hashimoto','f737bd52007ca0dc9abb86b5567e8372',1,39,'http://${hostName}:9000/mediums/ryutaro_hashimoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(40,'sanetomi_sanjo','sanetomi_sanjo','f737bd52007ca0dc9abb86b5567e8372',1,40,'http://${hostName}:9000/mediums/sanetomi_sanjo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(41,'senjuro_hayashi','senjuro_hayashi','f737bd52007ca0dc9abb86b5567e8372',1,41,'http://${hostName}:9000/mediums/senjuro_hayashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(42,'shigeru_yoshida','shigeru_yoshida','f737bd52007ca0dc9abb86b5567e8372',1,42,'http://${hostName}:9000/mediums/shigeru_yoshida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(43,'shinzo_abe','shinzo_abe','f737bd52007ca0dc9abb86b5567e8372',2,43,'http://${hostName}:9000/mediums/shinzo_abe.jpg',2,0,1,NULL,NULL,NULL,NULL,0,NULL),(44,'sosuke_uno','sosuke_uno','f737bd52007ca0dc9abb86b5567e8372',1,44,'http://${hostName}:9000/mediums/sosuke_uno.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(45,'takaaki_kato','takaaki_kato','f737bd52007ca0dc9abb86b5567e8372',1,45,'http://${hostName}:9000/mediums/takaaki_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(46,'takashi_hara','takashi_hara','f737bd52007ca0dc9abb86b5567e8372',1,46,'http://${hostName}:9000/mediums/takashi_hara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(47,'takeo_fukuda','takeo_fukuda','f737bd52007ca0dc9abb86b5567e8372',1,47,'http://${hostName}:9000/mediums/takeo_fukuda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(48,'takeo_miki','takeo_miki','f737bd52007ca0dc9abb86b5567e8372',1,48,'http://${hostName}:9000/mediums/takeo_miki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(49,'tanzan_ishibashi','tanzan_ishibashi','f737bd52007ca0dc9abb86b5567e8372',1,49,'http://${hostName}:9000/mediums/tanzan_ishibashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(50,'taro_aso','taro_aso','f737bd52007ca0dc9abb86b5567e8372',1,50,'http://${hostName}:9000/mediums/taro_aso.jpg',1,0,1,NULL,NULL,NULL,NULL,0,NULL),(51,'taro_katsura','taro_katsura','f737bd52007ca0dc9abb86b5567e8372',1,51,'http://${hostName}:9000/mediums/taro_katsura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(52,'terauchi_masatake','terauchi_masatake','f737bd52007ca0dc9abb86b5567e8372',1,52,'http://${hostName}:9000/mediums/terauchi_masatake.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(53,'tetsu_katayama','tetsu_katayama','f737bd52007ca0dc9abb86b5567e8372',1,53,'http://${hostName}:9000/mediums/tetsu_katayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(54,'tomiichi_murayama','tomiichi_murayama','f737bd52007ca0dc9abb86b5567e8372',1,54,'http://${hostName}:9000/mediums/tomiichi_murayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(55,'tomosaburo_kato','tomosaburo_kato','f737bd52007ca0dc9abb86b5567e8372',1,55,'http://${hostName}:9000/mediums/tomosaburo_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(56,'toshiki_kaifu','toshiki_kaifu','f737bd52007ca0dc9abb86b5567e8372',1,56,'http://${hostName}:9000/mediums/toshiki_kaifu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(57,'tsutomu_hata','tsutomu_hata','f737bd52007ca0dc9abb86b5567e8372',1,57,'http://${hostName}:9000/mediums/tsutomu_hata.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(58,'tsuyoshi_inukai','tsuyoshi_inukai','f737bd52007ca0dc9abb86b5567e8372',1,58,'http://${hostName}:9000/mediums/tsuyoshi_inukai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(59,'yasuhiro_nakasone','yasuhiro_nakasone','f737bd52007ca0dc9abb86b5567e8372',2,59,'http://${hostName}:9000/mediums/yasuhiro_nakasone.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(60,'yasuo_fukuda','yasuo_fukuda','f737bd52007ca0dc9abb86b5567e8372',2,60,'http://${hostName}:9000/mediums/yasuo_fukuda.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(61,'yoshihiko_noda','yoshihiko_noda','f737bd52007ca0dc9abb86b5567e8372',2,61,'http://${hostName}:9000/mediums/yoshihiko_noda.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(62,'yoshiro_mori','yoshiro_mori','f737bd52007ca0dc9abb86b5567e8372',2,62,'http://${hostName}:9000/mediums/yoshiro_mori.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(63,'yukio_hatoyama','yukio_hatoyama','f737bd52007ca0dc9abb86b5567e8372',2,63,'http://${hostName}:9000/mediums/yukio_hatoyama.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(64,'zenko_suzuki','zenko_suzuki','f737bd52007ca0dc9abb86b5567e8372',2,64,'http://${hostName}:9000/mediums/zenko_suzuki.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL);
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
INSERT INTO `comments` VALUES (1,'When he was four Ieyasu was sent as a hostage to secure an alliance between his clan and the neighbouring Imagawa clan. He was raised at their court and given the education suitable for a nobleman.',3,NULL,0,59,0,0,1,1544280406693),(2,'In 1567 Ieyasu, whose father\'s death had left him as leader of the Matsudaira, allied with Oda Nobunaga, a powerful neighbour.',3,NULL,0,60,0,0,1,1544280406743),(3,'It was at this time that he changed his name from Matsudaira to Tokugawa, which was the name of the area from which his family originated.',3,NULL,0,61,0,0,1,1544280406779),(4,'He also changed his personal name to Ieyasu, so he was now known as Tokugawa Ieyasu.',3,NULL,0,62,0,0,1,1544280406810),(5,'When he was four Ieyasu was sent as a hostage to secure an alliance between his clan and the neighbouring Imagawa clan.',3,NULL,0,63,0,0,1,1544280406835),(6,'He was raised at their court and given the education suitable for a nobleman.',3,NULL,0,64,0,0,1,1544280406859);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `devices`
--

LOCK TABLES `devices` WRITE;
/*!40000 ALTER TABLE `devices` DISABLE KEYS */;
INSERT INTO `devices` VALUES (1,1,'d8c1be5d-a3a9-4a06-ba87-8a1abd47970b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(2,2,'3f11f85a-c5de-4878-9da0-b143ddc3f840',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(3,3,'deea9878-94ae-4f07-bed6-9b9aa70fc328',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(4,4,'cfb0a991-417d-47b3-be62-3cc931c02519',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(5,5,'601cc228-8ad6-4661-a134-b3e4f15c07bd',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(6,6,'455bcbbd-84ae-4ec9-81f1-a291ad0ffaf0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(7,7,'75d3179f-4e1d-4113-9d37-ffcd5b510ff8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(8,8,'4d893d01-d5b5-4263-8125-1ea525621b98',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(9,9,'b5c734da-5009-4087-99d0-dc2680fdde37',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(10,10,'dd75dc75-e39b-4620-8457-ca0e6d575660',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(11,11,'7b7f9c98-5e4a-4752-a1c8-b3293220e675',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(12,12,'f772fcad-13b1-4061-b8fe-89bf9aa1689a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(13,13,'d720e75d-52fb-4935-ab70-9a549a93c09c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(14,14,'e7d1669e-87b9-4bb1-8162-d8d9713a5848',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(15,15,'5b2ddb32-3766-4f67-bd25-4f6aacc4c39e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(16,16,'36816844-c3e9-4224-adc7-ec1dbbf75be7',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(17,17,'74aaa50e-2acc-4b9b-8381-1f9f6915afee',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(18,18,'ab6fb22d-9eda-474a-9a40-152af719a007',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(19,19,'4bbd2401-4e57-4eea-8aed-637205f160f8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(20,20,'807f04aa-fe65-442b-ae54-ffc64b0c5679',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(21,21,'63c28458-dfbc-4820-9c8c-49f2d3aa7e15',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(22,22,'1c7765c5-f998-4a8f-a41c-ad4a5374ddb8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(23,23,'6f09f172-0145-4416-a3b5-f4be9012dc2d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(24,24,'0717083a-cf88-4bf8-8dee-11c9e17f8444',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(25,25,'d45a0edc-020d-407d-a7fa-095d583f2db2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(26,26,'beb36553-23a3-4c63-a499-d66a7bce440d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(27,27,'a2a3d7b2-b632-4561-a4af-33e2f189f674',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(28,28,'9eea5d72-25c9-447f-93d0-dbc223d48218',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(29,29,'f1dc7a62-4a28-4c12-8927-ef7924d23a5a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(30,30,'0d2b8213-aa49-42fe-93ee-5ca8b33b4abe',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(31,31,'a4ac7b1e-d1a8-4c91-9910-6795fe2d9fa0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(32,32,'26d67012-78b0-489d-9feb-6338ebba6776',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(33,33,'14215539-12b7-42e2-8897-aa95f846db62',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(34,34,'52567368-f42f-4941-b210-049c370bc2ea',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(35,35,'3542d146-4e7d-4f26-95ec-25ea689456f6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(36,36,'cb327adc-b9cb-4414-99af-b5733982359c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(37,37,'f79c96e7-f531-4bec-bba1-005d8d72f0a8',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(38,38,'0d2a1eb9-00d0-4aed-aa6b-997ee5cfec78',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(39,39,'6a45a1d3-193a-4cd5-b91b-79c37227c79d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(40,40,'fcd862b8-8467-4dce-88f1-a34b4825e5c5',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(41,41,'82fc90ba-a150-4662-bb1d-97c526fe4257',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(42,42,'554d5233-cda0-4ace-8318-097968994b81',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(43,43,'20f65a1a-0313-4670-b11d-523eb532d3ac',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(44,44,'b76da8ea-e2d7-4a3c-a8b1-df5a95d487ad',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(45,45,'c1cb35f5-a57f-41f8-80df-1dec6db198bf',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(46,46,'2a882920-188a-4c14-bd33-2c48bbb568ef',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(47,47,'b6f9cce5-33b4-42c2-92d5-dfe5cf7ed58e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(48,48,'901e3a18-bc71-4699-a7b0-c8b096db08ac',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(49,49,'c92b9f99-b3a6-4a75-ba34-eb54cae8aa5e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(50,50,'e1234082-d0ea-417f-9c39-ded2a117d3ff',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(51,51,'5c35d506-3261-4f02-9452-dca566fb5a04',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(52,52,'0de6dc55-cdf1-41ca-b990-a9ae0516438e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(53,53,'7cf63a86-d0d3-475e-a08c-90086c6b1a26',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(54,54,'84e3461a-aa6b-4301-9009-4ddf7d90ce7a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(55,55,'53e637c8-68d8-4116-9741-c9d5fc22248b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(56,56,'e40603df-cd9b-4f22-af95-4839a0d9077e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(57,57,'b98a5112-c43f-4d75-be32-114c7530b855',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(58,58,'8dcf7ce2-1e48-4714-a1bc-bc3d67f19517',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(59,59,'3adec5af-0892-44d0-803b-ea4ca422bab0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(60,60,'10a95711-1d75-4e24-900b-051baac16994',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(61,61,'7acf4c70-89f3-4b40-9b51-65e2589c8867',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(62,62,'06ccb333-8994-4865-b94a-c11e27ee976d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(63,63,'a5c18fbd-e9e6-48fa-81dc-13ff4cb69ea0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(64,64,'0f2f4bb0-ce58-4dab-817e-e72e8e2fd3de',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1');
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
INSERT INTO `feeds` VALUES (1,'Uesugi Kenshin (上杉 謙信, February 18, 1530 – April 19, 1578[1]) was a daimyō who was born as Nagao Kagetora,[2] and after the adoption into the Uesugi clan, ruled Echigo Province in the Sengoku period of Japan.[3] He was one of the most powerful daimyōs of the Sengoku period. While chiefly remembered for his prowess on the battlefield, Kenshin is also regarded as an extremely skillful administrator who fostered the growth of local industries and trade; his rule saw a marked rise in the standard of living of Echigo.',43,0,0,0,0,0,NULL,0,1544280406359),(2,'Oda Nobunaga (help·info), June 23, 1534 – June 21, 1582) was a powerful daimyō (feudal lord) of Japan in the late 16th century who attempted to unify Japan during the late Sengoku period, and successfully gained control over most of Honshu. Nobunaga is regarded as one of three unifiers of Japan along with his retainers Toyotomi Hideyoshi and Tokugawa Ieyasu. During his later life, Nobunaga was widely known for most brutal suppression of determined opponents, eliminating those who by principle refused to cooperate or yield to his demands. His reign was noted for innovative military tactics, fostering free trade, and encouraging the start of the Momoyama historical art period. He was killed when his retainer Akechi Mitsuhide rebelled against him at Honnō-ji.',1,0,0,0,0,0,NULL,0,1544280406481),(3,'Tokugawa Ieyasu (徳川家康, January 30, 1543 – June 1, 1616) was the founder and first shōgun of the Tokugawa shogunate of Japan, which effectively ruled Japan from the Battle of Sekigahara in 1600 until the Meiji Restoration in 1868. Ieyasu seized power in 1600, received appointment as shōgun in 1603, and abdicated from office in 1605, but remained in power until his death in 1616. His given name is sometimes spelled Iyeyasu,[1][2] according to the historical pronunciation of the kana character he. Ieyasu was posthumously enshrined at Nikkō Tōshō-gū with the name Tōshō Daigongen (東照大権現). He was one of the three unifiers of Japan, along with his former lord Nobunaga and Toyotomi Hideyoshi.',50,0,6,0,0,0,NULL,0,1544280406535),(4,'Toyotomi Hideyoshi (豊臣 秀吉, March 17, 1537 – September 18, 1598) was a preeminent daimyō, warrior, general, samurai, and politician of the Sengoku period[1] who is regarded as Japan\'s second \"great unifier\".[2] He succeeded his former liege lord, Oda Nobunaga, and brought an end to the Warring Lords period. The period of his rule is often called the Momoyama period, named after Hideyoshi\'s castle. After his death, his young son Hideyori was displaced by Tokugawa Ieyasu.',1,0,0,0,0,0,NULL,0,1544280406577),(5,'Yasuke (variously rendered as 弥助 or 弥介, 彌助 or 彌介 in different sources.[1]) (b. c. 1555–1590) was a Samurai of African origin who served under the Japanese hegemon and warlord Oda Nobunaga in 1581 and 1582.',1,0,0,2,0,0,NULL,0,1544280406628);
/*!40000 ALTER TABLE `feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `followers`
--

LOCK TABLES `followers` WRITE;
/*!40000 ALTER TABLE `followers` DISABLE KEYS */;
INSERT INTO `followers` VALUES (1,1,43,1544280404431),(2,2,43,1544280404490),(3,3,43,1544280404525),(4,4,43,1544280404557),(5,5,43,1544280404587),(6,6,43,1544280404611),(7,7,43,1544280404635),(8,8,43,1544280404657),(9,9,43,1544280404684),(10,10,43,1544280404710),(11,11,43,1544280404737),(12,12,43,1544280404766),(13,13,43,1544280404790),(14,14,43,1544280404813),(15,15,43,1544280404836),(16,16,43,1544280404862),(17,17,43,1544280404892),(18,18,43,1544280404917),(19,19,43,1544280404942),(20,20,43,1544280404970),(21,21,43,1544280404997),(22,22,43,1544280405023),(23,23,43,1544280405054),(24,24,43,1544280405080),(25,25,43,1544280405105),(26,26,43,1544280405130),(27,27,43,1544280405160),(28,28,43,1544280405189),(29,29,43,1544280405215),(30,30,43,1544280405241),(31,31,43,1544280405272),(32,32,43,1544280405306),(33,33,43,1544280405339),(34,34,43,1544280405365),(35,35,43,1544280405394),(36,36,43,1544280405418),(37,37,43,1544280405441),(38,38,43,1544280405463),(39,39,43,1544280405485),(40,40,43,1544280405510),(41,41,43,1544280405533),(42,42,43,1544280405558),(43,44,43,1544280405583),(44,45,43,1544280405610),(45,46,43,1544280405637),(46,47,43,1544280405661),(47,48,43,1544280405683),(48,49,43,1544280405704),(49,50,43,1544280405726),(50,51,43,1544280405748),(51,52,43,1544280405778),(52,53,43,1544280405804),(53,54,43,1544280405827),(54,55,43,1544280405854),(55,56,43,1544280405885),(56,57,43,1544280405909),(57,58,43,1544280405931),(58,59,43,1544280405953),(59,60,43,1544280405977),(60,61,43,1544280406000),(61,62,43,1544280406024),(62,63,43,1544280406054),(63,64,43,1544280406086),(64,59,1,1544280406110),(65,60,1,1544280406134),(66,61,1,1544280406158),(67,62,1,1544280406185),(68,63,1,1544280406210),(69,64,1,1544280406233),(70,43,1,1544280406256),(71,43,50,1544280406279);
/*!40000 ALTER TABLE `followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `follows`
--

LOCK TABLES `follows` WRITE;
/*!40000 ALTER TABLE `follows` DISABLE KEYS */;
INSERT INTO `follows` VALUES (1,43,1,1544280404407),(2,43,2,1544280404480),(3,43,3,1544280404521),(4,43,4,1544280404550),(5,43,5,1544280404583),(6,43,6,1544280404606),(7,43,7,1544280404632),(8,43,8,1544280404654),(9,43,9,1544280404679),(10,43,10,1544280404706),(11,43,11,1544280404732),(12,43,12,1544280404761),(13,43,13,1544280404785),(14,43,14,1544280404809),(15,43,15,1544280404832),(16,43,16,1544280404856),(17,43,17,1544280404887),(18,43,18,1544280404913),(19,43,19,1544280404937),(20,43,20,1544280404965),(21,43,21,1544280404992),(22,43,22,1544280405018),(23,43,23,1544280405050),(24,43,24,1544280405075),(25,43,25,1544280405101),(26,43,26,1544280405126),(27,43,27,1544280405154),(28,43,28,1544280405184),(29,43,29,1544280405211),(30,43,30,1544280405236),(31,43,31,1544280405267),(32,43,32,1544280405300),(33,43,33,1544280405333),(34,43,34,1544280405360),(35,43,35,1544280405389),(36,43,36,1544280405414),(37,43,37,1544280405437),(38,43,38,1544280405460),(39,43,39,1544280405481),(40,43,40,1544280405505),(41,43,41,1544280405529),(42,43,42,1544280405554),(43,43,44,1544280405578),(44,43,45,1544280405605),(45,43,46,1544280405632),(46,43,47,1544280405658),(47,43,48,1544280405679),(48,43,49,1544280405701),(49,43,50,1544280405723),(50,43,51,1544280405744),(51,43,52,1544280405772),(52,43,53,1544280405800),(53,43,54,1544280405822),(54,43,55,1544280405849),(55,43,56,1544280405881),(56,43,57,1544280405905),(57,43,58,1544280405928),(58,43,59,1544280405949),(59,43,60,1544280405974),(60,43,61,1544280405996),(61,43,62,1544280406020),(62,43,63,1544280406050),(63,43,64,1544280406082),(64,1,59,1544280406107),(65,1,60,1544280406130),(66,1,61,1544280406154),(67,1,62,1544280406180),(68,1,63,1544280406205),(69,1,64,1544280406229),(70,1,43,1544280406252),(71,50,43,1544280406275);
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
INSERT INTO `mediums` VALUES (1,'aritomo_yamagata.jpg','http://${hostName}:9000/mediums/aritomo_yamagata.jpg',160,213,13192,'http://${hostName}:9000/mediums/aritomo_yamagata.jpg',0,1,0,0),(2,'eisaku_sato.jpg','http://${hostName}:9000/mediums/eisaku_sato.jpg',160,227,10510,'http://${hostName}:9000/mediums/eisaku_sato.jpg',0,2,0,0),(3,'giichi_tanaka.jpg','http://${hostName}:9000/mediums/giichi_tanaka.jpg',160,198,7181,'http://${hostName}:9000/mediums/giichi_tanaka.jpg',0,3,0,0),(4,'gonbei_yamamoto.jpg','http://${hostName}:9000/mediums/gonbei_yamamoto.jpg',160,210,9566,'http://${hostName}:9000/mediums/gonbei_yamamoto.jpg',0,4,0,0),(5,'hayato_ikeda.jpg','http://${hostName}:9000/mediums/hayato_ikeda.jpg',160,213,5901,'http://${hostName}:9000/mediums/hayato_ikeda.jpg',0,5,0,0),(6,'hideki_tojo.jpg','http://${hostName}:9000/mediums/hideki_tojo.jpg',160,215,7435,'http://${hostName}:9000/mediums/hideki_tojo.jpg',0,6,0,0),(7,'hirobumi_ito.jpg','http://${hostName}:9000/mediums/hirobumi_ito.jpg',160,218,9141,'http://${hostName}:9000/mediums/hirobumi_ito.jpg',0,7,0,0),(8,'hitoshi_ashida.jpg','http://${hostName}:9000/mediums/hitoshi_ashida.jpg',160,194,12280,'http://${hostName}:9000/mediums/hitoshi_ashida.jpg',0,8,0,0),(9,'ichiro_hatoyama.jpg','http://${hostName}:9000/mediums/ichiro_hatoyama.jpg',160,195,9804,'http://${hostName}:9000/mediums/ichiro_hatoyama.jpg',0,9,0,0),(10,'junichiro_koizumi.jpg','http://${hostName}:9000/mediums/junichiro_koizumi.jpg',131,175,53624,'http://${hostName}:9000/mediums/junichiro_koizumi.jpg',0,10,0,0),(11,'kakuei_tanaka.jpg','http://${hostName}:9000/mediums/kakuei_tanaka.jpg',148,183,34578,'http://${hostName}:9000/mediums/kakuei_tanaka.jpg',0,11,0,0),(12,'kantaro_suzuki.jpg','http://${hostName}:9000/mediums/kantaro_suzuki.jpg',160,201,5381,'http://${hostName}:9000/mediums/kantaro_suzuki.jpg',0,12,0,0),(13,'keigo_kiyoura.jpg','http://${hostName}:9000/mediums/keigo_kiyoura.jpg',160,213,11265,'http://${hostName}:9000/mediums/keigo_kiyoura.jpg',0,13,0,0),(14,'keisuke_okada.jpg','http://${hostName}:9000/mediums/keisuke_okada.jpg',160,213,10460,'http://${hostName}:9000/mediums/keisuke_okada.jpg',0,14,0,0),(15,'keizo_obuchi.jpg','http://${hostName}:9000/mediums/keizo_obuchi.jpg',160,213,11264,'http://${hostName}:9000/mediums/keizo_obuchi.jpg',0,15,0,0),(16,'kiichi_miyazawa.jpg','http://${hostName}:9000/mediums/kiichi_miyazawa.jpg',160,213,15500,'http://${hostName}:9000/mediums/kiichi_miyazawa.jpg',0,16,0,0),(17,'kiichiro_hiranumra.jpg','http://${hostName}:9000/mediums/kiichiro_hiranumra.jpg',160,219,8780,'http://${hostName}:9000/mediums/kiichiro_hiranumra.jpg',0,17,0,0),(18,'kijuro_shidehara.jpg','http://${hostName}:9000/mediums/kijuro_shidehara.jpg',160,211,12099,'http://${hostName}:9000/mediums/kijuro_shidehara.jpg',0,18,0,0),(19,'kinmochi_saionji.jpg','http://${hostName}:9000/mediums/kinmochi_saionji.jpg',160,198,9571,'http://${hostName}:9000/mediums/kinmochi_saionji.jpg',0,19,0,0),(20,'kiyotaka_kuroda.jpg','http://${hostName}:9000/mediums/kiyotaka_kuroda.jpg',160,207,11504,'http://${hostName}:9000/mediums/kiyotaka_kuroda.jpg',0,20,0,0),(21,'kohki_hirota.jpg','http://${hostName}:9000/mediums/kohki_hirota.jpg',160,213,7291,'http://${hostName}:9000/mediums/kohki_hirota.jpg',0,21,0,0),(22,'korekiyo_takahashi.jpg','http://${hostName}:9000/mediums/korekiyo_takahashi.jpg',160,200,6941,'http://${hostName}:9000/mediums/korekiyo_takahashi.jpg',0,22,0,0),(23,'kosai_uchida.jpg','http://${hostName}:9000/mediums/kosai_uchida.jpg',160,213,9038,'http://${hostName}:9000/mediums/kosai_uchida.jpg',0,23,0,0),(24,'kuniaki_koiso.jpg','http://${hostName}:9000/mediums/kuniaki_koiso.jpg',160,213,8478,'http://${hostName}:9000/mediums/kuniaki_koiso.jpg',0,24,0,0),(25,'makoto_saito.jpg','http://${hostName}:9000/mediums/makoto_saito.jpg',160,213,7271,'http://${hostName}:9000/mediums/makoto_saito.jpg',0,25,0,0),(26,'masayoshi_ito.jpg','http://${hostName}:9000/mediums/masayoshi_ito.jpg',160,219,6479,'http://${hostName}:9000/mediums/masayoshi_ito.jpg',0,26,0,0),(27,'masayoshi_matsutaka.jpg','http://${hostName}:9000/mediums/masayoshi_matsutaka.jpg',160,213,11710,'http://${hostName}:9000/mediums/masayoshi_matsutaka.jpg',0,27,0,0),(28,'masayoshi_ohira.jpg','http://${hostName}:9000/mediums/masayoshi_ohira.jpg',160,215,11323,'http://${hostName}:9000/mediums/masayoshi_ohira.jpg',0,28,0,0),(29,'mitsumasa_yonai.jpg','http://${hostName}:9000/mediums/mitsumasa_yonai.jpg',160,204,5076,'http://${hostName}:9000/mediums/mitsumasa_yonai.jpg',0,29,0,0),(30,'morihiro_hosokawa.jpg','http://${hostName}:9000/mediums/morihiro_hosokawa.jpg',135,197,34210,'http://${hostName}:9000/mediums/morihiro_hosokawa.jpg',0,30,0,0),(31,'naoto_kan.jpg','http://${hostName}:9000/mediums/naoto_kan.jpg',160,214,10468,'http://${hostName}:9000/mediums/naoto_kan.jpg',0,31,0,0),(32,'naruhiko_higashikuni.jpg','http://${hostName}:9000/mediums/naruhiko_higashikuni.jpg',160,193,11366,'http://${hostName}:9000/mediums/naruhiko_higashikuni.jpg',0,32,0,0),(33,'noboru_takeshita.jpg','http://${hostName}:9000/mediums/noboru_takeshita.jpg',149,195,14112,'http://${hostName}:9000/mediums/noboru_takeshita.jpg',0,33,0,0),(34,'nobusuke_kishi.jpg','http://${hostName}:9000/mediums/nobusuke_kishi.jpg',126,185,15176,'http://${hostName}:9000/mediums/nobusuke_kishi.jpg',0,34,0,0),(35,'nobuyuki_abe.jpg','http://${hostName}:9000/mediums/nobuyuki_abe.jpg',160,231,9779,'http://${hostName}:9000/mediums/nobuyuki_abe.jpg',0,35,0,0),(36,'okuma_shigenobu.jpg','http://${hostName}:9000/mediums/okuma_shigenobu.jpg',160,213,10802,'http://${hostName}:9000/mediums/okuma_shigenobu.jpg',0,36,0,0),(37,'osachi_hamaguchi.jpg','http://${hostName}:9000/mediums/osachi_hamaguchi.jpg',160,200,7364,'http://${hostName}:9000/mediums/osachi_hamaguchi.jpg',0,37,0,0),(38,'reijiro_wakatsuki.jpg','http://${hostName}:9000/mediums/reijiro_wakatsuki.jpg',160,198,6621,'http://${hostName}:9000/mediums/reijiro_wakatsuki.jpg',0,38,0,0),(39,'ryutaro_hashimoto.jpg','http://${hostName}:9000/mediums/ryutaro_hashimoto.jpg',160,211,8375,'http://${hostName}:9000/mediums/ryutaro_hashimoto.jpg',0,39,0,0),(40,'sanetomi_sanjo.jpg','http://${hostName}:9000/mediums/sanetomi_sanjo.jpg',160,207,8942,'http://${hostName}:9000/mediums/sanetomi_sanjo.jpg',0,40,0,0),(41,'senjuro_hayashi.jpg','http://${hostName}:9000/mediums/senjuro_hayashi.jpg',160,213,9667,'http://${hostName}:9000/mediums/senjuro_hayashi.jpg',0,41,0,0),(42,'shigeru_yoshida.jpg','http://${hostName}:9000/mediums/shigeru_yoshida.jpg',160,213,11620,'http://${hostName}:9000/mediums/shigeru_yoshida.jpg',0,42,0,0),(43,'shinzo_abe.jpg','http://${hostName}:9000/mediums/shinzo_abe.jpg',160,213,11012,'http://${hostName}:9000/mediums/shinzo_abe.jpg',0,43,0,0),(44,'sosuke_uno.jpg','http://${hostName}:9000/mediums/sosuke_uno.jpg',142,201,13812,'http://${hostName}:9000/mediums/sosuke_uno.jpg',0,44,0,0),(45,'takaaki_kato.jpg','http://${hostName}:9000/mediums/takaaki_kato.jpg',160,212,6955,'http://${hostName}:9000/mediums/takaaki_kato.jpg',0,45,0,0),(46,'takashi_hara.jpg','http://${hostName}:9000/mediums/takashi_hara.jpg',160,212,8517,'http://${hostName}:9000/mediums/takashi_hara.jpg',0,46,0,0),(47,'takeo_fukuda.jpg','http://${hostName}:9000/mediums/takeo_fukuda.jpg',132,179,10872,'http://${hostName}:9000/mediums/takeo_fukuda.jpg',0,47,0,0),(48,'takeo_miki.jpg','http://${hostName}:9000/mediums/takeo_miki.jpg',137,185,40279,'http://${hostName}:9000/mediums/takeo_miki.jpg',0,48,0,0),(49,'tanzan_ishibashi.jpg','http://${hostName}:9000/mediums/tanzan_ishibashi.jpg',160,196,13115,'http://${hostName}:9000/mediums/tanzan_ishibashi.jpg',0,49,0,0),(50,'taro_aso.jpg','http://${hostName}:9000/mediums/taro_aso.jpg',160,213,10657,'http://${hostName}:9000/mediums/taro_aso.jpg',0,50,0,0),(51,'taro_katsura.jpg','http://${hostName}:9000/mediums/taro_katsura.jpg',160,213,11514,'http://${hostName}:9000/mediums/taro_katsura.jpg',0,51,0,0),(52,'terauchi_masatake.jpg','http://${hostName}:9000/mediums/terauchi_masatake.jpg',160,208,11127,'http://${hostName}:9000/mediums/terauchi_masatake.jpg',0,52,0,0),(53,'tetsu_katayama.jpg','http://${hostName}:9000/mediums/tetsu_katayama.jpg',160,196,8235,'http://${hostName}:9000/mediums/tetsu_katayama.jpg',0,53,0,0),(54,'tomiichi_murayama.jpg','http://${hostName}:9000/mediums/tomiichi_murayama.jpg',132,189,30215,'http://${hostName}:9000/mediums/tomiichi_murayama.jpg',0,54,0,0),(55,'tomosaburo_kato.jpg','http://${hostName}:9000/mediums/tomosaburo_kato.jpg',160,213,6824,'http://${hostName}:9000/mediums/tomosaburo_kato.jpg',0,55,0,0),(56,'toshiki_kaifu.jpg','http://${hostName}:9000/mediums/toshiki_kaifu.jpg',160,195,9269,'http://${hostName}:9000/mediums/toshiki_kaifu.jpg',0,56,0,0),(57,'tsutomu_hata.jpg','http://${hostName}:9000/mediums/tsutomu_hata.jpg',137,191,31719,'http://${hostName}:9000/mediums/tsutomu_hata.jpg',0,57,0,0),(58,'tsuyoshi_inukai.jpg','http://${hostName}:9000/mediums/tsuyoshi_inukai.jpg',160,203,5640,'http://${hostName}:9000/mediums/tsuyoshi_inukai.jpg',0,58,0,0),(59,'yasuhiro_nakasone.jpg','http://${hostName}:9000/mediums/yasuhiro_nakasone.jpg',160,217,11583,'http://${hostName}:9000/mediums/yasuhiro_nakasone.jpg',0,59,0,0),(60,'yasuo_fukuda.jpg','http://${hostName}:9000/mediums/yasuo_fukuda.jpg',129,187,31413,'http://${hostName}:9000/mediums/yasuo_fukuda.jpg',0,60,0,0),(61,'yoshihiko_noda.jpg','http://${hostName}:9000/mediums/yoshihiko_noda.jpg',160,213,8076,'http://${hostName}:9000/mediums/yoshihiko_noda.jpg',0,61,0,0),(62,'yoshiro_mori.jpg','http://${hostName}:9000/mediums/yoshiro_mori.jpg',160,203,15200,'http://${hostName}:9000/mediums/yoshiro_mori.jpg',0,62,0,0),(63,'yukio_hatoyama.jpg','http://${hostName}:9000/mediums/yukio_hatoyama.jpg',160,213,16380,'http://${hostName}:9000/mediums/yukio_hatoyama.jpg',0,63,0,0),(64,'zenko_suzuki.jpg','http://${hostName}:9000/mediums/zenko_suzuki.jpg',139,197,13934,'http://${hostName}:9000/mediums/zenko_suzuki.jpg',0,64,0,0),(65,'hideyosi_toyotomi.jpg','http://${hostName}:9000/mediums/hideyosi_toyotomi.jpg',360,509,33140,'http://${hostName}:9000/mediums/hideyosi_toyotomi.jpg',0,43,0,0),(66,'nobunaga_oda.jpg','http://${hostName}:9000/mediums/nobunaga_oda.jpg',342,400,91703,'http://${hostName}:9000/mediums/nobunaga_oda.jpg',0,1,0,0),(67,'ieyasu_tokugawa.jpg','http://${hostName}:9000/mediums/ieyasu_tokugawa.jpg',440,459,53830,'http://${hostName}:9000/mediums/ieyasu_tokugawa.jpg',0,50,0,0),(68,'hideyosi_toyotomi.jpg','http://${hostName}:9000/mediums/hideyosi_toyotomi.jpg',360,509,33140,'http://${hostName}:9000/mediums/hideyosi_toyotomi.jpg',0,1,0,0),(69,'yasuke.jpg','http://${hostName}:9000/mediums/yasuke.jpg',400,533,84335,'http://${hostName}:9000/mediums/yasuke.jpg',0,1,0,0);
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
INSERT INTO `notifications` VALUES (1,50,59,4,1,'cactacea://feeds/3/comments/1',1,1544280406755),(2,50,60,4,2,'cactacea://feeds/3/comments/2',1,1544280406765),(3,50,61,4,3,'cactacea://feeds/3/comments/3',1,1544280406798),(4,50,62,4,4,'cactacea://feeds/3/comments/4',1,1544280406828),(5,50,63,4,5,'cactacea://feeds/3/comments/5',1,1544280406850),(6,50,64,4,6,'cactacea://feeds/3/comments/6',1,1544280406874);
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
INSERT INTO `relationships` VALUES (1,43,NULL,1,0,0,1,0,0),(1,59,NULL,1,0,0,0,0,0),(1,60,NULL,1,0,0,0,0,0),(1,61,NULL,1,0,0,0,0,0),(1,62,NULL,1,0,0,0,0,0),(1,63,NULL,1,0,0,0,0,0),(1,64,NULL,1,0,0,0,0,0),(2,43,NULL,0,0,0,1,0,0),(3,43,NULL,0,0,0,1,0,0),(4,43,NULL,0,0,0,1,0,0),(5,43,NULL,0,0,0,1,0,0),(6,43,NULL,0,0,0,1,0,0),(7,43,NULL,0,0,0,1,0,0),(8,43,NULL,0,0,0,1,0,0),(9,43,NULL,0,0,0,1,0,0),(10,43,NULL,0,0,0,1,0,0),(11,43,NULL,0,0,0,1,0,0),(12,43,NULL,0,0,0,1,0,0),(13,43,NULL,0,0,0,1,0,0),(14,43,NULL,0,0,0,1,0,0),(15,43,NULL,0,0,0,1,0,0),(16,43,NULL,0,0,0,1,0,0),(17,43,NULL,0,0,0,1,0,0),(18,43,NULL,0,0,0,1,0,0),(19,43,NULL,0,0,0,1,0,0),(20,43,NULL,0,0,0,1,0,0),(21,43,NULL,0,0,0,1,0,0),(22,43,NULL,0,0,0,1,0,0),(23,43,NULL,0,0,0,1,0,0),(24,43,NULL,0,0,0,1,0,0),(25,43,NULL,0,0,0,1,0,0),(26,43,NULL,0,0,0,1,0,0),(27,43,NULL,0,0,0,1,0,0),(28,43,NULL,0,0,0,1,0,0),(29,43,NULL,0,0,0,1,0,0),(30,43,NULL,0,0,0,1,0,0),(31,43,NULL,0,0,0,1,0,0),(32,43,NULL,0,0,0,1,0,0),(33,43,NULL,0,0,0,1,0,0),(34,43,NULL,0,0,0,1,0,0),(35,43,NULL,0,0,0,1,0,0),(36,43,NULL,0,0,0,1,0,0),(37,43,NULL,0,0,0,1,0,0),(38,43,NULL,0,0,0,1,0,0),(39,43,NULL,0,0,0,1,0,0),(40,43,NULL,0,0,0,1,0,0),(41,43,NULL,0,0,0,1,0,0),(42,43,NULL,0,0,0,1,0,0),(43,1,NULL,1,0,0,1,0,0),(43,2,NULL,1,0,0,0,0,0),(43,3,NULL,1,0,0,0,0,0),(43,4,NULL,1,0,0,0,0,0),(43,5,NULL,1,0,0,0,0,0),(43,6,NULL,1,0,0,0,0,0),(43,7,NULL,1,0,0,0,0,0),(43,8,NULL,1,0,0,0,0,0),(43,9,NULL,1,0,0,0,0,0),(43,10,NULL,1,0,0,0,0,0),(43,11,NULL,1,0,0,0,0,0),(43,12,NULL,1,0,0,0,0,0),(43,13,NULL,1,0,0,0,0,0),(43,14,NULL,1,0,0,0,0,0),(43,15,NULL,1,0,0,0,0,0),(43,16,NULL,1,0,0,0,0,0),(43,17,NULL,1,0,0,0,0,0),(43,18,NULL,1,0,0,0,0,0),(43,19,NULL,1,0,0,0,0,0),(43,20,NULL,1,0,0,0,0,0),(43,21,NULL,1,0,0,0,0,0),(43,22,NULL,1,0,0,0,0,0),(43,23,NULL,1,0,0,0,0,0),(43,24,NULL,1,0,0,0,0,0),(43,25,NULL,1,0,0,0,0,0),(43,26,NULL,1,0,0,0,0,0),(43,27,NULL,1,0,0,0,0,0),(43,28,NULL,1,0,0,0,0,0),(43,29,NULL,1,0,0,0,0,0),(43,30,NULL,1,0,0,0,0,0),(43,31,NULL,1,0,0,0,0,0),(43,32,NULL,1,0,0,0,0,0),(43,33,NULL,1,0,0,0,0,0),(43,34,NULL,1,0,0,0,0,0),(43,35,NULL,1,0,0,0,0,0),(43,36,NULL,1,0,0,0,0,0),(43,37,NULL,1,0,0,0,0,0),(43,38,NULL,1,0,0,0,0,0),(43,39,NULL,1,0,0,0,0,0),(43,40,NULL,1,0,0,0,0,0),(43,41,NULL,1,0,0,0,0,0),(43,42,NULL,1,0,0,0,0,0),(43,44,NULL,1,0,0,0,0,0),(43,45,NULL,1,0,0,0,0,0),(43,46,NULL,1,0,0,0,0,0),(43,47,NULL,1,0,0,0,0,0),(43,48,NULL,1,0,0,0,0,0),(43,49,NULL,1,0,0,0,0,0),(43,50,NULL,1,0,0,1,0,0),(43,51,NULL,1,0,0,0,0,0),(43,52,NULL,1,0,0,0,0,0),(43,53,NULL,1,0,0,0,0,0),(43,54,NULL,1,0,0,0,0,0),(43,55,NULL,1,0,0,0,0,0),(43,56,NULL,1,0,0,0,0,0),(43,57,NULL,1,0,0,0,0,0),(43,58,NULL,1,0,0,0,0,0),(43,59,NULL,1,0,0,0,0,0),(43,60,NULL,1,0,0,0,0,0),(43,61,NULL,1,0,0,0,0,0),(43,62,NULL,1,0,0,0,0,0),(43,63,NULL,1,0,0,0,0,0),(43,64,NULL,1,0,0,0,0,0),(44,43,NULL,0,0,0,1,0,0),(45,43,NULL,0,0,0,1,0,0),(46,43,NULL,0,0,0,1,0,0),(47,43,NULL,0,0,0,1,0,0),(48,43,NULL,0,0,0,1,0,0),(49,43,NULL,0,0,0,1,0,0),(50,43,NULL,1,0,0,1,0,0),(51,43,NULL,0,0,0,1,0,0),(52,43,NULL,0,0,0,1,0,0),(53,43,NULL,0,0,0,1,0,0),(54,43,NULL,0,0,0,1,0,0),(55,43,NULL,0,0,0,1,0,0),(56,43,NULL,0,0,0,1,0,0),(57,43,NULL,0,0,0,1,0,0),(58,43,NULL,0,0,0,1,0,0),(59,1,NULL,0,0,0,1,0,0),(59,43,NULL,0,0,0,1,0,0),(60,1,NULL,0,0,0,1,0,0),(60,43,NULL,0,0,0,1,0,0),(61,1,NULL,0,0,0,1,0,0),(61,43,NULL,0,0,0,1,0,0),(62,1,NULL,0,0,0,1,0,0),(62,43,NULL,0,0,0,1,0,0),(63,1,NULL,0,0,0,1,0,0),(63,43,NULL,0,0,0,1,0,0),(64,1,NULL,0,0,0,1,0,0),(64,43,NULL,0,0,0,1,0,0);
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

-- Dump completed on 2018-12-08 23:51:01
