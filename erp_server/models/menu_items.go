package models

import "gorm.io/gorm"

type MenuItem struct {
	ID        uint   `json:"id"`
	ParentId  uint   `json:"parent_id"`
	Name      string `gorm:"uniqueIndex: unique_index" json:"name"`
	PaneClass string `json:"pane_class"`
	Posit     uint   `json:"posit"`
	Active    uint   `json:"active"`
}

type MenuItems struct {
	ID        uint    `gorm:"primary key;autoIncrement" json:"id"`
	ParentId  *uint   `json:"parent_id"`
	Name      *string `gorm:"uniqueIndex: unique_index" json:"name"`
	PaneClass *string `json:"pane_class"`
	Posit     *uint   `json:"posit"`
	Active    *uint   `json:"active"`
}

func MigrateMenuItems(db *gorm.DB) error {
	err := db.AutoMigrate(&MenuItems{})
	return err
}
