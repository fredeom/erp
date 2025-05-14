package models

import "gorm.io/gorm"

type Notification struct {
	ID   uint64 `json:"id"`
	Text string `json:"text"`
}

type Notifications struct {
	ID   uint64  `gorm:"primary key;autoIncrement" json:"id"`
	Text *string `json:"text"`
}

func MigrateNotifications(db *gorm.DB) error {
	err := db.AutoMigrate(&Notifications{})
	return err
}
