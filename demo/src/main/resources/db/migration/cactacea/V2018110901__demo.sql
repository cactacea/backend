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
-- Dumping data for table`${schema}`.`account_feeds`
--

LOCK TABLES`${schema}`.`account_feeds` WRITE;
/*!40000 ALTER TABLE`${schema}`.`account_feeds` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`account_feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`account_groups`
--

LOCK TABLES`${schema}`.`account_groups` WRITE;
/*!40000 ALTER TABLE`${schema}`.`account_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`account_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`account_messages`
--

LOCK TABLES`${schema}`.`account_messages` WRITE;
/*!40000 ALTER TABLE`${schema}`.`account_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`account_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`account_reports`
--

LOCK TABLES`${schema}`.`account_reports` WRITE;
/*!40000 ALTER TABLE`${schema}`.`account_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`account_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`accounts`
--

LOCK TABLES`${schema}`.`accounts` WRITE;
/*!40000 ALTER TABLE`${schema}`.`accounts` DISABLE KEYS */;
INSERT INTO`${schema}`.`accounts` VALUES (1,'ito_hirobumi','ito_hirobumi','f737bd52007ca0dc9abb86b5567e8372',0,1,'http://10.0.1.3:9000/mediums/26d273ea-928f-4dc5-b700-4dace2f29d1c',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(2,'kuroda_kiyotaka','kuroda_kiyotaka','f737bd52007ca0dc9abb86b5567e8372',0,2,'http://10.0.1.3:9000/mediums/c87864bb-31e4-40de-b7fd-f2e22801ba4b',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(3,'sanjo_sanetomi','sanjo_sanetomi','f737bd52007ca0dc9abb86b5567e8372',0,3,'http://10.0.1.3:9000/mediums/8ed1d124-95a1-429c-9cdd-5fdf870fc2a2',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(4,'yamagata_aritomo','yamagata_aritomo','f737bd52007ca0dc9abb86b5567e8372',0,4,'http://10.0.1.3:9000/mediums/fc20ae30-2b36-42b3-ad88-868ee73cf8ae',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(5,'matsutaka_masayoshi','matsutaka_masayoshi','f737bd52007ca0dc9abb86b5567e8372',0,5,'http://10.0.1.3:9000/mediums/e77ba2bc-6c2a-49ca-807f-6571159bcf78',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(6,'okuma_shigenobu','okuma_shigenobu','f737bd52007ca0dc9abb86b5567e8372',0,6,'http://10.0.1.3:9000/mediums/f65d141e-f95a-40cd-b4df-ef6a2035d8ab',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(7,'saionji_kinmochi','saionji_kinmochi','f737bd52007ca0dc9abb86b5567e8372',0,7,'http://10.0.1.3:9000/mediums/65beecd6-d78b-4805-9026-f96cb064b500',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(8,'katsura_taro','katsura_taro','f737bd52007ca0dc9abb86b5567e8372',0,8,'http://10.0.1.3:9000/mediums/e9905655-ed5a-4842-986d-16dcf85b7ba1',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(9,'terauchi_masatake','terauchi_masatake','f737bd52007ca0dc9abb86b5567e8372',0,9,'http://10.0.1.3:9000/mediums/17d1c3eb-09d5-4dc3-8412-4bdcdcaadadd',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(10,'hara_takashi','hara_takashi','f737bd52007ca0dc9abb86b5567e8372',0,10,'http://10.0.1.3:9000/mediums/74116607-2a0f-4b3c-b0f4-75dea7331c7d',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(11,'uchida_kosai','uchida_kosai','f737bd52007ca0dc9abb86b5567e8372',0,11,'http://10.0.1.3:9000/mediums/9db4e781-981c-4d5b-a774-f752a0a54cfd',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(12,'takahashi_korekiyo','takahashi_korekiyo','f737bd52007ca0dc9abb86b5567e8372',0,12,'http://10.0.1.3:9000/mediums/b643b26f-0ba1-46b3-965e-6c22ca471be1',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(13,'kato_tomosaburo','kato_tomosaburo','f737bd52007ca0dc9abb86b5567e8372',0,13,'http://10.0.1.3:9000/mediums/8ca7db4e-e53e-4e03-b02f-a308159badf1',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(14,'yamamoto_gonbei','yamamoto_gonbei','f737bd52007ca0dc9abb86b5567e8372',0,14,'http://10.0.1.3:9000/mediums/9bd131cd-c576-4c78-ae2e-63e90597a89e',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(15,'kiyoura_keigo','kiyoura_keigo','f737bd52007ca0dc9abb86b5567e8372',0,15,'http://10.0.1.3:9000/mediums/a226019d-dc00-4e97-b83f-d460e368113d',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(16,'kato_takaaki','kato_takaaki','f737bd52007ca0dc9abb86b5567e8372',0,16,'http://10.0.1.3:9000/mediums/3d481e5c-a8cf-48b5-9b0b-dd131c55058a',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(17,'wakatsuki_reijiro','wakatsuki_reijiro','f737bd52007ca0dc9abb86b5567e8372',0,17,'http://10.0.1.3:9000/mediums/b545bf2d-f11d-4346-9898-33d7aac627ed',0,0,0,NULL,NULL,NULL,NULL,0,NULL),(18,'tanaka_giichi','tanaka_giichi','f737bd52007ca0dc9abb86b5567e8372',0,18,'http://10.0.1.3:9000/mediums/39fb44d3-5524-4cee-ba4d-50ca0e2a051a',0,0,0,NULL,NULL,NULL,NULL,0,NULL);
/*!40000 ALTER TABLE`${schema}`.`accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`advertisement_settings`
--

LOCK TABLES`${schema}`.`advertisement_settings` WRITE;
/*!40000 ALTER TABLE`${schema}`.`advertisement_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`advertisement_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`blocks`
--

LOCK TABLES`${schema}`.`blocks` WRITE;
/*!40000 ALTER TABLE`${schema}`.`blocks` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`blocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`client_grant_types`
--

LOCK TABLES`${schema}`.`client_grant_types` WRITE;
/*!40000 ALTER TABLE`${schema}`.`client_grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`client_grant_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`clients`
--

LOCK TABLES`${schema}`.`clients` WRITE;
/*!40000 ALTER TABLE`${schema}`.`clients` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`comment_likes`
--

LOCK TABLES`${schema}`.`comment_likes` WRITE;
/*!40000 ALTER TABLE`${schema}`.`comment_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`comment_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`comment_reports`
--

LOCK TABLES`${schema}`.`comment_reports` WRITE;
/*!40000 ALTER TABLE`${schema}`.`comment_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`comment_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`comments`
--

LOCK TABLES`${schema}`.`comments` WRITE;
/*!40000 ALTER TABLE`${schema}`.`comments` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`devices`
--

LOCK TABLES`${schema}`.`devices` WRITE;
/*!40000 ALTER TABLE`${schema}`.`devices` DISABLE KEYS */;
INSERT INTO`${schema}`.`devices` VALUES (1,1,'c78e66a9-26d7-4735-b0ce-10718ef50f5f',0,1,NULL,'http-warmup-client'),(2,2,'e227fc06-facd-4cb7-acbc-503a11c8d7cc',0,1,NULL,'http-warmup-client'),(3,3,'5c374039-7765-4cb5-9d45-38a63db80f16',0,1,NULL,'http-warmup-client'),(4,4,'9b96b297-67e8-4254-8c01-ff4c6a979789',0,1,NULL,'http-warmup-client'),(5,5,'5bd0ea4f-fb90-4a09-ac27-5cd483484da2',0,1,NULL,'http-warmup-client'),(6,6,'b1483e6a-412a-4189-8d6d-ca97b126ee1e',0,1,NULL,'http-warmup-client'),(7,7,'3c9bd68e-9715-43fa-ae2e-2cad29d92294',0,1,NULL,'http-warmup-client'),(8,8,'2d6fb145-cf08-4701-a27a-bb7dd03ea82b',0,1,NULL,'http-warmup-client'),(9,9,'197599e7-4a00-4c02-9813-e8d08f3ba17b',0,1,NULL,'http-warmup-client'),(10,10,'61767c7f-96fb-48dc-8ece-30cac0c2262c',0,1,NULL,'http-warmup-client'),(11,11,'85a40652-cb7b-4923-8f4c-83817f4152c6',0,1,NULL,'http-warmup-client'),(12,12,'f01ed774-b33d-4bd3-b93b-45338f06a1df',0,1,NULL,'http-warmup-client'),(13,13,'fd144bac-e874-4065-86fa-1343740fa374',0,1,NULL,'http-warmup-client'),(14,14,'7a2b3f7d-3a71-4f41-9f77-3fe7c4560f57',0,1,NULL,'http-warmup-client'),(15,15,'e9c15d66-f4c2-4ea2-9d39-dabbd5d4a17b',0,1,NULL,'http-warmup-client'),(16,16,'ae481dfe-27c1-4a1e-bf42-3e5ab1ecd462',0,1,NULL,'http-warmup-client'),(17,17,'c7bd95bb-d4a9-4022-8faa-b1ba69c444de',0,1,NULL,'http-warmup-client'),(18,18,'9caf332b-62f5-48cf-ad4c-305425cddb9e',0,1,NULL,'http-warmup-client');
/*!40000 ALTER TABLE`${schema}`.`devices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`feed_likes`
--

LOCK TABLES`${schema}`.`feed_likes` WRITE;
/*!40000 ALTER TABLE`${schema}`.`feed_likes` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`feed_likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`feed_mediums`
--

LOCK TABLES`${schema}`.`feed_mediums` WRITE;
/*!40000 ALTER TABLE`${schema}`.`feed_mediums` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`feed_mediums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`feed_reports`
--

LOCK TABLES`${schema}`.`feed_reports` WRITE;
/*!40000 ALTER TABLE`${schema}`.`feed_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`feed_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`feed_tags`
--

LOCK TABLES`${schema}`.`feed_tags` WRITE;
/*!40000 ALTER TABLE`${schema}`.`feed_tags` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`feed_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`feeds`
--

LOCK TABLES`${schema}`.`feeds` WRITE;
/*!40000 ALTER TABLE`${schema}`.`feeds` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`feeds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`followers`
--

LOCK TABLES`${schema}`.`followers` WRITE;
/*!40000 ALTER TABLE`${schema}`.`followers` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`follows`
--

LOCK TABLES`${schema}`.`follows` WRITE;
/*!40000 ALTER TABLE`${schema}`.`follows` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`friend_requests`
--

LOCK TABLES`${schema}`.`friend_requests` WRITE;
/*!40000 ALTER TABLE`${schema}`.`friend_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`friend_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`friends`
--

LOCK TABLES`${schema}`.`friends` WRITE;
/*!40000 ALTER TABLE`${schema}`.`friends` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`grant_types`
--

LOCK TABLES`${schema}`.`grant_types` WRITE;
/*!40000 ALTER TABLE`${schema}`.`grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`grant_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`group_invitations`
--

LOCK TABLES`${schema}`.`group_invitations` WRITE;
/*!40000 ALTER TABLE`${schema}`.`group_invitations` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`group_invitations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`group_reports`
--

LOCK TABLES`${schema}`.`group_reports` WRITE;
/*!40000 ALTER TABLE`${schema}`.`group_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`group_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`groups`
--

LOCK TABLES`${schema}`.`groups` WRITE;
/*!40000 ALTER TABLE`${schema}`.`groups` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`mediums`
--

LOCK TABLES`${schema}`.`mediums` WRITE;
/*!40000 ALTER TABLE`${schema}`.`mediums` DISABLE KEYS */;
INSERT INTO`${schema}`.`mediums` VALUES (1,'/tmp/','http://10.0.1.3:9000/mediums/26d273ea-928f-4dc5-b700-4dace2f29d1c',160,218,9141,NULL,0,1,0,0),(2,'/tmp/','http://10.0.1.3:9000/mediums/c87864bb-31e4-40de-b7fd-f2e22801ba4b',160,207,11504,NULL,0,2,0,0),(3,'/tmp/','http://10.0.1.3:9000/mediums/8ed1d124-95a1-429c-9cdd-5fdf870fc2a2',160,207,8942,NULL,0,3,0,0),(4,'/tmp/','http://10.0.1.3:9000/mediums/fc20ae30-2b36-42b3-ad88-868ee73cf8ae',160,213,13192,NULL,0,4,0,0),(5,'/tmp/','http://10.0.1.3:9000/mediums/e77ba2bc-6c2a-49ca-807f-6571159bcf78',160,213,11710,NULL,0,5,0,0),(6,'/tmp/','http://10.0.1.3:9000/mediums/f65d141e-f95a-40cd-b4df-ef6a2035d8ab',160,213,10802,NULL,0,6,0,0),(7,'/tmp/','http://10.0.1.3:9000/mediums/65beecd6-d78b-4805-9026-f96cb064b500',160,198,9571,NULL,0,7,0,0),(8,'/tmp/','http://10.0.1.3:9000/mediums/e9905655-ed5a-4842-986d-16dcf85b7ba1',160,213,11514,NULL,0,8,0,0),(9,'/tmp/','http://10.0.1.3:9000/mediums/17d1c3eb-09d5-4dc3-8412-4bdcdcaadadd',160,208,11127,NULL,0,9,0,0),(10,'/tmp/','http://10.0.1.3:9000/mediums/74116607-2a0f-4b3c-b0f4-75dea7331c7d',160,212,8517,NULL,0,10,0,0),(11,'/tmp/','http://10.0.1.3:9000/mediums/9db4e781-981c-4d5b-a774-f752a0a54cfd',160,213,9038,NULL,0,11,0,0),(12,'/tmp/','http://10.0.1.3:9000/mediums/b643b26f-0ba1-46b3-965e-6c22ca471be1',160,200,6941,NULL,0,12,0,0),(13,'/tmp/','http://10.0.1.3:9000/mediums/8ca7db4e-e53e-4e03-b02f-a308159badf1',160,213,6824,NULL,0,13,0,0),(14,'/tmp/','http://10.0.1.3:9000/mediums/9bd131cd-c576-4c78-ae2e-63e90597a89e',160,210,9566,NULL,0,14,0,0),(15,'/tmp/','http://10.0.1.3:9000/mediums/a226019d-dc00-4e97-b83f-d460e368113d',160,213,11265,NULL,0,15,0,0),(16,'/tmp/','http://10.0.1.3:9000/mediums/3d481e5c-a8cf-48b5-9b0b-dd131c55058a',160,212,6955,NULL,0,16,0,0),(17,'/tmp/','http://10.0.1.3:9000/mediums/b545bf2d-f11d-4346-9898-33d7aac627ed',160,198,6621,NULL,0,17,0,0),(18,'/tmp/','http://10.0.1.3:9000/mediums/39fb44d3-5524-4cee-ba4d-50ca0e2a051a',160,198,7181,NULL,0,18,0,0);
/*!40000 ALTER TABLE`${schema}`.`mediums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`messages`
--

LOCK TABLES`${schema}`.`messages` WRITE;
/*!40000 ALTER TABLE`${schema}`.`messages` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`mutes`
--

LOCK TABLES`${schema}`.`mutes` WRITE;
/*!40000 ALTER TABLE`${schema}`.`mutes` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`mutes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`notifications`
--

LOCK TABLES`${schema}`.`notifications` WRITE;
/*!40000 ALTER TABLE`${schema}`.`notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`push_notification_settings`
--

LOCK TABLES`${schema}`.`push_notification_settings` WRITE;
/*!40000 ALTER TABLE`${schema}`.`push_notification_settings` DISABLE KEYS */;
INSERT INTO`${schema}`.`push_notification_settings` VALUES (1,1,1,1,1,1,1),(2,1,1,1,1,1,1),(3,1,1,1,1,1,1),(4,1,1,1,1,1,1),(5,1,1,1,1,1,1),(6,1,1,1,1,1,1),(7,1,1,1,1,1,1),(8,1,1,1,1,1,1),(9,1,1,1,1,1,1),(10,1,1,1,1,1,1),(11,1,1,1,1,1,1),(12,1,1,1,1,1,1),(13,1,1,1,1,1,1),(14,1,1,1,1,1,1),(15,1,1,1,1,1,1),(16,1,1,1,1,1,1),(17,1,1,1,1,1,1),(18,1,1,1,1,1,1);
/*!40000 ALTER TABLE`${schema}`.`push_notification_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table`${schema}`.`relationships`
--

LOCK TABLES`${schema}`.`relationships` WRITE;
/*!40000 ALTER TABLE`${schema}`.`relationships` DISABLE KEYS */;
/*!40000 ALTER TABLE`${schema}`.`relationships` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

