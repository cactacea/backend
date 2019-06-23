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

-- LOCK TABLES `account_feeds` WRITE;
/*!40000 ALTER TABLE `account_feeds` DISABLE KEYS */;
INSERT INTO `account_feeds` VALUES (1,1,0,43,20181211120049),(50,1,0,43,20181211120049),(43,2,0,1,20181211120049),(43,3,0,50,20181211120049),(43,4,0,1,20181211120049);
/*!40000 ALTER TABLE `account_feeds` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `account_groups`
--

-- LOCK TABLES `account_groups` WRITE;
/*!40000 ALTER TABLE `account_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_groups` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `account_messages`
--

-- LOCK TABLES `account_messages` WRITE;
/*!40000 ALTER TABLE `account_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_messages` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `account_reports`
--

-- LOCK TABLES `account_reports` WRITE;
/*!40000 ALTER TABLE `account_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_reports` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `accounts`
--

-- LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'aritomo_yamagata','aritomo_yamagata','f737bd52007ca0dc9abb86b5567e8372',1,1,'http://${hostName}/mediums/aritomo_yamagata.jpg',1,0,3,NULL,NULL,NULL,NULL,0,NULL),(2,'eisaku_sato','eisaku_sato','f737bd52007ca0dc9abb86b5567e8372',1,2,'http://${hostName}/mediums/eisaku_sato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(3,'giichi_tanaka','giichi_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,3,'http://${hostName}/mediums/giichi_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(4,'gonbei_yamamoto','gonbei_yamamoto','f737bd52007ca0dc9abb86b5567e8372',1,4,'http://${hostName}/mediums/gonbei_yamamoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(5,'hayato_ikeda','hayato_ikeda','f737bd52007ca0dc9abb86b5567e8372',1,5,'http://${hostName}/mediums/hayato_ikeda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(6,'hideki_tojo','hideki_tojo','f737bd52007ca0dc9abb86b5567e8372',1,6,'http://${hostName}/mediums/hideki_tojo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(7,'hirobumi_ito','hirobumi_ito','f737bd52007ca0dc9abb86b5567e8372',1,7,'http://${hostName}/mediums/hirobumi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(8,'hitoshi_ashida','hitoshi_ashida','f737bd52007ca0dc9abb86b5567e8372',1,8,'http://${hostName}/mediums/hitoshi_ashida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(9,'ichiro_hatoyama','ichiro_hatoyama','f737bd52007ca0dc9abb86b5567e8372',1,9,'http://${hostName}/mediums/ichiro_hatoyama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(10,'junichiro_koizumi','junichiro_koizumi','f737bd52007ca0dc9abb86b5567e8372',1,10,'http://${hostName}/mediums/junichiro_koizumi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(11,'kakuei_tanaka','kakuei_tanaka','f737bd52007ca0dc9abb86b5567e8372',1,11,'http://${hostName}/mediums/kakuei_tanaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(12,'kantaro_suzuki','kantaro_suzuki','f737bd52007ca0dc9abb86b5567e8372',1,12,'http://${hostName}/mediums/kantaro_suzuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(13,'keigo_kiyoura','keigo_kiyoura','f737bd52007ca0dc9abb86b5567e8372',1,13,'http://${hostName}/mediums/keigo_kiyoura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(14,'keisuke_okada','keisuke_okada','f737bd52007ca0dc9abb86b5567e8372',1,14,'http://${hostName}/mediums/keisuke_okada.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(15,'keizo_obuchi','keizo_obuchi','f737bd52007ca0dc9abb86b5567e8372',1,15,'http://${hostName}/mediums/keizo_obuchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(16,'kiichi_miyazawa','kiichi_miyazawa','f737bd52007ca0dc9abb86b5567e8372',1,16,'http://${hostName}/mediums/kiichi_miyazawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(17,'kiichiro_hiranumra','kiichiro_hiranumra','f737bd52007ca0dc9abb86b5567e8372',1,17,'http://${hostName}/mediums/kiichiro_hiranumra.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(18,'kijuro_shidehara','kijuro_shidehara','f737bd52007ca0dc9abb86b5567e8372',1,18,'http://${hostName}/mediums/kijuro_shidehara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(19,'kinmochi_saionji','kinmochi_saionji','f737bd52007ca0dc9abb86b5567e8372',1,19,'http://${hostName}/mediums/kinmochi_saionji.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(20,'kiyotaka_kuroda','kiyotaka_kuroda','f737bd52007ca0dc9abb86b5567e8372',1,20,'http://${hostName}/mediums/kiyotaka_kuroda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(21,'kohki_hirota','kohki_hirota','f737bd52007ca0dc9abb86b5567e8372',1,21,'http://${hostName}/mediums/kohki_hirota.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(22,'korekiyo_takahashi','korekiyo_takahashi','f737bd52007ca0dc9abb86b5567e8372',1,22,'http://${hostName}/mediums/korekiyo_takahashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(23,'kosai_uchida','kosai_uchida','f737bd52007ca0dc9abb86b5567e8372',1,23,'http://${hostName}/mediums/kosai_uchida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(24,'kuniaki_koiso','kuniaki_koiso','f737bd52007ca0dc9abb86b5567e8372',1,24,'http://${hostName}/mediums/kuniaki_koiso.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(25,'makoto_saito','makoto_saito','f737bd52007ca0dc9abb86b5567e8372',1,25,'http://${hostName}/mediums/makoto_saito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(26,'masayoshi_ito','masayoshi_ito','f737bd52007ca0dc9abb86b5567e8372',1,26,'http://${hostName}/mediums/masayoshi_ito.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(27,'masayoshi_matsutaka','masayoshi_matsutaka','f737bd52007ca0dc9abb86b5567e8372',1,27,'http://${hostName}/mediums/masayoshi_matsutaka.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(28,'masayoshi_ohira','masayoshi_ohira','f737bd52007ca0dc9abb86b5567e8372',1,28,'http://${hostName}/mediums/masayoshi_ohira.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(29,'mitsumasa_yonai','mitsumasa_yonai','f737bd52007ca0dc9abb86b5567e8372',1,29,'http://${hostName}/mediums/mitsumasa_yonai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(30,'morihiro_hosokawa','morihiro_hosokawa','f737bd52007ca0dc9abb86b5567e8372',1,30,'http://${hostName}/mediums/morihiro_hosokawa.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(31,'naoto_kan','naoto_kan','f737bd52007ca0dc9abb86b5567e8372',1,31,'http://${hostName}/mediums/naoto_kan.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(32,'naruhiko_higashikuni','naruhiko_higashikuni','f737bd52007ca0dc9abb86b5567e8372',1,32,'http://${hostName}/mediums/naruhiko_higashikuni.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(33,'noboru_takeshita','noboru_takeshita','f737bd52007ca0dc9abb86b5567e8372',1,33,'http://${hostName}/mediums/noboru_takeshita.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(34,'nobusuke_kishi','nobusuke_kishi','f737bd52007ca0dc9abb86b5567e8372',1,34,'http://${hostName}/mediums/nobusuke_kishi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(35,'nobuyuki_abe','nobuyuki_abe','f737bd52007ca0dc9abb86b5567e8372',1,35,'http://${hostName}/mediums/nobuyuki_abe.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(36,'okuma_shigenobu','okuma_shigenobu','f737bd52007ca0dc9abb86b5567e8372',1,36,'http://${hostName}/mediums/okuma_shigenobu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(37,'osachi_hamaguchi','osachi_hamaguchi','f737bd52007ca0dc9abb86b5567e8372',1,37,'http://${hostName}/mediums/osachi_hamaguchi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(38,'reijiro_wakatsuki','reijiro_wakatsuki','f737bd52007ca0dc9abb86b5567e8372',1,38,'http://${hostName}/mediums/reijiro_wakatsuki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(39,'ryutaro_hashimoto','ryutaro_hashimoto','f737bd52007ca0dc9abb86b5567e8372',1,39,'http://${hostName}/mediums/ryutaro_hashimoto.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(40,'sanetomi_sanjo','sanetomi_sanjo','f737bd52007ca0dc9abb86b5567e8372',1,40,'http://${hostName}/mediums/sanetomi_sanjo.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(41,'senjuro_hayashi','senjuro_hayashi','f737bd52007ca0dc9abb86b5567e8372',1,41,'http://${hostName}/mediums/senjuro_hayashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(42,'shigeru_yoshida','shigeru_yoshida','f737bd52007ca0dc9abb86b5567e8372',1,42,'http://${hostName}/mediums/shigeru_yoshida.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(43,'shinzo_abe','shinzo_abe','f737bd52007ca0dc9abb86b5567e8372',2,43,'http://${hostName}/mediums/shinzo_abe.jpg',2,0,1,NULL,NULL,NULL,NULL,0,NULL),(44,'sosuke_uno','sosuke_uno','f737bd52007ca0dc9abb86b5567e8372',1,44,'http://${hostName}/mediums/sosuke_uno.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(45,'takaaki_kato','takaaki_kato','f737bd52007ca0dc9abb86b5567e8372',1,45,'http://${hostName}/mediums/takaaki_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(46,'takashi_hara','takashi_hara','f737bd52007ca0dc9abb86b5567e8372',1,46,'http://${hostName}/mediums/takashi_hara.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(47,'takeo_fukuda','takeo_fukuda','f737bd52007ca0dc9abb86b5567e8372',1,47,'http://${hostName}/mediums/takeo_fukuda.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(48,'takeo_miki','takeo_miki','f737bd52007ca0dc9abb86b5567e8372',1,48,'http://${hostName}/mediums/takeo_miki.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(49,'tanzan_ishibashi','tanzan_ishibashi','f737bd52007ca0dc9abb86b5567e8372',1,49,'http://${hostName}/mediums/tanzan_ishibashi.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(50,'taro_aso','taro_aso','f737bd52007ca0dc9abb86b5567e8372',1,50,'http://${hostName}/mediums/taro_aso.jpg',1,0,1,NULL,NULL,NULL,NULL,0,NULL),(51,'taro_katsura','taro_katsura','f737bd52007ca0dc9abb86b5567e8372',1,51,'http://${hostName}/mediums/taro_katsura.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(52,'terauchi_masatake','terauchi_masatake','f737bd52007ca0dc9abb86b5567e8372',1,52,'http://${hostName}/mediums/terauchi_masatake.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(53,'tetsu_katayama','tetsu_katayama','f737bd52007ca0dc9abb86b5567e8372',1,53,'http://${hostName}/mediums/tetsu_katayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(54,'tomiichi_murayama','tomiichi_murayama','f737bd52007ca0dc9abb86b5567e8372',1,54,'http://${hostName}/mediums/tomiichi_murayama.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(55,'tomosaburo_kato','tomosaburo_kato','f737bd52007ca0dc9abb86b5567e8372',1,55,'http://${hostName}/mediums/tomosaburo_kato.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(56,'toshiki_kaifu','toshiki_kaifu','f737bd52007ca0dc9abb86b5567e8372',1,56,'http://${hostName}/mediums/toshiki_kaifu.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(57,'tsutomu_hata','tsutomu_hata','f737bd52007ca0dc9abb86b5567e8372',1,57,'http://${hostName}/mediums/tsutomu_hata.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(58,'tsuyoshi_inukai','tsuyoshi_inukai','f737bd52007ca0dc9abb86b5567e8372',1,58,'http://${hostName}/mediums/tsuyoshi_inukai.jpg',1,0,0,NULL,NULL,NULL,NULL,0,NULL),(59,'yasuhiro_nakasone','yasuhiro_nakasone','f737bd52007ca0dc9abb86b5567e8372',2,59,'http://${hostName}/mediums/yasuhiro_nakasone.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(60,'yasuo_fukuda','yasuo_fukuda','f737bd52007ca0dc9abb86b5567e8372',2,60,'http://${hostName}/mediums/yasuo_fukuda.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(61,'yoshihiko_noda','yoshihiko_noda','f737bd52007ca0dc9abb86b5567e8372',2,61,'http://${hostName}/mediums/yoshihiko_noda.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(62,'yoshiro_mori','yoshiro_mori','f737bd52007ca0dc9abb86b5567e8372',2,62,'http://${hostName}/mediums/yoshiro_mori.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(63,'yukio_hatoyama','yukio_hatoyama','f737bd52007ca0dc9abb86b5567e8372',2,63,'http://${hostName}/mediums/yukio_hatoyama.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL),(64,'zenko_suzuki','zenko_suzuki','f737bd52007ca0dc9abb86b5567e8372',2,64,'http://${hostName}/mediums/zenko_suzuki.jpg',2,0,0,NULL,NULL,NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `advertisement_settings`
--

-- LOCK TABLES `advertisement_settings` WRITE;
/*!40000 ALTER TABLE `advertisement_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `advertisement_settings` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `blocks`
--

-- LOCK TABLES `blocks` WRITE;
/*!40000 ALTER TABLE `blocks` DISABLE KEYS */;
/*!40000 ALTER TABLE `blocks` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `client_grant_types`
--

-- LOCK TABLES `client_grant_types` WRITE;
/*!40000 ALTER TABLE `client_grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_grant_types` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `clients`
--

-- LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `comment_likes`
--

-- LOCK TABLES `comment_likes` WRITE;
/*!40000 ALTER TABLE `comment_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_likes` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `comment_reports`
--

-- LOCK TABLES `comment_reports` WRITE;
/*!40000 ALTER TABLE `comment_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_reports` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `comments`
--

-- LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,'When he was four Ieyasu was sent as a hostage to secure an alliance between his clan and the neighbouring Imagawa clan. He was raised at their court and given the education suitable for a nobleman.',3,NULL,0,59,0,0,1,1544529649375),(2,'In 1567 Ieyasu, whose father\'s death had left him as leader of the Matsudaira, allied with Oda Nobunaga, a powerful neighbour.',3,NULL,0,60,0,0,1,1544529649435),(3,'It was at this time that he changed his name from Matsudaira to Tokugawa, which was the name of the area from which his family originated.',3,NULL,0,61,0,0,1,1544529649475),(4,'He also changed his personal name to Ieyasu, so he was now known as Tokugawa Ieyasu.',3,NULL,0,62,0,0,1,1544529649502),(5,'When he was four Ieyasu was sent as a hostage to secure an alliance between his clan and the neighbouring Imagawa clan.',3,NULL,0,63,0,0,1,1544529649525),(6,'He was raised at their court and given the education suitable for a nobleman.',3,NULL,0,64,0,0,1,1544529649541);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `devices`
--

-- LOCK TABLES `devices` WRITE;
/*!40000 ALTER TABLE `devices` DISABLE KEYS */;
INSERT INTO `devices` VALUES (1,1,'5debe087-2772-4b69-83c8-f6a2f4a5ba4d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(2,2,'caf9d3be-4000-416d-b06c-2758714cc12b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(3,3,'3df240fb-f82c-47d5-b08a-6c2bd4ad47c3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(4,4,'bcf8b599-9375-4b26-af6c-53fcdc792a42',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(5,5,'eb522828-7bf5-4906-9422-b8b27ba0d547',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(6,6,'63648f71-7867-410c-ad13-de13bcf3ec23',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(7,7,'2348a9c1-37c8-4636-830d-4d856859cdbc',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(8,8,'9f9aa497-58f8-44eb-ba6d-596e5df3c757',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(9,9,'c32f7a10-eeee-43c5-99be-9a1be6db3ba5',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(10,10,'00374add-c627-4a9f-98d8-2582efdca200',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(11,11,'32a40f8e-4939-4432-a682-b20ba75fd72c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(12,12,'4d339a7a-b7ad-4e88-9cd1-2587cee1efee',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(13,13,'7bdd7e3d-11b4-4d58-be54-9c826bdf6117',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(14,14,'bb17f8da-48ac-4087-9c20-e353b85c06b0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(15,15,'5841187f-7c93-4e1c-af12-989682d35541',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(16,16,'be3ef674-415d-4490-a258-5189e833553b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(17,17,'172064f6-26c4-4de2-bb69-24bb36e625c1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(18,18,'3fde381a-870f-4c14-9b09-ec202d0c3ec2',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(19,19,'133415b6-7957-4420-9e46-718df4579a33',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(20,20,'3bda78b3-ec4b-4745-9337-f253491284d1',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(21,21,'1bbf7665-9556-4a59-8fac-35e8ddca2af9',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(22,22,'174738a0-0b7a-46a3-b051-b5ab69d20cdd',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(23,23,'9a42e1ec-c251-415d-8fe5-e825c17a1c0f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(24,24,'bee41e83-62e7-491d-b6dd-e0b538b4de68',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(25,25,'d7038855-acc6-4949-8a56-fa5c4469b7c3',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(26,26,'ce8c8c46-2fc1-4e71-be35-4e2def2bc961',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(27,27,'21e27539-d3c0-489a-b23c-afc70fac914e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(28,28,'028ca4c4-3858-45a5-8778-a3e1938d8ea5',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(29,29,'fdfa8a4a-9890-490e-87d0-9409f97d36d0',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(30,30,'90d78485-2b97-4928-a45e-a1a26f4aef91',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(31,31,'351787bf-3dce-46b4-895a-d6563bdda00c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(32,32,'26a866f7-33ae-4dd5-ac92-516bd249de32',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(33,33,'cab6ad6e-267e-4a53-a80f-a8c25ac10a8a',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(34,34,'7f914686-392d-417e-9cff-fb0788d13c47',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(35,35,'d9894d0a-36cf-4b84-9b1f-5aa018a9b721',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(36,36,'9442cbcc-d9cc-44f4-a678-157ae33ba197',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(37,37,'aa1d17cd-18db-4342-9d43-2651cd596a1f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(38,38,'2e386085-1250-44bf-8b4a-e5536e7b1ac7',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(39,39,'756131fe-b152-49ad-9bb8-a432d84ba908',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(40,40,'2e6a190e-3946-482a-a89c-ccb8a066b63e',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(41,41,'66397c61-14b7-45da-a69b-7d963298ec80',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(42,42,'0dad5ad9-a028-40ff-bc52-e0e9d286dedd',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(43,43,'04f7efe6-d6fb-4002-bf45-23a21b241cca',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(44,44,'5a11c387-9d41-4e9a-9f41-e1edaf660260',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(45,45,'4865b9f7-7606-4ee1-8884-773d0f8796df',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(46,46,'981255d4-3484-4e64-b3bd-c82503051693',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(47,47,'e32eed7a-691f-4b94-8a8f-f693c202bec6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(48,48,'898e7601-7c86-46b3-a7e7-b328e587cacb',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(49,49,'c23847ed-e384-494f-a92b-b4681d648548',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(50,50,'99bfda4a-7804-4075-9cef-7bd45d3d949d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(51,51,'3ce5e9e1-cf27-4c43-8a53-a2f3b6e63a87',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(52,52,'fa5e3e42-a8f7-4573-b10f-5cfa411e5c72',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(53,53,'27ee2933-539f-48ed-8688-0a43b8fb70c9',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(54,54,'22d86d01-030c-4e78-bfde-50b0d06dc09c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(55,55,'faf81ca7-ba1a-4892-8cbd-5fb152382335',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(56,56,'58cfcdd9-d0ad-41bb-9001-0acc263268a6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(57,57,'35703807-1528-48e1-b318-53bed53e1ded',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(58,58,'f99a4fe0-dff5-46e3-a711-e4898d3d6a2c',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(59,59,'0efb1d78-0037-48a1-8061-bb7247915243',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(60,60,'baf9cde8-bbf3-484e-9e3c-042dba5da53d',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(61,61,'577213ed-2dc6-4c88-a128-e57c31b5560f',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(62,62,'bb702928-9996-4ad4-865e-68fb13f93aa6',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(63,63,'efe40727-4f66-49fc-b651-0f382567e78b',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1'),(64,64,'cb79a44c-80e6-479c-b89c-08b222b489b7',0,1,NULL,'Mozilla/5.0 (iPhone; CPU iPhone OS 11_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1');
/*!40000 ALTER TABLE `devices` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `feed_likes`
--

-- LOCK TABLES `feed_likes` WRITE;
/*!40000 ALTER TABLE `feed_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE `feed_likes` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `feed_mediums`
--

-- LOCK TABLES `feed_mediums` WRITE;
/*!40000 ALTER TABLE `feed_mediums` DISABLE KEYS */;
INSERT INTO `feed_mediums` VALUES (1,65,0),(2,66,0),(3,67,0),(4,68,0),(5,69,0);
/*!40000 ALTER TABLE `feed_mediums` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `feed_reports`
--

-- LOCK TABLES `feed_reports` WRITE;
/*!40000 ALTER TABLE `feed_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `feed_reports` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `feed_tags`
--

-- LOCK TABLES `feed_tags` WRITE;
/*!40000 ALTER TABLE `feed_tags` DISABLE KEYS */;
INSERT INTO `feed_tags` VALUES (1,'daimyo',0),(1,'sengoku',1),(2,'daimyo',0),(2,'sengoku',1),(3,'daimyo',0),(3,'sengoku',1),(4,'daimyo',0),(4,'sengoku',1),(5,'samurai',0),(5,'sengoku',1);
/*!40000 ALTER TABLE `feed_tags` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `feeds`
--

-- LOCK TABLES `feeds` WRITE;
/*!40000 ALTER TABLE `feeds` DISABLE KEYS */;
INSERT INTO `feeds` VALUES (1,'Uesugi Kenshin (上杉 謙信, February 18, 1530 – April 19, 1578[1]) was a daimyō who was born as Nagao Kagetora,[2] and after the adoption into the Uesugi clan, ruled Echigo Province in the Sengoku period of Japan.[3] He was one of the most powerful daimyōs of the Sengoku period. While chiefly remembered for his prowess on the battlefield, Kenshin is also regarded as an extremely skillful administrator who fostered the growth of local industries and trade; his rule saw a marked rise in the standard of living of Echigo.',43,0,0,0,0,0,NULL,0,1544529649063),(2,'Oda Nobunaga (help·info), June 23, 1534 – June 21, 1582) was a powerful daimyō (feudal lord) of Japan in the late 16th century who attempted to unify Japan during the late Sengoku period, and successfully gained control over most of Honshu. Nobunaga is regarded as one of three unifiers of Japan along with his retainers Toyotomi Hideyoshi and Tokugawa Ieyasu. During his later life, Nobunaga was widely known for most brutal suppression of determined opponents, eliminating those who by principle refused to cooperate or yield to his demands. His reign was noted for innovative military tactics, fostering free trade, and encouraging the start of the Momoyama historical art period. He was killed when his retainer Akechi Mitsuhide rebelled against him at Honnō-ji.',1,0,0,0,0,0,NULL,0,1544529649177),(3,'Tokugawa Ieyasu (徳川家康, January 30, 1543 – June 1, 1616) was the founder and first shōgun of the Tokugawa shogunate of Japan, which effectively ruled Japan from the Battle of Sekigahara in 1600 until the Meiji Restoration in 1868. Ieyasu seized power in 1600, received appointment as shōgun in 1603, and abdicated from office in 1605, but remained in power until his death in 1616. His given name is sometimes spelled Iyeyasu,[1][2] according to the historical pronunciation of the kana character he. Ieyasu was posthumously enshrined at Nikkō Tōshō-gū with the name Tōshō Daigongen (東照大権現). He was one of the three unifiers of Japan, along with his former lord Nobunaga and Toyotomi Hideyoshi.',50,0,6,0,0,0,NULL,0,1544529649228),(4,'Toyotomi Hideyoshi (豊臣 秀吉, March 17, 1537 – September 18, 1598) was a preeminent daimyō, warrior, general, samurai, and politician of the Sengoku period[1] who is regarded as Japan\'s second \"great unifier\".[2] He succeeded his former liege lord, Oda Nobunaga, and brought an end to the Warring Lords period. The period of his rule is often called the Momoyama period, named after Hideyoshi\'s castle. After his death, his young son Hideyori was displaced by Tokugawa Ieyasu.',1,0,0,0,0,0,NULL,0,1544529649274),(5,'Yasuke (variously rendered as 弥助 or 弥介, 彌助 or 彌介 in different sources.[1]) (b. c. 1555–1590) was a Samurai of African origin who served under the Japanese hegemon and warlord Oda Nobunaga in 1581 and 1582.',1,0,0,2,0,0,NULL,0,1544529649327);
/*!40000 ALTER TABLE `feeds` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `followers`
--

-- LOCK TABLES `followers` WRITE;
/*!40000 ALTER TABLE `followers` DISABLE KEYS */;
INSERT INTO `followers` VALUES (1,1,43,1544529647249),(2,2,43,1544529647297),(3,3,43,1544529647325),(4,4,43,1544529647350),(5,5,43,1544529647373),(6,6,43,1544529647395),(7,7,43,1544529647417),(8,8,43,1544529647443),(9,9,43,1544529647471),(10,10,43,1544529647495),(11,11,43,1544529647519),(12,12,43,1544529647545),(13,13,43,1544529647570),(14,14,43,1544529647594),(15,15,43,1544529647616),(16,16,43,1544529647643),(17,17,43,1544529647670),(18,18,43,1544529647691),(19,19,43,1544529647715),(20,20,43,1544529647743),(21,21,43,1544529647769),(22,22,43,1544529647791),(23,23,43,1544529647817),(24,24,43,1544529647845),(25,25,43,1544529647886),(26,26,43,1544529647911),(27,27,43,1544529647936),(28,28,43,1544529647962),(29,29,43,1544529647996),(30,30,43,1544529648024),(31,31,43,1544529648048),(32,32,43,1544529648072),(33,33,43,1544529648095),(34,34,43,1544529648120),(35,35,43,1544529648146),(36,36,43,1544529648169),(37,37,43,1544529648194),(38,38,43,1544529648219),(39,39,43,1544529648243),(40,40,43,1544529648265),(41,41,43,1544529648286),(42,42,43,1544529648310),(43,44,43,1544529648333),(44,45,43,1544529648355),(45,46,43,1544529648375),(46,47,43,1544529648398),(47,48,43,1544529648421),(48,49,43,1544529648442),(49,50,43,1544529648465),(50,51,43,1544529648488),(51,52,43,1544529648514),(52,53,43,1544529648536),(53,54,43,1544529648557),(54,55,43,1544529648581),(55,56,43,1544529648609),(56,57,43,1544529648636),(57,58,43,1544529648659),(58,59,43,1544529648682),(59,60,43,1544529648704),(60,61,43,1544529648725),(61,62,43,1544529648749),(62,63,43,1544529648770),(63,64,43,1544529648791),(64,59,1,1544529648817),(65,60,1,1544529648839),(66,61,1,1544529648863),(67,62,1,1544529648884),(68,63,1,1544529648908),(69,64,1,1544529648930),(70,43,1,1544529648957),(71,43,50,1544529648978);
/*!40000 ALTER TABLE `followers` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `follows`
--

-- LOCK TABLES `follows` WRITE;
/*!40000 ALTER TABLE `follows` DISABLE KEYS */;
INSERT INTO `follows` VALUES (1,43,1,1544529647227),(2,43,2,1544529647289),(3,43,3,1544529647320),(4,43,4,1544529647345),(5,43,5,1544529647369),(6,43,6,1544529647391),(7,43,7,1544529647414),(8,43,8,1544529647438),(9,43,9,1544529647466),(10,43,10,1544529647491),(11,43,11,1544529647515),(12,43,12,1544529647541),(13,43,13,1544529647566),(14,43,14,1544529647590),(15,43,15,1544529647612),(16,43,16,1544529647637),(17,43,17,1544529647666),(18,43,18,1544529647687),(19,43,19,1544529647710),(20,43,20,1544529647737),(21,43,21,1544529647765),(22,43,22,1544529647787),(23,43,23,1544529647813),(24,43,24,1544529647840),(25,43,25,1544529647882),(26,43,26,1544529647907),(27,43,27,1544529647932),(28,43,28,1544529647957),(29,43,29,1544529647990),(30,43,30,1544529648019),(31,43,31,1544529648045),(32,43,32,1544529648068),(33,43,33,1544529648091),(34,43,34,1544529648116),(35,43,35,1544529648141),(36,43,36,1544529648165),(37,43,37,1544529648189),(38,43,38,1544529648215),(39,43,39,1544529648239),(40,43,40,1544529648261),(41,43,41,1544529648282),(42,43,42,1544529648306),(43,43,44,1544529648329),(44,43,45,1544529648352),(45,43,46,1544529648372),(46,43,47,1544529648394),(47,43,48,1544529648417),(48,43,49,1544529648439),(49,43,50,1544529648462),(50,43,51,1544529648484),(51,43,52,1544529648510),(52,43,53,1544529648532),(53,43,54,1544529648554),(54,43,55,1544529648576),(55,43,56,1544529648605),(56,43,57,1544529648632),(57,43,58,1544529648656),(58,43,59,1544529648678),(59,43,60,1544529648701),(60,43,61,1544529648722),(61,43,62,1544529648745),(62,43,63,1544529648766),(63,43,64,1544529648787),(64,1,59,1544529648813),(65,1,60,1544529648836),(66,1,61,1544529648859),(67,1,62,1544529648880),(68,1,63,1544529648904),(69,1,64,1544529648926),(70,1,43,1544529648952),(71,50,43,1544529648975);
/*!40000 ALTER TABLE `follows` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `friend_requests`
--

-- LOCK TABLES `friend_requests` WRITE;
/*!40000 ALTER TABLE `friend_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_requests` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `friends`
--

-- LOCK TABLES `friends` WRITE;
/*!40000 ALTER TABLE `friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `friends` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `grant_types`
--

-- LOCK TABLES `grant_types` WRITE;
/*!40000 ALTER TABLE `grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `grant_types` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `group_invitations`
--

-- LOCK TABLES `group_invitations` WRITE;
/*!40000 ALTER TABLE `group_invitations` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_invitations` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `group_reports`
--

-- LOCK TABLES `group_reports` WRITE;
/*!40000 ALTER TABLE `group_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_reports` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `groups`
--

-- LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `mediums`
--

-- LOCK TABLES `mediums` WRITE;
/*!40000 ALTER TABLE `mediums` DISABLE KEYS */;
INSERT INTO `mediums` VALUES (1,'aritomo_yamagata.jpg','http://${hostName}/mediums/aritomo_yamagata.jpg',160,213,13192,'http://${hostName}/mediums/aritomo_yamagata.jpg',0,1,0,0),(2,'eisaku_sato.jpg','http://${hostName}/mediums/eisaku_sato.jpg',160,227,10510,'http://${hostName}/mediums/eisaku_sato.jpg',0,2,0,0),(3,'giichi_tanaka.jpg','http://${hostName}/mediums/giichi_tanaka.jpg',160,198,7181,'http://${hostName}/mediums/giichi_tanaka.jpg',0,3,0,0),(4,'gonbei_yamamoto.jpg','http://${hostName}/mediums/gonbei_yamamoto.jpg',160,210,9566,'http://${hostName}/mediums/gonbei_yamamoto.jpg',0,4,0,0),(5,'hayato_ikeda.jpg','http://${hostName}/mediums/hayato_ikeda.jpg',160,213,5901,'http://${hostName}/mediums/hayato_ikeda.jpg',0,5,0,0),(6,'hideki_tojo.jpg','http://${hostName}/mediums/hideki_tojo.jpg',160,215,7435,'http://${hostName}/mediums/hideki_tojo.jpg',0,6,0,0),(7,'hirobumi_ito.jpg','http://${hostName}/mediums/hirobumi_ito.jpg',160,218,9141,'http://${hostName}/mediums/hirobumi_ito.jpg',0,7,0,0),(8,'hitoshi_ashida.jpg','http://${hostName}/mediums/hitoshi_ashida.jpg',160,194,12280,'http://${hostName}/mediums/hitoshi_ashida.jpg',0,8,0,0),(9,'ichiro_hatoyama.jpg','http://${hostName}/mediums/ichiro_hatoyama.jpg',160,195,9804,'http://${hostName}/mediums/ichiro_hatoyama.jpg',0,9,0,0),(10,'junichiro_koizumi.jpg','http://${hostName}/mediums/junichiro_koizumi.jpg',131,175,53624,'http://${hostName}/mediums/junichiro_koizumi.jpg',0,10,0,0),(11,'kakuei_tanaka.jpg','http://${hostName}/mediums/kakuei_tanaka.jpg',148,183,34578,'http://${hostName}/mediums/kakuei_tanaka.jpg',0,11,0,0),(12,'kantaro_suzuki.jpg','http://${hostName}/mediums/kantaro_suzuki.jpg',160,201,5381,'http://${hostName}/mediums/kantaro_suzuki.jpg',0,12,0,0),(13,'keigo_kiyoura.jpg','http://${hostName}/mediums/keigo_kiyoura.jpg',160,213,11265,'http://${hostName}/mediums/keigo_kiyoura.jpg',0,13,0,0),(14,'keisuke_okada.jpg','http://${hostName}/mediums/keisuke_okada.jpg',160,213,10460,'http://${hostName}/mediums/keisuke_okada.jpg',0,14,0,0),(15,'keizo_obuchi.jpg','http://${hostName}/mediums/keizo_obuchi.jpg',160,213,11264,'http://${hostName}/mediums/keizo_obuchi.jpg',0,15,0,0),(16,'kiichi_miyazawa.jpg','http://${hostName}/mediums/kiichi_miyazawa.jpg',160,213,15500,'http://${hostName}/mediums/kiichi_miyazawa.jpg',0,16,0,0),(17,'kiichiro_hiranumra.jpg','http://${hostName}/mediums/kiichiro_hiranumra.jpg',160,219,8780,'http://${hostName}/mediums/kiichiro_hiranumra.jpg',0,17,0,0),(18,'kijuro_shidehara.jpg','http://${hostName}/mediums/kijuro_shidehara.jpg',160,211,12099,'http://${hostName}/mediums/kijuro_shidehara.jpg',0,18,0,0),(19,'kinmochi_saionji.jpg','http://${hostName}/mediums/kinmochi_saionji.jpg',160,198,9571,'http://${hostName}/mediums/kinmochi_saionji.jpg',0,19,0,0),(20,'kiyotaka_kuroda.jpg','http://${hostName}/mediums/kiyotaka_kuroda.jpg',160,207,11504,'http://${hostName}/mediums/kiyotaka_kuroda.jpg',0,20,0,0),(21,'kohki_hirota.jpg','http://${hostName}/mediums/kohki_hirota.jpg',160,213,7291,'http://${hostName}/mediums/kohki_hirota.jpg',0,21,0,0),(22,'korekiyo_takahashi.jpg','http://${hostName}/mediums/korekiyo_takahashi.jpg',160,200,6941,'http://${hostName}/mediums/korekiyo_takahashi.jpg',0,22,0,0),(23,'kosai_uchida.jpg','http://${hostName}/mediums/kosai_uchida.jpg',160,213,9038,'http://${hostName}/mediums/kosai_uchida.jpg',0,23,0,0),(24,'kuniaki_koiso.jpg','http://${hostName}/mediums/kuniaki_koiso.jpg',160,213,8478,'http://${hostName}/mediums/kuniaki_koiso.jpg',0,24,0,0),(25,'makoto_saito.jpg','http://${hostName}/mediums/makoto_saito.jpg',160,213,7271,'http://${hostName}/mediums/makoto_saito.jpg',0,25,0,0),(26,'masayoshi_ito.jpg','http://${hostName}/mediums/masayoshi_ito.jpg',160,219,6479,'http://${hostName}/mediums/masayoshi_ito.jpg',0,26,0,0),(27,'masayoshi_matsutaka.jpg','http://${hostName}/mediums/masayoshi_matsutaka.jpg',160,213,11710,'http://${hostName}/mediums/masayoshi_matsutaka.jpg',0,27,0,0),(28,'masayoshi_ohira.jpg','http://${hostName}/mediums/masayoshi_ohira.jpg',160,215,11323,'http://${hostName}/mediums/masayoshi_ohira.jpg',0,28,0,0),(29,'mitsumasa_yonai.jpg','http://${hostName}/mediums/mitsumasa_yonai.jpg',160,204,5076,'http://${hostName}/mediums/mitsumasa_yonai.jpg',0,29,0,0),(30,'morihiro_hosokawa.jpg','http://${hostName}/mediums/morihiro_hosokawa.jpg',135,197,34210,'http://${hostName}/mediums/morihiro_hosokawa.jpg',0,30,0,0),(31,'naoto_kan.jpg','http://${hostName}/mediums/naoto_kan.jpg',160,214,10468,'http://${hostName}/mediums/naoto_kan.jpg',0,31,0,0),(32,'naruhiko_higashikuni.jpg','http://${hostName}/mediums/naruhiko_higashikuni.jpg',160,193,11366,'http://${hostName}/mediums/naruhiko_higashikuni.jpg',0,32,0,0),(33,'noboru_takeshita.jpg','http://${hostName}/mediums/noboru_takeshita.jpg',149,195,14112,'http://${hostName}/mediums/noboru_takeshita.jpg',0,33,0,0),(34,'nobusuke_kishi.jpg','http://${hostName}/mediums/nobusuke_kishi.jpg',126,185,15176,'http://${hostName}/mediums/nobusuke_kishi.jpg',0,34,0,0),(35,'nobuyuki_abe.jpg','http://${hostName}/mediums/nobuyuki_abe.jpg',160,231,9779,'http://${hostName}/mediums/nobuyuki_abe.jpg',0,35,0,0),(36,'okuma_shigenobu.jpg','http://${hostName}/mediums/okuma_shigenobu.jpg',160,213,10802,'http://${hostName}/mediums/okuma_shigenobu.jpg',0,36,0,0),(37,'osachi_hamaguchi.jpg','http://${hostName}/mediums/osachi_hamaguchi.jpg',160,200,7364,'http://${hostName}/mediums/osachi_hamaguchi.jpg',0,37,0,0),(38,'reijiro_wakatsuki.jpg','http://${hostName}/mediums/reijiro_wakatsuki.jpg',160,198,6621,'http://${hostName}/mediums/reijiro_wakatsuki.jpg',0,38,0,0),(39,'ryutaro_hashimoto.jpg','http://${hostName}/mediums/ryutaro_hashimoto.jpg',160,211,8375,'http://${hostName}/mediums/ryutaro_hashimoto.jpg',0,39,0,0),(40,'sanetomi_sanjo.jpg','http://${hostName}/mediums/sanetomi_sanjo.jpg',160,207,8942,'http://${hostName}/mediums/sanetomi_sanjo.jpg',0,40,0,0),(41,'senjuro_hayashi.jpg','http://${hostName}/mediums/senjuro_hayashi.jpg',160,213,9667,'http://${hostName}/mediums/senjuro_hayashi.jpg',0,41,0,0),(42,'shigeru_yoshida.jpg','http://${hostName}/mediums/shigeru_yoshida.jpg',160,213,11620,'http://${hostName}/mediums/shigeru_yoshida.jpg',0,42,0,0),(43,'shinzo_abe.jpg','http://${hostName}/mediums/shinzo_abe.jpg',160,213,11012,'http://${hostName}/mediums/shinzo_abe.jpg',0,43,0,0),(44,'sosuke_uno.jpg','http://${hostName}/mediums/sosuke_uno.jpg',142,201,13812,'http://${hostName}/mediums/sosuke_uno.jpg',0,44,0,0),(45,'takaaki_kato.jpg','http://${hostName}/mediums/takaaki_kato.jpg',160,212,6955,'http://${hostName}/mediums/takaaki_kato.jpg',0,45,0,0),(46,'takashi_hara.jpg','http://${hostName}/mediums/takashi_hara.jpg',160,212,8517,'http://${hostName}/mediums/takashi_hara.jpg',0,46,0,0),(47,'takeo_fukuda.jpg','http://${hostName}/mediums/takeo_fukuda.jpg',132,179,10872,'http://${hostName}/mediums/takeo_fukuda.jpg',0,47,0,0),(48,'takeo_miki.jpg','http://${hostName}/mediums/takeo_miki.jpg',137,185,40279,'http://${hostName}/mediums/takeo_miki.jpg',0,48,0,0),(49,'tanzan_ishibashi.jpg','http://${hostName}/mediums/tanzan_ishibashi.jpg',160,196,13115,'http://${hostName}/mediums/tanzan_ishibashi.jpg',0,49,0,0),(50,'taro_aso.jpg','http://${hostName}/mediums/taro_aso.jpg',160,213,10657,'http://${hostName}/mediums/taro_aso.jpg',0,50,0,0),(51,'taro_katsura.jpg','http://${hostName}/mediums/taro_katsura.jpg',160,213,11514,'http://${hostName}/mediums/taro_katsura.jpg',0,51,0,0),(52,'terauchi_masatake.jpg','http://${hostName}/mediums/terauchi_masatake.jpg',160,208,11127,'http://${hostName}/mediums/terauchi_masatake.jpg',0,52,0,0),(53,'tetsu_katayama.jpg','http://${hostName}/mediums/tetsu_katayama.jpg',160,196,8235,'http://${hostName}/mediums/tetsu_katayama.jpg',0,53,0,0),(54,'tomiichi_murayama.jpg','http://${hostName}/mediums/tomiichi_murayama.jpg',132,189,30215,'http://${hostName}/mediums/tomiichi_murayama.jpg',0,54,0,0),(55,'tomosaburo_kato.jpg','http://${hostName}/mediums/tomosaburo_kato.jpg',160,213,6824,'http://${hostName}/mediums/tomosaburo_kato.jpg',0,55,0,0),(56,'toshiki_kaifu.jpg','http://${hostName}/mediums/toshiki_kaifu.jpg',160,195,9269,'http://${hostName}/mediums/toshiki_kaifu.jpg',0,56,0,0),(57,'tsutomu_hata.jpg','http://${hostName}/mediums/tsutomu_hata.jpg',137,191,31719,'http://${hostName}/mediums/tsutomu_hata.jpg',0,57,0,0),(58,'tsuyoshi_inukai.jpg','http://${hostName}/mediums/tsuyoshi_inukai.jpg',160,203,5640,'http://${hostName}/mediums/tsuyoshi_inukai.jpg',0,58,0,0),(59,'yasuhiro_nakasone.jpg','http://${hostName}/mediums/yasuhiro_nakasone.jpg',160,217,11583,'http://${hostName}/mediums/yasuhiro_nakasone.jpg',0,59,0,0),(60,'yasuo_fukuda.jpg','http://${hostName}/mediums/yasuo_fukuda.jpg',129,187,31413,'http://${hostName}/mediums/yasuo_fukuda.jpg',0,60,0,0),(61,'yoshihiko_noda.jpg','http://${hostName}/mediums/yoshihiko_noda.jpg',160,213,8076,'http://${hostName}/mediums/yoshihiko_noda.jpg',0,61,0,0),(62,'yoshiro_mori.jpg','http://${hostName}/mediums/yoshiro_mori.jpg',160,203,15200,'http://${hostName}/mediums/yoshiro_mori.jpg',0,62,0,0),(63,'yukio_hatoyama.jpg','http://${hostName}/mediums/yukio_hatoyama.jpg',160,213,16380,'http://${hostName}/mediums/yukio_hatoyama.jpg',0,63,0,0),(64,'zenko_suzuki.jpg','http://${hostName}/mediums/zenko_suzuki.jpg',139,197,13934,'http://${hostName}/mediums/zenko_suzuki.jpg',0,64,0,0),(65,'hideyosi_toyotomi.jpg','http://${hostName}/mediums/hideyosi_toyotomi.jpg',360,509,33140,'http://${hostName}/mediums/hideyosi_toyotomi.jpg',0,43,0,0),(66,'nobunaga_oda.jpg','http://${hostName}/mediums/nobunaga_oda.jpg',342,400,91703,'http://${hostName}/mediums/nobunaga_oda.jpg',0,1,0,0),(67,'ieyasu_tokugawa.jpg','http://${hostName}/mediums/ieyasu_tokugawa.jpg',440,459,53830,'http://${hostName}/mediums/ieyasu_tokugawa.jpg',0,50,0,0),(68,'hideyosi_toyotomi.jpg','http://${hostName}/mediums/hideyosi_toyotomi.jpg',360,509,33140,'http://${hostName}/mediums/hideyosi_toyotomi.jpg',0,1,0,0),(69,'yasuke.jpg','http://${hostName}/mediums/yasuke.jpg',400,533,84335,'http://${hostName}/mediums/yasuke.jpg',0,1,0,0);
/*!40000 ALTER TABLE `mediums` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `messages`
--

-- LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `mutes`
--

-- LOCK TABLES `mutes` WRITE;
/*!40000 ALTER TABLE `mutes` DISABLE KEYS */;
/*!40000 ALTER TABLE `mutes` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `notifications`
--

-- LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,50,59,4,1,'cactacea://feeds/3/comments/1',1,1544529649444),(2,50,60,4,2,'cactacea://feeds/3/comments/2',1,1544529649463),(3,50,61,4,3,'cactacea://feeds/3/comments/3',1,1544529649491),(4,50,62,4,4,'cactacea://feeds/3/comments/4',1,1544529649520),(5,50,63,4,5,'cactacea://feeds/3/comments/5',1,1544529649537),(6,50,64,4,6,'cactacea://feeds/3/comments/6',1,1544529649555);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `push_notification_settings`
--

-- LOCK TABLES `push_notification_settings` WRITE;
/*!40000 ALTER TABLE `push_notification_settings` DISABLE KEYS */;
INSERT INTO `push_notification_settings` VALUES (1,1,1,1,1,1,1,1),(2,1,1,1,1,1,1,1),(3,1,1,1,1,1,1,1),(4,1,1,1,1,1,1,1),(5,1,1,1,1,1,1,1),(6,1,1,1,1,1,1,1),(7,1,1,1,1,1,1,1),(8,1,1,1,1,1,1,1),(9,1,1,1,1,1,1,1),(10,1,1,1,1,1,1,1),(11,1,1,1,1,1,1,1),(12,1,1,1,1,1,1,1),(13,1,1,1,1,1,1,1),(14,1,1,1,1,1,1,1),(15,1,1,1,1,1,1,1),(16,1,1,1,1,1,1,1),(17,1,1,1,1,1,1,1),(18,1,1,1,1,1,1,1),(19,1,1,1,1,1,1,1),(20,1,1,1,1,1,1,1),(21,1,1,1,1,1,1,1),(22,1,1,1,1,1,1,1),(23,1,1,1,1,1,1,1),(24,1,1,1,1,1,1,1),(25,1,1,1,1,1,1,1),(26,1,1,1,1,1,1,1),(27,1,1,1,1,1,1,1),(28,1,1,1,1,1,1,1),(29,1,1,1,1,1,1,1),(30,1,1,1,1,1,1,1),(31,1,1,1,1,1,1,1),(32,1,1,1,1,1,1,1),(33,1,1,1,1,1,1,1),(34,1,1,1,1,1,1,1),(35,1,1,1,1,1,1,1),(36,1,1,1,1,1,1,1),(37,1,1,1,1,1,1,1),(38,1,1,1,1,1,1,1),(39,1,1,1,1,1,1,1),(40,1,1,1,1,1,1,1),(41,1,1,1,1,1,1,1),(42,1,1,1,1,1,1,1),(43,1,1,1,1,1,1,1),(44,1,1,1,1,1,1,1),(45,1,1,1,1,1,1,1),(46,1,1,1,1,1,1,1),(47,1,1,1,1,1,1,1),(48,1,1,1,1,1,1,1),(49,1,1,1,1,1,1,1),(50,1,1,1,1,1,1,1),(51,1,1,1,1,1,1,1),(52,1,1,1,1,1,1,1),(53,1,1,1,1,1,1,1),(54,1,1,1,1,1,1,1),(55,1,1,1,1,1,1,1),(56,1,1,1,1,1,1,1),(57,1,1,1,1,1,1,1),(58,1,1,1,1,1,1,1),(59,1,1,1,1,1,1,1),(60,1,1,1,1,1,1,1),(61,1,1,1,1,1,1,1),(62,1,1,1,1,1,1,1),(63,1,1,1,1,1,1,1),(64,1,1,1,1,1,1,1);
/*!40000 ALTER TABLE `push_notification_settings` ENABLE KEYS */;
-- UNLOCK TABLES;

--
-- Dumping data for table `relationships`
--

-- LOCK TABLES `relationships` WRITE;
/*!40000 ALTER TABLE `relationships` DISABLE KEYS */;
INSERT INTO `relationships` VALUES (1,43,NULL,1,0,0,1,0,0,0,0,0),(1,59,NULL,1,0,0,0,0,0,0,0,0),(1,60,NULL,1,0,0,0,0,0,0,0,0),(1,61,NULL,1,0,0,0,0,0,0,0,0),(1,62,NULL,1,0,0,0,0,0,0,0,0),(1,63,NULL,1,0,0,0,0,0,0,0,0),(1,64,NULL,1,0,0,0,0,0,0,0,0),(2,43,NULL,0,0,0,1,0,0,0,0,0),(3,43,NULL,0,0,0,1,0,0,0,0,0),(4,43,NULL,0,0,0,1,0,0,0,0,0),(5,43,NULL,0,0,0,1,0,0,0,0,0),(6,43,NULL,0,0,0,1,0,0,0,0,0),(7,43,NULL,0,0,0,1,0,0,0,0,0),(8,43,NULL,0,0,0,1,0,0,0,0,0),(9,43,NULL,0,0,0,1,0,0,0,0,0),(10,43,NULL,0,0,0,1,0,0,0,0,0),(11,43,NULL,0,0,0,1,0,0,0,0,0),(12,43,NULL,0,0,0,1,0,0,0,0,0),(13,43,NULL,0,0,0,1,0,0,0,0,0),(14,43,NULL,0,0,0,1,0,0,0,0,0),(15,43,NULL,0,0,0,1,0,0,0,0,0),(16,43,NULL,0,0,0,1,0,0,0,0,0),(17,43,NULL,0,0,0,1,0,0,0,0,0),(18,43,NULL,0,0,0,1,0,0,0,0,0),(19,43,NULL,0,0,0,1,0,0,0,0,0),(20,43,NULL,0,0,0,1,0,0,0,0,0),(21,43,NULL,0,0,0,1,0,0,0,0,0),(22,43,NULL,0,0,0,1,0,0,0,0,0),(23,43,NULL,0,0,0,1,0,0,0,0,0),(24,43,NULL,0,0,0,1,0,0,0,0,0),(25,43,NULL,0,0,0,1,0,0,0,0,0),(26,43,NULL,0,0,0,1,0,0,0,0,0),(27,43,NULL,0,0,0,1,0,0,0,0,0),(28,43,NULL,0,0,0,1,0,0,0,0,0),(29,43,NULL,0,0,0,1,0,0,0,0,0),(30,43,NULL,0,0,0,1,0,0,0,0,0),(31,43,NULL,0,0,0,1,0,0,0,0,0),(32,43,NULL,0,0,0,1,0,0,0,0,0),(33,43,NULL,0,0,0,1,0,0,0,0,0),(34,43,NULL,0,0,0,1,0,0,0,0,0),(35,43,NULL,0,0,0,1,0,0,0,0,0),(36,43,NULL,0,0,0,1,0,0,0,0,0),(37,43,NULL,0,0,0,1,0,0,0,0,0),(38,43,NULL,0,0,0,1,0,0,0,0,0),(39,43,NULL,0,0,0,1,0,0,0,0,0),(40,43,NULL,0,0,0,1,0,0,0,0,0),(41,43,NULL,0,0,0,1,0,0,0,0,0),(42,43,NULL,0,0,0,1,0,0,0,0,0),(43,1,NULL,1,0,0,1,0,0,0,0,0),(43,2,NULL,1,0,0,0,0,0,0,0,0),(43,3,NULL,1,0,0,0,0,0,0,0,0),(43,4,NULL,1,0,0,0,0,0,0,0,0),(43,5,NULL,1,0,0,0,0,0,0,0,0),(43,6,NULL,1,0,0,0,0,0,0,0,0),(43,7,NULL,1,0,0,0,0,0,0,0,0),(43,8,NULL,1,0,0,0,0,0,0,0,0),(43,9,NULL,1,0,0,0,0,0,0,0,0),(43,10,NULL,1,0,0,0,0,0,0,0,0),(43,11,NULL,1,0,0,0,0,0,0,0,0),(43,12,NULL,1,0,0,0,0,0,0,0,0),(43,13,NULL,1,0,0,0,0,0,0,0,0),(43,14,NULL,1,0,0,0,0,0,0,0,0),(43,15,NULL,1,0,0,0,0,0,0,0,0),(43,16,NULL,1,0,0,0,0,0,0,0,0),(43,17,NULL,1,0,0,0,0,0,0,0,0),(43,18,NULL,1,0,0,0,0,0,0,0,0),(43,19,NULL,1,0,0,0,0,0,0,0,0),(43,20,NULL,1,0,0,0,0,0,0,0,0),(43,21,NULL,1,0,0,0,0,0,0,0,0),(43,22,NULL,1,0,0,0,0,0,0,0,0),(43,23,NULL,1,0,0,0,0,0,0,0,0),(43,24,NULL,1,0,0,0,0,0,0,0,0),(43,25,NULL,1,0,0,0,0,0,0,0,0),(43,26,NULL,1,0,0,0,0,0,0,0,0),(43,27,NULL,1,0,0,0,0,0,0,0,0),(43,28,NULL,1,0,0,0,0,0,0,0,0),(43,29,NULL,1,0,0,0,0,0,0,0,0),(43,30,NULL,1,0,0,0,0,0,0,0,0),(43,31,NULL,1,0,0,0,0,0,0,0,0),(43,32,NULL,1,0,0,0,0,0,0,0,0),(43,33,NULL,1,0,0,0,0,0,0,0,0),(43,34,NULL,1,0,0,0,0,0,0,0,0),(43,35,NULL,1,0,0,0,0,0,0,0,0),(43,36,NULL,1,0,0,0,0,0,0,0,0),(43,37,NULL,1,0,0,0,0,0,0,0,0),(43,38,NULL,1,0,0,0,0,0,0,0,0),(43,39,NULL,1,0,0,0,0,0,0,0,0),(43,40,NULL,1,0,0,0,0,0,0,0,0),(43,41,NULL,1,0,0,0,0,0,0,0,0),(43,42,NULL,1,0,0,0,0,0,0,0,0),(43,44,NULL,1,0,0,0,0,0,0,0,0),(43,45,NULL,1,0,0,0,0,0,0,0,0),(43,46,NULL,1,0,0,0,0,0,0,0,0),(43,47,NULL,1,0,0,0,0,0,0,0,0),(43,48,NULL,1,0,0,0,0,0,0,0,0),(43,49,NULL,1,0,0,0,0,0,0,0,0),(43,50,NULL,1,0,0,1,0,0,0,0,0),(43,51,NULL,1,0,0,0,0,0,0,0,0),(43,52,NULL,1,0,0,0,0,0,0,0,0),(43,53,NULL,1,0,0,0,0,0,0,0,0),(43,54,NULL,1,0,0,0,0,0,0,0,0),(43,55,NULL,1,0,0,0,0,0,0,0,0),(43,56,NULL,1,0,0,0,0,0,0,0,0),(43,57,NULL,1,0,0,0,0,0,0,0,0),(43,58,NULL,1,0,0,0,0,0,0,0,0),(43,59,NULL,1,0,0,0,0,0,0,0,0),(43,60,NULL,1,0,0,0,0,0,0,0,0),(43,61,NULL,1,0,0,0,0,0,0,0,0),(43,62,NULL,1,0,0,0,0,0,0,0,0),(43,63,NULL,1,0,0,0,0,0,0,0,0),(43,64,NULL,1,0,0,0,0,0,0,0,0),(44,43,NULL,0,0,0,1,0,0,0,0,0),(45,43,NULL,0,0,0,1,0,0,0,0,0),(46,43,NULL,0,0,0,1,0,0,0,0,0),(47,43,NULL,0,0,0,1,0,0,0,0,0),(48,43,NULL,0,0,0,1,0,0,0,0,0),(49,43,NULL,0,0,0,1,0,0,0,0,0),(50,43,NULL,1,0,0,1,0,0,0,0,0),(51,43,NULL,0,0,0,1,0,0,0,0,0),(52,43,NULL,0,0,0,1,0,0,0,0,0),(53,43,NULL,0,0,0,1,0,0,0,0,0),(54,43,NULL,0,0,0,1,0,0,0,0,0),(55,43,NULL,0,0,0,1,0,0,0,0,0),(56,43,NULL,0,0,0,1,0,0,0,0,0),(57,43,NULL,0,0,0,1,0,0,0,0,0),(58,43,NULL,0,0,0,1,0,0,0,0,0),(59,1,NULL,0,0,0,1,0,0,0,0,0),(59,43,NULL,0,0,0,1,0,0,0,0,0),(60,1,NULL,0,0,0,1,0,0,0,0,0),(60,43,NULL,0,0,0,1,0,0,0,0,0),(61,1,NULL,0,0,0,1,0,0,0,0,0),(61,43,NULL,0,0,0,1,0,0,0,0,0),(62,1,NULL,0,0,0,1,0,0,0,0,0),(62,43,NULL,0,0,0,1,0,0,0,0,0),(63,1,NULL,0,0,0,1,0,0,0,0,0),(63,43,NULL,0,0,0,1,0,0,0,0,0),(64,1,NULL,0,0,0,1,0,0,0,0,0),(64,43,NULL,0,0,0,1,0,0,0,0,0);
/*!40000 ALTER TABLE `relationships` ENABLE KEYS */;
-- UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-12-11 21:06:11
