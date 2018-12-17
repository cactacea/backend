-- MySQL Workbench Synchronization
-- Generated: 2018-12-16 20:03
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: TAKESHI SHIMADA

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
SET SQL_SAFE_UPDATES = 0;

ALTER TABLE `${schema}`.`followers`
ADD COLUMN `by_bak2` BIGINT(20) NULL DEFAULT NULL;

UPDATE `${schema}`.`followers`
SET by_bak2 = `by`;

UPDATE `${schema}`.`followers`
SET `by` = account_id;

UPDATE `${schema}`.`followers`
SET account_id = by_bak2;

ALTER TABLE `${schema}`.`followers`
DROP COLUMN `by_bak2`;

ALTER TABLE `${schema}`.`push_notification_settings`
ADD COLUMN `friend_request` TINYINT(4) NOT NULL AFTER `show_message`,
CHANGE COLUMN `follower_feed` `feed` TINYINT(4) NOT NULL ,
CHANGE COLUMN `feed_comment` `comment` TINYINT(4) NOT NULL ,
CHANGE COLUMN `direct_message` `message` TINYINT(4) NOT NULL ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
