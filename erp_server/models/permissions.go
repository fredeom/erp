package models

import "gorm.io/gorm"

type Permission struct {
	ID     uint   `json:"id"`
	Name   string `json:"name"`
	UserId uint   `json:"user_id"`
}

type Permissions struct {
	ID     uint    `gorm:"primary key;autoIncrement" json:"id"`
	Name   *string `json:"name"`
	UserId *uint   `json:"user_id"`
}

func MigratePermissions(db *gorm.DB) error {
	err := db.AutoMigrate(&Permissions{})
	return err
}
