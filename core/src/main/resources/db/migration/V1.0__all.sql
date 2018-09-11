-- MySQL Script generated by MySQL Workbench
-- Sun Mar 25 00:17:03 2018
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

-- -----------------------------------------------------
-- Schema ${schema}
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `${schema}` DEFAULT CHARACTER SET utf8 ;
USE `${schema}` ;

-- -----------------------------------------------------
-- Table `${schema}`.`mediums`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`mediums` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `key` VARCHAR(1024) NOT NULL,
  `uri` VARCHAR(2048) NOT NULL,
  `width` INT NOT NULL,
  `height` INT NOT NULL,
  `size` BIGINT NOT NULL,
  `thumbnail_uri` VARCHAR(2048) NULL DEFAULT NULL,
  `medium_type` TINYINT NOT NULL,
  `by` BIGINT NOT NULL,
  `content_warning` TINYINT NOT NULL,
  `content_status` TINYINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`accounts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_name` VARCHAR(30) NOT NULL,
  `display_name` VARCHAR(50) NULL DEFAULT NULL,
  `password` VARCHAR(255) NOT NULL,
  `follow_count` BIGINT NOT NULL DEFAULT 0,
  `profile_image` BIGINT NULL DEFAULT NULL,
  `profile_image_url` VARCHAR(2083) NULL DEFAULT NULL,
  `follower_count` BIGINT NOT NULL DEFAULT 0,
  `friend_count` BIGINT NOT NULL DEFAULT 0,
  `web` VARCHAR(2083) NULL DEFAULT NULL,
  `birthday` BIGINT NULL DEFAULT NULL,
  `location` VARCHAR(255) NULL DEFAULT NULL,
  `bio` VARCHAR(1024) NULL DEFAULT NULL,
  `account_status` TINYINT NOT NULL DEFAULT 0,
  `signed_out_at` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_accounts_mediums1_idx` (`profile_image` ASC),
  INDEX `search_idx1` (`account_name` ASC, `display_name` ASC, `birthday` ASC, `account_status` ASC),
  CONSTRAINT `fk_accounts_mediums1`
    FOREIGN KEY (`profile_image`)
    REFERENCES `${schema}`.`mediums` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`feeds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feeds` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(1000) NOT NULL,
  `by` BIGINT NOT NULL,
  `like_count` BIGINT NOT NULL,
  `comment_count` BIGINT NOT NULL,
  `privacy_type` TINYINT NOT NULL,
  `content_warning` TINYINT NOT NULL,
  `content_status` TINYINT NOT NULL,
  `expiration` BIGINT NULL DEFAULT NULL,
  `notified` TINYINT NOT NULL,
  `posted_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`account_feeds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`account_feeds` (
  `account_id` BIGINT NOT NULL,
  `feed_id` BIGINT NOT NULL,
  `notified` TINYINT NOT NULL,
  `by` BIGINT NOT NULL,
  `posted_at` BIGINT NOT NULL,
  INDEX `fk_account_feeds_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_feeds_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_account_feeds_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_feeds_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`groups` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1000) NULL DEFAULT NULL,
  `privacy_type` TINYINT NOT NULL,
  `invitation_only` TINYINT NOT NULL,
  `direct_message` TINYINT NOT NULL,
  `authority_type` TINYINT NOT NULL,
  `account_count` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `message_id` BIGINT NULL DEFAULT NULL,
  `organized_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_groups_messages1_idx` (`message_id` ASC),
  INDEX `fk_groups_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_groups_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`account_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`account_groups` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_id` BIGINT NOT NULL,
  `group_id` BIGINT NOT NULL,
  `unread_count` BIGINT NOT NULL,
  `hidden` TINYINT NOT NULL,
  `mute` TINYINT NOT NULL,
  `to_account_id` BIGINT NOT NULL,
  `joined_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_account_groups_groups1_idx` (`group_id` ASC),
  INDEX `fk_account_groups_accounts1` (`account_id` ASC),
  INDEX `fk_account_groups_accounts2_idx` (`to_account_id` ASC),
  INDEX `search1_idx` (`hidden` ASC, `mute` ASC, `to_account_id` ASC),
  CONSTRAINT `fk_account_groups_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_groups_accounts2`
    FOREIGN KEY (`to_account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_groups_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `${schema}`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`messages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `by` BIGINT NOT NULL,
  `group_id` BIGINT NOT NULL,
  `message_type` TINYINT NOT NULL,
  `message` VARCHAR(1000) NULL DEFAULT NULL,
  `medium_id` BIGINT NULL DEFAULT NULL,
  `stamp_id` BIGINT NULL DEFAULT NULL,
  `account_count` BIGINT NOT NULL DEFAULT 0,
  `read_account_count` BIGINT NOT NULL DEFAULT 0,
  `content_warning` TINYINT NOT NULL,
  `notified` TINYINT NOT NULL,
  `posted_at` BIGINT NOT NULL,
  `content_status` TINYINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_messages_accounts2_idx` (`by` ASC),
  INDEX `fk_messages_groups1_idx` (`group_id` ASC),
  CONSTRAINT `fk_messages_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_messages_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `${schema}`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`account_messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`account_messages` (
  `account_id` BIGINT NOT NULL,
  `group_id` BIGINT NOT NULL,
  `message_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `unread` TINYINT NOT NULL,
  `notified` TINYINT NOT NULL,
  `posted_at` BIGINT NOT NULL,
  INDEX `fk_message_reads_messages1_idx` (`message_id` ASC),
  INDEX `fk_account_message_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_message_groups1_idx` (`group_id` ASC),
  INDEX `fk_account_messages_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_account_message_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_message_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `${schema}`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_message_messages1`
    FOREIGN KEY (`message_id`)
    REFERENCES `${schema}`.`messages` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_messages_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`account_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`account_reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `report_type` TINYINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`account_id` ASC, `report_type` ASC, `by` ASC),
  INDEX `fk_account_reports_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_reports_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_account_reports_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_reports_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`advertisement_settings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`advertisement_settings` (
  `account_id` BIGINT NOT NULL,
  `ad1` TINYINT NOT NULL,
  `ad2` TINYINT NOT NULL,
  `ad3` TINYINT NOT NULL,
  `ad4` TINYINT NOT NULL,
  `ad5` TINYINT NOT NULL,
  INDEX `fk_advertisement_settings_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_advertisement_settings_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`blocks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`blocks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `blocked` TINYINT NOT NULL DEFAULT 0,
  `being_blocked` TINYINT NOT NULL DEFAULT 0,
  `blocked_at` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `fk_blocks_accounts1_idx` (`account_id` ASC),
  INDEX `fk_blocks_accounts2_idx` (`by` ASC),
  INDEX `search_idx1` (`blocked` ASC, `being_blocked` ASC),
  CONSTRAINT `fk_blocks_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_blocks_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`clients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`clients` (
  `id` VARCHAR(80) NOT NULL,
  `secret` VARCHAR(80) NULL DEFAULT NULL,
  `redirect_uri` VARCHAR(2000) NOT NULL,
  `scope` VARCHAR(2000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`grant_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`grant_types` (
  `id` TINYINT NOT NULL,
  `grant_type` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`client_grant_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`client_grant_types` (
  `grant_type_id` TINYINT NOT NULL,
  `client_id` VARCHAR(80) NOT NULL,
  INDEX `fk_oauth_client_grant_type_oauth_grant_type_idx` (`grant_type_id` ASC),
  INDEX `fk_oauth_client_grant_type_oauth_client1_idx` (`client_id` ASC),
  CONSTRAINT `fk_oauth_client_grant_type_oauth_client1`
    FOREIGN KEY (`client_id`)
    REFERENCES `${schema}`.`clients` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_oauth_client_grant_type_oauth_grant_type`
    FOREIGN KEY (`grant_type_id`)
    REFERENCES `${schema}`.`grant_types` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`comments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(1000) NOT NULL,
  `feed_id` BIGINT NOT NULL,
  `reply_id` BIGINT NULL DEFAULT NULL,
  `like_count` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `content_warning` TINYINT NOT NULL,
  `content_status` TINYINT NOT NULL,
  `notified` TINYINT NOT NULL,
  `posted_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`comment_likes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`comment_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `posted_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`comment_id` ASC, `by` ASC),
  INDEX `fk_comment_likes_accounts1_idx` (`by` ASC),
  INDEX `fx_comment_likes_idx` (`comment_id` ASC, `by` ASC, `posted_at` ASC),
  CONSTRAINT `fk_comment_likes_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_likes_comments1`
    FOREIGN KEY (`comment_id`)
    REFERENCES `${schema}`.`comments` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`comment_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`comment_reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `report_type` TINYINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`comment_id` ASC, `by` ASC),
  INDEX `fk_comment_reports_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_comment_reports_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_reports_comments1`
    FOREIGN KEY (`comment_id`)
    REFERENCES `${schema}`.`comments` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`devices`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_id` BIGINT NOT NULL,
  `udid` VARCHAR(255) NOT NULL,
  `device_type` TINYINT NOT NULL,
  `active_status` TINYINT NOT NULL,
  `push_token` VARCHAR(255) NULL DEFAULT NULL,
  `user_agent` VARCHAR(1000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `fk_devices_accounts_idx` (`account_id` ASC, `udid` ASC),
  CONSTRAINT `fk_devices_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_likes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_likes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `feed_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `posted_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_feed_likes_feeds1_idx` (`feed_id` ASC),
  INDEX `fk_feed_likes_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_feed_likes_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_feed_likes_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_mediums`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_mediums` (
  `feed_id` BIGINT NOT NULL,
  `medium_id` BIGINT NOT NULL,
  `order_no` INT NOT NULL,
  INDEX `fk_feed_mediums_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_mediums_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `feed_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `report_type` TINYINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`by` ASC),
  INDEX `fk_feed_reports_accounts_idx` (`by` ASC),
  INDEX `fk_feed_reports_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_reports_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_feed_reports_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_tags` (
  `feed_id` BIGINT NOT NULL,
  `name` VARCHAR(1024) NOT NULL,
  `order_no` INT NOT NULL,
  INDEX `fk_feed_tags_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_tags_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`friend_requests`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`friend_requests` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `notified` TINYINT NOT NULL,
  `request_status` TINYINT NOT NULL,
  `requested_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_requests_accounts1_idx` (`account_id` ASC),
  INDEX `fk_requests_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_requests_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requests_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`group_invitations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`group_invitations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT NOT NULL,
  `account_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `notified` TINYINT NOT NULL,
  `invitation_status` TINYINT NOT NULL,
  `invited_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_invitations_groups1_idx` (`group_id` ASC),
  INDEX `fk_invitations_accounts1_idx` (`account_id` ASC),
  INDEX `fk_invitations_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_invitations_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invitations_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invitations_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `${schema}`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`group_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`group_reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `report_type` TINYINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`report_type` ASC, `by` ASC),
  INDEX `fk_association_reports_accounts1_idx` (`by` ASC),
  INDEX `fk_group_reports_groups1_idx` (`group_id` ASC),
  CONSTRAINT `fk_group_reports_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `${schema}`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`notifications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `notification_type` BIGINT NOT NULL,
  `content_id` BIGINT NULL DEFAULT NULL,
  `url` VARCHAR(2083) NOT NULL,
  `unread` TINYINT NOT NULL,
  `notified_at` BIGINT NOT NULL,
  PRIMARY KEY (`id`, `notification_type`),
  INDEX `fk_notifications_accounts1_idx` (`account_id` ASC),
  INDEX `fk_notifications_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_notifications_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_notifications_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`push_notification_settings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`push_notification_settings` (
  `account_id` BIGINT NOT NULL,
  `group_invitation` TINYINT NOT NULL,
  `follower_feed` TINYINT NOT NULL,
  `feed_comment` TINYINT NOT NULL,
  `group_message` TINYINT NOT NULL,
  `direct_message` TINYINT NOT NULL,
  `show_message` TINYINT NOT NULL,
  INDEX `fk_account_notification_settings_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_account_notification_settings_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`relationships`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`relationships` (
  `account_id` BIGINT NOT NULL,
  `by` BIGINT NOT NULL,
  `edited_display_name` VARCHAR(30) NULL DEFAULT NULL,
  `follow` TINYINT NOT NULL DEFAULT 0,
  `mute` TINYINT NOT NULL DEFAULT 0,
  `friend` TINYINT NOT NULL DEFAULT 0,
  `follower` TINYINT NOT NULL DEFAULT 0,
  `in_progress` TINYINT NOT NULL DEFAULT 0,
  `followed_at` BIGINT NOT NULL DEFAULT 0,
  `muted_at` BIGINT NOT NULL DEFAULT 0,
  `friended_at` BIGINT NOT NULL DEFAULT 0,
  UNIQUE INDEX `unique` (`account_id` ASC, `by` ASC),
  INDEX `fk_account_relationships_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_relationships_accounts2_idx` (`by` ASC),
  INDEX `index4` (`account_id` ASC, `by` ASC, `follow` ASC, `follower` ASC, `mute` ASC, `friend` ASC, `in_progress` ASC),
  INDEX `index5` (`account_id` ASC, `by` ASC, `follower` ASC, `follow` ASC, `friend` ASC),
  CONSTRAINT `fk_account_status_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_status_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`social_accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`social_accounts` (
  `provider_id` VARCHAR(255) NOT NULL,
  `account_id` BIGINT NOT NULL,
  `provider_key` VARCHAR(1024) NOT NULL,
  `authentication_code` VARCHAR(4) NULL,
  `verified` TINYINT NOT NULL,
  UNIQUE INDEX `UNIQUE` (`account_id` ASC, `provider_id` ASC),
  INDEX `fk_social_accounts_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_social_accounts_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `${schema}`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `${schema}`.`tickets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`tickets` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `stub` VARCHAR(1) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `stub` (`stub` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `${schema}` ;

DROP FUNCTION IF EXISTS `${schema}`.`generateId`;
-- -----------------------------------------------------
-- function generateId
-- -----------------------------------------------------

-- https://engineering.instagram.com/sharding-ids-at-instagram-1cf5a71e5a5c

DELIMITER $$
CREATE FUNCTION `generateId`(shard_id INT) RETURNS BIGINT
    DETERMINISTIC
BEGIN
    DECLARE our_epoc BIGINT;
    DECLARE now_millis BIGINT;
    DECLARE seq_id BIGINT;
    DECLARE result BIGINT;

	REPLACE INTO tickets (stub) VALUES ('a');
	SET seq_id := LAST_INSERT_ID() % 1024;
	SET our_epoc := 1387263000;  # 2011/01/01 00:00:00
    SET now_millis := (UNIX_TIMESTAMP() - our_epoc) * 1000;
    SET result := now_millis << 23;
    SET result := result | (shard_id << 10);
    SET result := result | seq_id;

    RETURN result;
END$$

DELIMITER ;

