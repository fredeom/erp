package models

import (
	"crypto/rand"
	"math/big"

	"gorm.io/gorm"
)

func GenerateRandomString(n int) (string, error) {
	const letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-"
	ret := make([]byte, n)
	for i := 0; i < n; i++ {
		num, err := rand.Int(rand.Reader, big.NewInt(int64(len(letters))))
		if err != nil {
			return "", err
		}
		ret[i] = letters[num.Int64()]
	}

	return string(ret), nil
}

func MigrateAll(db *gorm.DB) error {
	err := MigrateBooks(db)
	if err != nil {
		return err
	}
	err = MigrateMenuItems(db)
	if err != nil {
		return err
	}
	err = MigratePermissions(db)
	if err != nil {
		return err
	}
	err = MigrateUserGroups(db)
	if err != nil {
		return err
	}
	err = MigrateMenuPermissions(db)
	if err != nil {
		return err
	}
	err = MigrateUserGroupPermissions(db)
	if err != nil {
		return err
	}
	err = MigrateNotifications(db)
	if err != nil {
		return err
	}
	err = MigrateNotificationStatuses(db)
	if err != nil {
		return err
	}
	return MigrateUsers(db)
}
