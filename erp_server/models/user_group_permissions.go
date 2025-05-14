package models

import "gorm.io/gorm"

type UserGroupPermission struct {
	ID          uint   `json:"id"`
	Name        string `json:"name"`
	UserGroupId int64  `json:"user_group_id"`
}

type UserGroupPermissions struct {
	ID          uint    `gorm:"primary key;autoIncrement" json:"id"`
	Name        *string `json:"name"`
	UserGroupId *int64  `json:"user_group_id"`
}

func MigrateUserGroupPermissions(db *gorm.DB) error {
	err := db.AutoMigrate(&UserGroupPermissions{})
	return err
}
