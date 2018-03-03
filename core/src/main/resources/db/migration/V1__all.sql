-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema cactacea
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cactacea
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cactacea` DEFAULT CHARACTER SET utf8 ;
USE `cactacea` ;

-- -----------------------------------------------------
-- Table `cactacea`.`mediums`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`mediums` (
  `id` BIGINT(20) NOT NULL,
  `key` VARCHAR(1024) NOT NULL,
  `uri` VARCHAR(2048) NOT NULL,
  `width` INT(10) NOT NULL,
  `height` INT(10) NOT NULL,
  `size` BIGINT(20) NOT NULL,
  `thumbnail_uri` VARCHAR(2048) NULL DEFAULT NULL,
  `medium_type` INT(1) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`accounts` (
  `id` BIGINT(20) NOT NULL,
  `account_name` VARCHAR(30) NOT NULL,
  `display_name` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `follow_count` BIGINT(20) NOT NULL DEFAULT '0',
  `profile_image` BIGINT(20) NULL DEFAULT NULL,
  `profile_image_url` VARCHAR(2083) NULL DEFAULT NULL,
  `follower_count` BIGINT(20) NOT NULL DEFAULT '0',
  `friend_count` BIGINT(20) NOT NULL DEFAULT '0',
  `web` VARCHAR(2083) NULL DEFAULT NULL,
  `birthday` DATE NULL,
  `location` VARCHAR(255) NULL,
  `bio` VARCHAR(1024) NULL,
  `position` BIGINT(20) NOT NULL DEFAULT '0',
  `account_status` INT(1) NOT NULL DEFAULT '0',
  `signed_out_at` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `SEARCH_INDEX` (`account_name` ASC),
  INDEX `fk_accounts_mediums1_idx` (`profile_image` ASC),
  INDEX `SEARCH_INDEX2` (`id` ASC, `account_name` ASC, `position` ASC, `account_status` ASC),
  CONSTRAINT `fk_accounts_mediums1`
    FOREIGN KEY (`profile_image`)
    REFERENCES `cactacea`.`mediums` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`clients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`clients` (
  `id` VARCHAR(80) NOT NULL,
  `secret` VARCHAR(80) NULL DEFAULT NULL,
  `redirect_uri` VARCHAR(2000) NULL DEFAULT NULL,
  `scope` VARCHAR(2000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`access_tokens`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`access_tokens` (
  `access_token` VARCHAR(60) NOT NULL,
  `refresh_token` VARCHAR(60) NULL DEFAULT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `client_id` VARCHAR(80) NOT NULL,
  `scope` VARCHAR(2000) NULL DEFAULT NULL,
  `expires_in` BIGINT(20) NULL DEFAULT NULL,
  `created_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`access_token`),
  INDEX `fk_access_tokens_accounts1_idx` (`account_id` ASC),
  INDEX `fk_access_tokens_clients1_idx` (`client_id` ASC),
  CONSTRAINT `fk_access_tokens_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_access_tokens_clients1`
    FOREIGN KEY (`client_id`)
    REFERENCES `cactacea`.`clients` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`feeds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`feeds` (
  `id` BIGINT(20) NOT NULL,
  `message` VARCHAR(1000) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `favorite_count` BIGINT(20) NOT NULL,
  `comment_count` BIGINT(20) NOT NULL,
  `privacy_type` INT(1) NOT NULL,
  `content_warning` TINYINT(1) NOT NULL,
  `notified` TINYINT(1) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`account_feeds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`account_feeds` (
  `account_id` BIGINT(20) NOT NULL,
  `feed_id` BIGINT(20) NOT NULL,
  `notified` TINYINT(1) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  INDEX `fk_account_feeds_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_feeds_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_account_feeds_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_feeds_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `cactacea`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`groups` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(1000) NULL DEFAULT NULL,
  `privacy_type` INT(1) NOT NULL,
  `invitation_only` TINYINT(1) NOT NULL,
  `direct_message` TINYINT(1) NOT NULL,
  `authority_type` INT(1) NOT NULL,
  `account_count` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `message_id` BIGINT(20) NULL DEFAULT NULL,
  `organized_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_groups_messages1_idx` (`message_id` ASC),
  INDEX `fk_groups_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_groups_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`account_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`account_groups` (
  `account_id` BIGINT(20) NOT NULL,
  `group_id` BIGINT(20) NOT NULL,
  `unread_count` BIGINT(20) NOT NULL,
  `hidden` TINYINT(1) NOT NULL,
  `muted` TINYINT(1) NOT NULL,
  `to_account_id` BIGINT(20) NOT NULL,
  `joined_at` BIGINT(20) NOT NULL,
  INDEX `fk_account_groups_groups1_idx` (`group_id` ASC),
  INDEX `fk_account_groups_accounts1` (`account_id` ASC),
  INDEX `fk_account_groups_accounts2_idx` (`to_account_id` ASC),
  CONSTRAINT `fk_account_groups_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_groups_accounts2`
    FOREIGN KEY (`to_account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_groups_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `cactacea`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`messages` (
  `id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `group_id` BIGINT(20) NOT NULL,
  `message_type` INT(1) NOT NULL,
  `message` VARCHAR(1000) NULL DEFAULT NULL,
  `medium_id` BIGINT(20) NULL DEFAULT NULL,
  `stamp_id` BIGINT(20) NULL DEFAULT NULL,
  `account_id` BIGINT(20) NULL DEFAULT NULL,
  `account_name` VARCHAR(1000) NULL DEFAULT NULL,
  `account_count` BIGINT(20) NOT NULL DEFAULT '0',
  `read_account_count` BIGINT(20) NOT NULL DEFAULT '0',
  `notified` TINYINT(1) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_messages_accounts2_idx` (`by` ASC),
  INDEX `fk_messages_groups1_idx` (`group_id` ASC),
  INDEX `fk_messages_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_messages_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_messages_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_messages_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `cactacea`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`account_messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`account_messages` (
  `account_id` BIGINT(20) NOT NULL,
  `group_id` BIGINT(20) NOT NULL,
  `message_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `unread` TINYINT(1) NOT NULL,
  `notified` TINYINT(1) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  INDEX `fk_message_reads_messages1_idx` (`message_id` ASC),
  INDEX `fk_account_message_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_message_groups1_idx` (`group_id` ASC),
  INDEX `fk_account_messages_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_account_message_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_message_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `cactacea`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_message_messages1`
    FOREIGN KEY (`message_id`)
    REFERENCES `cactacea`.`messages` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_messages_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`account_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`account_reports` (
  `id` BIGINT(20) NOT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` INT(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`account_id` ASC, `report_type` ASC, `by` ASC),
  INDEX `fk_account_reports_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_reports_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_account_reports_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_reports_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`advertisement_settings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`advertisement_settings` (
  `account_id` BIGINT(20) NOT NULL,
  `ad1` TINYINT(1) NOT NULL,
  `ad2` TINYINT(1) NOT NULL,
  `ad3` TINYINT(1) NOT NULL,
  `ad4` TINYINT(1) NOT NULL,
  `ad5` TINYINT(1) NOT NULL,
  INDEX `fk_advertisement_settings_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_advertisement_settings_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`auth_codes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`auth_codes` (
  `authorization_code` VARCHAR(40) NOT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `redirect_uri` VARCHAR(2000) NULL DEFAULT NULL,
  `created_at` BIGINT(20) NOT NULL,
  `scope` VARCHAR(2000) NULL DEFAULT NULL,
  `client_id` VARCHAR(80) NOT NULL,
  `expires_in` BIGINT(20) NULL DEFAULT NULL,
  PRIMARY KEY (`authorization_code`),
  INDEX `fk_auth_codes_accounts_idx` (`account_id` ASC),
  INDEX `fk_auth_codes_clients1_idx` (`client_id` ASC),
  CONSTRAINT `fk_auth_codes_accounts`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_auth_codes_clients1`
    FOREIGN KEY (`client_id`)
    REFERENCES `cactacea`.`clients` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`blocks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`blocks` (
  `account_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `blocked` TINYINT(1) NOT NULL DEFAULT '0',
  `being_blocked` TINYINT(1) NOT NULL DEFAULT '0',
  `blocked_at` BIGINT(20) NOT NULL DEFAULT '0',
  INDEX `fk_blocks_accounts1_idx` (`account_id` ASC),
  INDEX `fk_blocks_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_blocks_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_blocks_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`grant_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`grant_types` (
  `id` INT(11) NOT NULL,
  `grant_type` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`client_grant_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`client_grant_types` (
  `grant_type_id` INT(11) NOT NULL,
  `client_id` VARCHAR(80) NOT NULL,
  INDEX `fk_oauth_client_grant_type_oauth_grant_type_idx` (`grant_type_id` ASC),
  INDEX `fk_oauth_client_grant_type_oauth_client1_idx` (`client_id` ASC),
  CONSTRAINT `fk_oauth_client_grant_type_oauth_client1`
    FOREIGN KEY (`client_id`)
    REFERENCES `cactacea`.`clients` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_oauth_client_grant_type_oauth_grant_type`
    FOREIGN KEY (`grant_type_id`)
    REFERENCES `cactacea`.`grant_types` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`comments` (
  `id` BIGINT(20) NOT NULL,
  `message` VARCHAR(1000) NOT NULL,
  `feed_id` BIGINT(20) NOT NULL,
  `favorite_count` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `notified` TINYINT(1) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`comment_favorites`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`comment_favorites` (
  `comment_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  UNIQUE INDEX `UNIQUE` (`comment_id` ASC, `by` ASC),
  INDEX `fk_comment_favorites_accounts1_idx` (`by` ASC),
  INDEX `fx_comment_favorites_idx` (`comment_id` ASC, `by` ASC, `posted_at` ASC),
  CONSTRAINT `fk_comment_favorites_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_favorites_comments1`
    FOREIGN KEY (`comment_id`)
    REFERENCES `cactacea`.`comments` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`comment_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`comment_reports` (
  `id` BIGINT(20) NOT NULL,
  `comment_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` INT(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`comment_id` ASC, `by` ASC),
  INDEX `fk_comment_reports_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_comment_reports_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comment_reports_comments1`
    FOREIGN KEY (`comment_id`)
    REFERENCES `cactacea`.`comments` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`devices`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`devices` (
  `id` BIGINT(20) NOT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `udid` VARCHAR(255) NOT NULL,
  `push_token` VARCHAR(255) NULL DEFAULT NULL,
  `user_agent` VARCHAR(1000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `fk_devices_accounts_idx` (`account_id` ASC, `udid` ASC),
  CONSTRAINT `fk_devices_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`feed_favorites`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`feed_favorites` (
  `feed_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  INDEX `fk_feed_favorites_feeds1_idx` (`feed_id` ASC),
  INDEX `fk_feed_favorites_accounts1_idx` (`by` ASC),
  CONSTRAINT `fk_feed_favorites_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_feed_favorites_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `cactacea`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`feed_mediums`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`feed_mediums` (
  `feed_id` BIGINT(20) NOT NULL,
  `medium_id` BIGINT(20) NOT NULL,
  `register_at` BIGINT(20) NOT NULL,
  INDEX `fk_feed_mediums_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_mediums_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `cactacea`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`feed_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`feed_reports` (
  `id` BIGINT(20) NOT NULL,
  `feed_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` INT(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`by` ASC),
  INDEX `fk_feed_reports_accounts_idx` (`by` ASC),
  INDEX `fk_feed_reports_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_reports_accounts1`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_feed_reports_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `cactacea`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`feed_tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`feed_tags` (
  `feed_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(1024) NOT NULL,
  `register_at` BIGINT(20) NOT NULL,
  INDEX `fk_feed_tags_feeds1_idx` (`feed_id` ASC),
  CONSTRAINT `fk_feed_tags_feeds1`
    FOREIGN KEY (`feed_id`)
    REFERENCES `cactacea`.`feeds` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`friend_requests`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`friend_requests` (
  `id` BIGINT(20) NOT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `request_status` INT(1) NOT NULL,
  `requested_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_requests_accounts1_idx` (`account_id` ASC),
  INDEX `fk_requests_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_requests_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_requests_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`group_invitations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`group_invitations` (
  `id` BIGINT(20) NOT NULL,
  `group_id` BIGINT(20) NOT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `notified` TINYINT(1) NOT NULL,
  `invitation_status` INT(1) NOT NULL,
  `invited_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_invitations_groups1_idx` (`group_id` ASC),
  INDEX `fk_invitations_accounts1_idx` (`account_id` ASC),
  INDEX `fk_invitations_accounts2_idx` (`by` ASC),
  CONSTRAINT `fk_invitations_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invitations_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_invitations_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `cactacea`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`group_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`group_reports` (
  `id` BIGINT(20) NOT NULL,
  `group_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `report_type` INT(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`report_type` ASC, `by` ASC),
  INDEX `fk_association_reports_accounts1_idx` (`by` ASC),
  INDEX `fk_group_reports_groups1_idx` (`group_id` ASC),
  CONSTRAINT `fk_group_reports_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `cactacea`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`push_notification_settings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`push_notification_settings` (
  `account_id` BIGINT(20) NOT NULL,
  `group_invitation` TINYINT(1) NOT NULL,
  `follower_feed` TINYINT(1) NOT NULL,
  `feed_comment` TINYINT(1) NOT NULL,
  `group_message` TINYINT(1) NOT NULL,
  `direct_message` TINYINT(1) NOT NULL,
  `show_message` TINYINT(1) NOT NULL,
  INDEX `fk_account_notification_settings_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_account_notification_settings_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`relationships`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`relationships` (
  `account_id` BIGINT(20) NOT NULL,
  `by` BIGINT(20) NOT NULL,
  `edited_display_name` VARCHAR(30) NULL DEFAULT NULL,
  `followed` TINYINT(1) NOT NULL DEFAULT '0',
  `muted` TINYINT(1) NOT NULL DEFAULT '0',
  `friend` TINYINT(1) NOT NULL DEFAULT '0',
  `follower` TINYINT(1) NOT NULL DEFAULT '0',
  `in_progress` TINYINT(1) NOT NULL DEFAULT '0',
  `followed_at` BIGINT(20) NOT NULL DEFAULT '0',
  `muted_at` BIGINT(20) NOT NULL DEFAULT '0',
  `friended_at` BIGINT(20) NOT NULL DEFAULT '0',
  UNIQUE INDEX `unique` (`account_id` ASC, `by` ASC),
  INDEX `fk_account_relationships_accounts1_idx` (`account_id` ASC),
  INDEX `fk_account_relationships_accounts2_idx` (`by` ASC),
  INDEX `index4` (`account_id` ASC, `by` ASC, `followed` ASC, `follower` ASC, `muted` ASC, `friend` ASC, `in_progress` ASC),
  INDEX `index5` (`account_id` ASC, `by` ASC, `follower` ASC, `followed` ASC, `friend` ASC),
  CONSTRAINT `fk_account_status_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_account_status_accounts2`
    FOREIGN KEY (`by`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`social_accounts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`social_accounts` (
  `social_account_type` INT(1) NOT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `social_account_id` VARCHAR(1024) NOT NULL,
  UNIQUE INDEX `UNIQUE` (`account_id` ASC, `social_account_type` ASC),
  INDEX `fk_social_accounts_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_social_accounts_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`tickets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`tickets` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `stub` VARCHAR(1) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `stub` (`stub` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 13250
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `cactacea`.`timelines`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cactacea`.`timelines` (
  `id` BIGINT(20) NOT NULL,
  `account_id` BIGINT(20) NOT NULL,
  `feed_id` BIGINT(20) NULL DEFAULT NULL,
  `by` BIGINT(20) NULL DEFAULT NULL,
  `posted_at` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_timeline_feeds_accounts1_idx` (`account_id` ASC),
  CONSTRAINT `fk_timeline_feeds_accounts1`
    FOREIGN KEY (`account_id`)
    REFERENCES `cactacea`.`accounts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

USE `cactacea` ;

-- -----------------------------------------------------
-- function generateId
-- -----------------------------------------------------

DROP FUNCTION IF EXISTS `generateId`;

DELIMITER $$
CREATE FUNCTION `generateId`() RETURNS bigint(20)
    DETERMINISTIC
BEGIN
    DECLARE our_epoc BIGINT;
    DECLARE now_millis BIGINT;
    DECLARE seq_id BIGINT;
    DECLARE result BIGINT;
    DECLARE shard_id BIGINT;

	REPLACE INTO tickets (stub) VALUES ('a');
	SET seq_id := LAST_INSERT_ID() % 1024;
    SET shard_id := IFNULL(@shard_id, 1);
	SET our_epoc := 946684800;  # 2000/01/01 00:00:00
    SET now_millis := (UNIX_TIMESTAMP() - our_epoc) * 1000;
    SET result := now_millis << 23;
    SET result := result | (shard_id << 10);
    SET result := result | seq_id;

    RETURN result;
END$$

DELIMITER ;

