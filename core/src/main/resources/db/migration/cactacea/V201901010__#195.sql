-- MySQL Workbench Synchronization
-- Generated: 2019-02-05 23:06
-- Model: New Model
-- Version: 1.0
-- Project: Name of the project
-- Author: TAKESHI SHIMADA

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

ALTER TABLE `${schema}`.`relationships`
ADD COLUMN `follower_block_count` BIGINT(20) NOT NULL DEFAULT 0 AFTER `friend_request_in_progress`,
ADD COLUMN `following_block_count` BIGINT(20) NOT NULL DEFAULT 0 AFTER `follower_block_count`,
ADD COLUMN `friend_block_count` BIGINT(20) NOT NULL DEFAULT 0 AFTER `following_block_count`;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;