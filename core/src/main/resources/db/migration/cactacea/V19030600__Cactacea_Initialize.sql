-- MySQL Script generated by MySQL Workbench
-- Sun Sep  1 11:25:08 2019
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema ${schema}
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema ${schema}
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `${schema}` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `${schema}` ;

-- -----------------------------------------------------
-- Table `${schema}`.`authentications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`authentications` (
  `provider_id` VARCHAR(30) NOT NULL,
  `provider_key` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `hasher` VARCHAR(30) NOT NULL,
  `confirm` TINYINT(1) NOT NULL,
  PRIMARY KEY (`provider_id`, `provider_key`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`user_authentications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`user_authentications` (
  `user_id` BIGINT(20) NOT NULL,
  `provider_id` VARCHAR(30) NOT NULL,
  `provider_key` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`provider_id`, `provider_key`),
  INDEX `ui_user_authentications1_idx` (`user_id` ASC),
  CONSTRAINT `fk_user_authentications_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`mediums`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`mediums` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `key` VARCHAR(1024) NOT NULL,
  `uri` VARCHAR(2048) NOT NULL,
  `width` INT(11) NOT NULL,
  `height` INT(11) NOT NULL,
  `size` BIGINT(20) NOT NULL,
  `thumbnail_url` VARCHAR(2048) NULL DEFAULT NULL,
  `medium_type` TINYINT(4) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `content_warning` TINYINT(4) NOT NULL,
  `content_status` TINYINT(4) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`users` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(50) NOT NULL,
  `display_name` VARCHAR(50) NOT NULL DEFAULT ' ',
  `follow_count` BIGINT(20) NOT NULL DEFAULT '0',
  `profile_image` BIGINT(20) NULL DEFAULT NULL,
  `profile_image_url` VARCHAR(2083) NULL DEFAULT NULL,
  `follower_count` BIGINT(20) NOT NULL DEFAULT '0',
  `friend_count` BIGINT(20) NOT NULL DEFAULT '0',
  `feed_count` BIGINT(20) NOT NULL DEFAULT '0',
  `web` VARCHAR(2083) NULL DEFAULT NULL,
  `birthday` BIGINT(20) NULL DEFAULT NULL,
  `location` VARCHAR(255) NULL DEFAULT NULL,
  `bio` VARCHAR(1024) NULL DEFAULT NULL,
  `user_status` TINYINT(4) NOT NULL DEFAULT '0',
  `signed_out_at` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_users1_idx` (`user_name` ASC),
  INDEX `fk_users_mediums1_idx` (`profile_image` ASC),
  CONSTRAINT `fk_users_mediums1`
    FOREIGN KEY (`profile_image`)
    REFERENCES `${schema}`.`mediums` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`blocks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`blocks` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `blocked_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_blocks1_idx` (`user_id` ASC, `by` ASC),
  INDEX `fk_blocks_users1_idx` (`user_id` ASC),
  INDEX `fk_blocks_users2_idx` (`by` ASC),
  CONSTRAINT `fk_blocks_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_blocks_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`channels`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`channels` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1000) NULL DEFAULT NULL,
  `privacy_type` TINYINT(4) NOT NULL,
  `invitation_only` TINYINT(4) NOT NULL,
  `direct_message` TINYINT(4) NOT NULL,
  `authority_type` TINYINT(4) NOT NULL,
  `user_count` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `message_id` BIGINT(20) NULL DEFAULT NULL,
  `last_posted_at` BIGINT(20) NULL DEFAULT NULL,
  `organized_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_channels_messages1_idx` (`message_id` ASC),
  INDEX `fk_channels_users1_idx` (`by` ASC),
  CONSTRAINT `fk_channels_users1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`channel_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`channel_reports` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `channel_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` TINYINT(4) NOT NULL,
  `report_content` VARCHAR(1000) NULL DEFAULT NULL,
  `reported_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `fk_association_reports_users1_idx` (`by` ASC),
  INDEX `fk_channel_reports_channels1_idx` (`channel_id` ASC),
  CONSTRAINT `fk_channel_reports_channels1`
    FOREIGN KEY (`channel_id`)
    REFERENCES `${schema}`.`channels` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`clients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`clients` (
  `id` VARCHAR(255) NOT NULL,
  `secret` VARCHAR(80) NULL DEFAULT NULL,
  `redirect_uri` VARCHAR(2000) NOT NULL,
  `scope` VARCHAR(2000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`grant_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`grant_types` (
  `id` TINYINT(4) NOT NULL,
  `grant_type` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`client_grant_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`client_grant_types` (
  `grant_type_id` TINYINT(4) NOT NULL,
  `client_id` VARCHAR(80) NOT NULL,
  INDEX `fk_oauth_client_grant_type_oauth_grant_type_idx` (`grant_type_id` ASC),
  INDEX `fk_oauth_client_grant_type_oauth_client1_idx` (`client_id` ASC),
  CONSTRAINT `fk_oauth_client_grant_type_oauth_client1`
    FOREIGN KEY (`client_id`)
    REFERENCES `${schema}`.`clients` (`id`),
  CONSTRAINT `fk_oauth_client_grant_type_oauth_grant_type`
    FOREIGN KEY (`grant_type_id`)
    REFERENCES `${schema}`.`grant_types` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`feeds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feeds` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(1000) NOT NULL,
  `tags` VARCHAR(1000) NULL DEFAULT NULL,
  `medium_id1` BIGINT(20) NULL DEFAULT NULL,
  `medium_id2` BIGINT(20) NULL DEFAULT NULL,
  `medium_id3` BIGINT(20) NULL DEFAULT NULL,
  `medium_id4` BIGINT(20) NULL DEFAULT NULL,
  `medium_id5` BIGINT(20) NULL DEFAULT NULL,
  `by` BIGINT(20) NOT NULL,
  `like_count` BIGINT(20) NOT NULL,
  `comment_count` BIGINT(20) NOT NULL,
  `privacy_type` TINYINT(4) NOT NULL,
  `content_warning` TINYINT(4) NOT NULL,
  `content_status` TINYINT(4) NOT NULL,
  `expiration` BIGINT(20) NULL DEFAULT NULL,
  `notified` TINYINT(4) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_feeds_mediums2_idx` (`medium_id1` ASC),
  INDEX `fk_feeds_mediums3_idx` (`medium_id2` ASC),
  INDEX `fk_feeds_mediums4_idx` (`medium_id3` ASC),
  INDEX `fk_feeds_mediums5_idx` (`medium_id4` ASC),
  INDEX `fk_feeds_mediums6_idx` (`medium_id5` ASC),
  INDEX `idx_feeds1` (`id` ASC, `medium_id1` ASC, `medium_id2` ASC, `medium_id3` ASC, `medium_id4` ASC, `medium_id5` ASC, `by` ASC, `privacy_type` ASC, `content_warning` ASC, `content_status` ASC, `expiration` ASC),
  CONSTRAINT `fk_feeds_mediums2`
    FOREIGN KEY (`medium_id1`)
    REFERENCES `${schema}`.`mediums` (`id`),
  CONSTRAINT `fk_feeds_mediums3`
    FOREIGN KEY (`medium_id2`)
    REFERENCES `${schema}`.`mediums` (`id`),
  CONSTRAINT `fk_feeds_mediums4`
    FOREIGN KEY (`medium_id3`)
    REFERENCES `${schema}`.`mediums` (`id`),
  CONSTRAINT `fk_feeds_mediums5`
    FOREIGN KEY (`medium_id4`)
    REFERENCES `${schema}`.`mediums` (`id`),
  CONSTRAINT `fk_feeds_mediums6`
    FOREIGN KEY (`medium_id5`)
    REFERENCES `${schema}`.`mediums` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`comments` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(1000) NOT NULL,
  `feed_id` BIGINT(20) NOT NULL,
  `reply_id` BIGINT(20) NULL DEFAULT NULL,
  `like_count` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `content_warning` TINYINT(4) NOT NULL,
  `content_status` TINYINT(4) NOT NULL,
  `notified` TINYINT(4) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_comments_feeds1` (`feed_id` ASC),
  CONSTRAINT `fk_comments_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`comment_likes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`comment_likes` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `liked_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`comment_id` ASC, `by` ASC),
  INDEX `fk_comment_likes_users1_idx` (`by` ASC),
  INDEX `fx_comment_likes_idx` (`comment_id` ASC, `by` ASC, `liked_at` ASC),
  CONSTRAINT `fk_comment_likes_comments1`
    FOREIGN KEY (`comment_id`)
    REFERENCES `${schema}`.`comments` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_comment_likes_users1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`comment_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`comment_reports` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `comment_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` TINYINT(4) NOT NULL,
  `report_content` VARCHAR(1000) NULL DEFAULT NULL,
  `reported_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `fk_comment_reports_users1_idx` (`by` ASC),
  INDEX `fk_comment_reports_comments1` (`comment_id` ASC),
  CONSTRAINT `fk_comment_reports_comments1`
    FOREIGN KEY (`comment_id`)
    REFERENCES `${schema}`.`comments` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_comment_reports_users1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`devices`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`devices` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `udid` VARCHAR(255) NOT NULL,
  `device_type` TINYINT(4) NOT NULL,
  `active_status` TINYINT(4) NOT NULL,
  `push_token` VARCHAR(255) NULL DEFAULT NULL,
  `user_agent` VARCHAR(1000) NULL DEFAULT NULL,
  `registered_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `fk_devices_users1` (`user_id` ASC),
  CONSTRAINT `fk_devices_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_likes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_likes` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `feed_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `liked_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_feed_likes1_idx` (`feed_id` ASC, `by` ASC),
  INDEX `fk_feed_likes_feeds1_idx` (`feed_id` ASC),
  INDEX `fk_feed_likes_users1_idx` (`by` ASC),
  CONSTRAINT `fk_feed_likes_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_feed_likes_users1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_mediums`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_mediums` (
  `feed_id` BIGINT(20) NOT NULL,
  `medium_id` BIGINT(20) NOT NULL,
  `order_no` INT(11) NOT NULL,
  INDEX `fk_feed_mediums_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_mediums_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_reports` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `feed_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` TINYINT(4) NOT NULL,
  `report_content` VARCHAR(1000) NULL DEFAULT NULL,
  `reported_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `fk_feed_reports_users_idx` (`by` ASC),
  INDEX `fk_feed_reports_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_reports_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_feed_reports_users1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`feed_tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`feed_tags` (
  `feed_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(1024) NOT NULL,
  `order_no` INT(11) NOT NULL,
  INDEX `fk_feed_tags_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_tags_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`flyway_schema_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`flyway_schema_history` (
  `installed_rank` INT(11) NOT NULL,
  `version` VARCHAR(50) NULL DEFAULT NULL,
  `description` VARCHAR(200) NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `script` VARCHAR(1000) NOT NULL,
  `checksum` INT(11) NULL DEFAULT NULL,
  `installed_by` VARCHAR(100) NOT NULL,
  `installed_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` INT(11) NOT NULL,
  `success` TINYINT(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  INDEX `flyway_schema_history_s_idx` (`success` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`followers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`followers` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `followed_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_followers1_idx` (`user_id` ASC, `by` ASC),
  INDEX `fk_follower_users1_idx` (`user_id` ASC),
  INDEX `fk_follower_users2_idx` (`by` ASC),
  CONSTRAINT `fk_follower_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_follower_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`follows`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`follows` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `followed_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_follows1_idx` (`user_id` ASC, `by` ASC),
  INDEX `fk_follow_users1_idx` (`user_id` ASC),
  INDEX `fk_follow_users2_idx` (`by` ASC),
  CONSTRAINT `fk_follow_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_follow_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`friend_requests`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`friend_requests` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `notified` TINYINT(4) NOT NULL,
  `requested_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_friend_requests1_idx` (`user_id` ASC, `by` ASC),
  INDEX `fk_requests_users1_idx` (`user_id` ASC),
  INDEX `fk_requests_users2_idx` (`by` ASC),
  CONSTRAINT `fk_requests_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_requests_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`friends`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`friends` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `friended_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_friends1_idx` (`user_id` ASC, `by` ASC),
  INDEX `fk_friend_users1_idx` (`user_id` ASC),
  INDEX `fk_friend_users2_idx` (`by` ASC),
  CONSTRAINT `fk_friend_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_friend_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`invitations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`invitations` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `channel_id` BIGINT(20) NOT NULL,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `notified` TINYINT(4) NOT NULL,
  `invited_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_invitations1_idx` (`channel_id` ASC, `user_id` ASC),
  INDEX `fk_invitations_channels1_idx` (`channel_id` ASC),
  INDEX `fk_invitations_users1_idx` (`user_id` ASC),
  INDEX `fk_invitations_users2_idx` (`by` ASC),
  CONSTRAINT `fk_invitations_channels1`
    FOREIGN KEY (`channel_id`)
    REFERENCES `${schema}`.`channels` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_invitations_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_invitations_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`messages` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `by` BIGINT(20) NOT NULL,
  `channel_id` BIGINT(20) NOT NULL,
  `message_type` TINYINT(4) NOT NULL,
  `message` VARCHAR(1000) NULL DEFAULT NULL,
  `medium_id` BIGINT(20) NULL DEFAULT NULL,
  `stamp_id` BIGINT(20) NULL DEFAULT NULL,
  `user_count` BIGINT(20) NOT NULL DEFAULT '0',
  `read_count` BIGINT(20) NOT NULL DEFAULT '0',
  `content_warning` TINYINT(4) NOT NULL,
  `notified` TINYINT(4) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  `content_status` TINYINT(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_messages_users2_idx` (`by` ASC),
  INDEX `fk_messages_channels1_idx` (`channel_id` ASC),
  CONSTRAINT `fk_messages_channels1`
    FOREIGN KEY (`channel_id`)
    REFERENCES `${schema}`.`channels` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_messages_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`mutes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`mutes` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `muted_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_mutes1_idx` (`user_id` ASC, `by` ASC),
  INDEX `fk_mutes_users1_idx` (`user_id` ASC),
  INDEX `fk_mutes_users2_idx` (`by` ASC),
  CONSTRAINT `fk_mute_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_mutes_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`notifications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`notifications` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `notification_type` TINYINT(4) NOT NULL,
  `content_id` BIGINT(20) NULL DEFAULT NULL,
  `url` VARCHAR(2083) NOT NULL,
  `unread` TINYINT(4) NOT NULL,
  `notified_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_notifications1_idx` (`user_id` ASC, `by` ASC, `notification_type` ASC, `content_id` ASC),
  INDEX `fk_notifications_users1_idx` (`user_id` ASC),
  INDEX `fk_notifications_users2_idx` (`by` ASC),
  CONSTRAINT `fk_notifications_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_notifications_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`push_notification_settings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`push_notification_settings` (
  `user_id` BIGINT(20) NOT NULL,
  `invitation` TINYINT(4) NOT NULL,
  `feed` TINYINT(4) NOT NULL,
  `comment` TINYINT(4) NOT NULL,
  `channel_message` TINYINT(4) NOT NULL,
  `message` TINYINT(4) NOT NULL,
  `show_message` TINYINT(4) NOT NULL,
  `friend_request` TINYINT(4) NOT NULL,
  UNIQUE INDEX `ui_push_notification_settings1_idx` (`user_id` ASC),
  INDEX `fk_user_notification_settings_users1_idx` (`user_id` ASC),
  CONSTRAINT `fk_user_notification_settings_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`relationships`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`relationships` (
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `display_name` VARCHAR(50) NULL DEFAULT NULL,
  `follow` TINYINT(4) NOT NULL DEFAULT '0',
  `muting` TINYINT(4) NOT NULL DEFAULT '0',
  `is_friend` TINYINT(4) NOT NULL DEFAULT '0',
  `is_follower` TINYINT(4) NOT NULL DEFAULT '0',
  `friend_request_in_progress` TINYINT(4) NOT NULL DEFAULT '0',
  `follower_block_count` BIGINT(20) NOT NULL DEFAULT '0',
  `follow_block_count` BIGINT(20) NOT NULL DEFAULT '0',
  `friend_block_count` BIGINT(20) NOT NULL DEFAULT '0',
  `friended_at` BIGINT(20) NOT NULL DEFAULT '0',
  UNIQUE INDEX `ui_relationships1_idx` (`user_id` ASC, `by` ASC),
  INDEX `fk_user_relationships_users1_idx` (`user_id` ASC),
  INDEX `fk_user_relationships_users2_idx` (`by` ASC),
  INDEX `INDEX` (`user_id` ASC, `by` ASC, `follow` ASC, `is_follower` ASC, `muting` ASC, `is_friend` ASC, `friend_request_in_progress` ASC),
  CONSTRAINT `fk_user_status_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_user_status_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`user_channels`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`user_channels` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `channel_id` BIGINT(20) NOT NULL,
  `unread_count` BIGINT(20) NOT NULL,
  `hidden` TINYINT(4) NOT NULL,
  `mute` TINYINT(4) NOT NULL,
  `authority_type` TINYINT(4) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `joined_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `ui_user_channels1_idx` (`user_id` ASC, `channel_id` ASC),
  INDEX `fk_user_channels_channels1_idx` (`channel_id` ASC),
  INDEX `fk_user_channels_users1` (`user_id` ASC),
  INDEX `fk_user_channels_users2_idx` (`by` ASC),
  INDEX `i_user_channels1` (`user_id` ASC, `channel_id` ASC, `hidden` ASC, `mute` ASC, `by` ASC),
  CONSTRAINT `fk_user_channels_channels1`
    FOREIGN KEY (`channel_id`)
    REFERENCES `${schema}`.`channels` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_channels_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_user_channels_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`user_feeds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`user_feeds` (
  `user_id` BIGINT(20) NOT NULL,
  `feed_id` BIGINT(20) NOT NULL,
  `notified` TINYINT(4) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  UNIQUE INDEX `ui_user_feeds1_idx` (`user_id` ASC, `feed_id` ASC),
  INDEX `fk_user_feeds_users1_idx` (`user_id` ASC),
  INDEX `fk_user_feeds_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_user_feeds_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `${schema}`.`feeds` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_feeds_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`user_messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`user_messages` (
  `user_id` BIGINT(20) NOT NULL,
  `channel_id` BIGINT(20) NOT NULL,
  `message_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `unread` TINYINT(4) NOT NULL,
  `notified` TINYINT(4) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  UNIQUE INDEX `ui_user_messages1_idx` (`user_id` ASC, `channel_id` ASC, `message_id` ASC),
  INDEX `fk_message_reads_messages1_idx` (`message_id` ASC),
  INDEX `fk_user_message_users1_idx` (`user_id` ASC),
  INDEX `fk_user_message_channels1_idx` (`channel_id` ASC),
  INDEX `fk_user_messages_users1_idx` (`by` ASC),
  CONSTRAINT `fk_user_message_channels1`
    FOREIGN KEY (`channel_id`)
    REFERENCES `${schema}`.`channels` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_message_messages1`
    FOREIGN KEY (`message_id`)
    REFERENCES `${schema}`.`messages` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_user_message_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_user_messages_users1`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `${schema}`.`user_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `${schema}`.`user_reports` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` TINYINT(4) NOT NULL,
  `report_content` VARCHAR(1000) NULL DEFAULT NULL,
  `reported_at` BIGINT(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `fk_user_reports_users1_idx` (`user_id` ASC),
  INDEX `fk_user_reports_users2_idx` (`by` ASC),
  CONSTRAINT `fk_user_reports_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `${schema}`.`users` (`id`),
  CONSTRAINT `fk_user_reports_users2`
    FOREIGN KEY (`by`)
    REFERENCES `${schema}`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
