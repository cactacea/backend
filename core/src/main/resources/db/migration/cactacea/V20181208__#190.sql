-- MySQL Workbench Synchronization
-- Generated: 2018-12-16 09:09
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: TAKESHI SHIMADA

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

ALTER TABLE `${schema}`.`follows` 
DROP FOREIGN KEY `fk_follow_accounts1`,
DROP FOREIGN KEY `fk_follow_accounts2`;

ALTER TABLE `${schema}`.`follows` 
ADD CONSTRAINT `fk_following_accounts1`
  FOREIGN KEY (`account_id`)
  REFERENCES `${schema}`.`accounts` (`id`),
ADD CONSTRAINT `fk_following_accounts2`
  FOREIGN KEY (`by`)
  REFERENCES `${schema}`.`accounts` (`id`);

ALTER TABLE `${schema}`.`follows` 
RENAME TO  `${schema}`.`followings` ;

ALTER TABLE `${schema}`.`relationships` 
DROP COLUMN `follower`,
DROP COLUMN `friend`,
DROP COLUMN `mute`,
DROP COLUMN `follow`,
ADD COLUMN `following` TINYINT(4) NOT NULL DEFAULT '0' AFTER `display_name`,
ADD COLUMN `muting` TINYINT(4) NOT NULL DEFAULT '0' AFTER `following`,
ADD COLUMN `is_friend` TINYINT(4) NOT NULL DEFAULT '0' AFTER `muting`,
ADD COLUMN `is_follower` TINYINT(4) NOT NULL DEFAULT '0' AFTER `is_friend`,
DROP INDEX `index4` ,
ADD INDEX `index4` (`account_id` ASC, `by` ASC, `following` ASC, `is_follower` ASC, `muting` ASC, `is_friend` ASC, `friend_request_in_progress` ASC),
DROP INDEX `index5` ,
ADD INDEX `index5` (`account_id` ASC, `by` ASC, `is_follower` ASC, `following` ASC, `is_friend` ASC);
;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
