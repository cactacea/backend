-- MySQL Workbench Synchronization
-- Generated: 2018-11-28 20:06
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: TAKESHI SHIMADA

SET SQL_SAFE_UPDATES = 0;

ALTER SCHEMA `${schema}`  DEFAULT COLLATE utf8_general_ci ;

ALTER TABLE `${schema}`.`account_feeds`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`account_groups`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`account_messages`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`account_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`accounts`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`advertisement_settings`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`blocks`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`client_grant_types`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`clients`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`comment_likes`
COLLATE = utf8_general_ci ,
CHANGE COLUMN `posted_at` `liked_at` BIGINT(20) NOT NULL ;

ALTER TABLE `${schema}`.`comment_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`comments`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`devices`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`feed_likes`
COLLATE = utf8_general_ci ,
CHANGE COLUMN `posted_at` `liked_at` BIGINT(20) NOT NULL ;

ALTER TABLE `${schema}`.`feed_mediums`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`feed_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`feed_tags`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`feeds`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`followers`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`follows`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`friend_requests`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`friends`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`grant_types`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`group_invitations`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`group_reports`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`groups`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`mediums`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`messages`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`mutes`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`notifications`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`push_notification_settings`
COLLATE = utf8_general_ci ;

ALTER TABLE `${schema}`.`relationships`
COLLATE = utf8_general_ci ;

DROP TABLE IF EXISTS `${schema}`.`tickets` ;

ALTER TABLE `${schema}`.`account_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `${schema}`.`comment_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `${schema}`.`feed_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `${schema}`.`group_reports`
ADD COLUMN `reported_at` BIGINT(20) NOT NULL DEFAULT 0 AFTER `report_content`;

ALTER TABLE `${schema}`.`groups`
ADD COLUMN `last_posted_at` BIGINT(20) NULL DEFAULT NULL AFTER `message_id`;

ALTER TABLE `${schema}`.`relationships`
CHANGE COLUMN `edited_display_name` `display_name` VARCHAR(50) NULL DEFAULT NULL ;

UPDATE `${schema}`.`accounts` set display_name = account_name where display_name is null;

ALTER TABLE `${schema}`.`accounts`
CHANGE COLUMN `display_name` `display_name` VARCHAR(50) NOT NULL DEFAULT ' ' ;
