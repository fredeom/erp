package models

import "gorm.io/gorm"

type NotificationStatus struct {
	ID             uint   `json:"id"`
	NotificationId uint64 `json:"notification_id"`
	UserId         uint64 `json:"user_id"`
}

type NotificationStatuses struct {
	ID             uint    `gorm:"primary key;autoIncrement" json:"id"`
	NotificationId *uint64 `json:"notification_id"`
	UserId         *uint64 `json:"user_id"`
}

func MigrateNotificationStatuses(db *gorm.DB) error {
	err := db.AutoMigrate(&NotificationStatuses{})
	return err
}
