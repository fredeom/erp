package models

import "gorm.io/gorm"

type UserShort struct {
	ID          uint   `json:"id"`
	Name        string `json:"name"`
	UserGroupId uint   `json:"user_group_id"`
	UserGroup   UserGroup
	LastLoginAt string `json:"last_login_at"`
}

type User struct {
	ID          uint   `json:"id"`
	Login       string `json:"login"`
	Pass        string `json:"pass"`
	Name        string `json:"name"`
	Phone       string `json:"phone"`
	UserGroupId uint   `json:"user_group_id"`
	UserGroup   UserGroup
	CreatedAt   string `json:"created_at"`
	UpdatedAt   string `json:"updated_at"`
	LastLoginAt string `json:"last_login_at"`
}

type Users struct {
	ID          uint    `gorm:"primary key;autoIncrement" json:"id"`
	Login       *string `json:"login"`
	Pass        *string `json:"pass"`
	Name        *string `json:"name"`
	Phone       *string `json:"phone"`
	UserGroupId *uint   `json:"user_group_id"`
	CreatedAt   *string `json:"created_at"`
	UpdatedAt   *string `json:"updated_at"`
	LastLoginAt *string `json:"last_login_at"`
}

func MigrateUsers(db *gorm.DB) error {
	err := db.AutoMigrate(&Users{})
	return err
}
