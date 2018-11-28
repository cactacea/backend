-- MySQL Workbench Synchronization
-- Generated: 2018-11-28 20:06
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: TAKESHI SHIMADA

SET SQL_SAFE_UPDATES = 0;

ALTER SCHEMA `cactacea`  DEFAULT COLLATE utf8_general_ci ;

ALTER TABLE `cactacea`.`account_feeds`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`account_groups`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`account_messages`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`account_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`accounts`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`advertisement_settings`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`blocks`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`client_grant_types`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`clients`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`comment_likes`
COLLATE = utf8_general_ci ,
CHANGE COLUMN `posted_at` `liked_at` BIGINT(20) NOT NULL ;

ALTER TABLE `cactacea`.`comment_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`comments`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`devices`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`feed_likes`
COLLATE = utf8_general_ci ,
CHANGE COLUMN `posted_at` `liked_at` BIGINT(20) NOT NULL ;

ALTER TABLE `cactacea`.`feed_mediums`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`feed_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`feed_tags`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`feeds`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`followers`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`follows`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`friend_requests`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`friends`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`grant_types`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`group_invitations`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`group_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`groups`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`mediums`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`messages`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`mutes`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`notifications`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`push_notification_settings`
COLLATE = utf8_general_ci ;

ALTER TABLE `cactacea`.`relationships`
COLLATE = utf8_general_ci ;

DROP TABLE IF EXISTS `cactacea`.`tickets` ;

ALTER TABLE `cactacea`.`account_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `cactacea`.`comment_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `cactacea`.`feed_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `cactacea`.`group_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `cactacea`.`groups`
ADD COLUMN `last_posted_at` BIGINT(20) NULL DEFAULT NULL AFTER `message_id`;

ALTER TABLE `cactacea`.`relationships`
CHANGE COLUMN `edited_display_name` `display_name` VARCHAR(50) NULL DEFAULT NULL ;

UPDATE `cactacea`.`accounts` set display_name = account_name where display_name is null;

ALTER TABLE `cactacea`.`accounts`
CHANGE COLUMN `display_name` `display_name` VARCHAR(50) NOT NULL DEFAULT ' ' ;
