package models

import "gorm.io/gorm"

type UserGroup struct {
	ID    int64  `json:"id"`
	Name  string `json:"name"`
	Posit uint   `json:"posit"`
}

type UserGroups struct {
	ID    int64   `gorm:"primary key;autoIncrement" json:"id"`
	Name  *string `json:"name"`
	Posit *uint   `json:"posit"`
}

func MigrateUserGroups(db *gorm.DB) error {
	err := db.AutoMigrate(&UserGroups{})
	return err
}
