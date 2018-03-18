USE `${schema}` ;

ALTER TABLE `cactacea`.`devices`
ADD COLUMN `active_status` TINYINT NOT NULL AFTER `device_type`;
