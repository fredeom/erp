package models

import "gorm.io/gorm"

type MenuPermission struct {
	ID         uint   `json:"id"`
	Name       string `json:"name"`
	MenuItemId uint   `json:"menu_item_id"`
}

type MenuPermissions struct {
	ID         uint    `gorm:"primary key;autoIncrement" json:"id"`
	Name       *string `json:"name"`
	MenuItemId *uint   `json:"menu_item_id"`
}

func MigrateMenuPermissions(db *gorm.DB) error {
	err := db.AutoMigrate(&MenuPermissions{})
	return err
}
