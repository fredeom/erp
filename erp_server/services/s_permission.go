package services

import (
	"github.com/fredeom/erp_server/models"
	"github.com/fredeom/erp_server/storage"
)

func ApplyPermissionByAffected(r storage.Repository, permissionName string, userGroupId string) error {
	err := r.DB.Where("user_id IN (?) AND name = ?",
		r.DB.Model(&models.User{}).Select("id").Where("user_group_id = ?", userGroupId),
		permissionName).
		Delete(&models.Permission{}).Error

	if err != nil {
		return err
	}

	users := []models.User{}
	err = r.DB.Where("user_group_id = ?", userGroupId).Find(&users).Error

	if err != nil {
		return err
	}

	userGroupPermissions := []models.UserGroupPermission{}
	err = r.DB.Where("user_group_id = ? AND name = ?", userGroupId, permissionName).Find(&userGroupPermissions).Error
	if err != nil {
		return err
	}

	if len(userGroupPermissions) > 0 {
		for _, user := range users {
			err = r.DB.Create(&models.Permission{Name: permissionName, UserId: user.ID}).Error
			if err != nil {
				return err
			}
		}
	}

	return nil
}

func ApplyPermissionsByUserGroupChange(r storage.Repository, userId int64, newUserGroupId int64) error {
	err := r.DB.Where("user_id = ?", userId).Delete(&models.Permission{}).Error

	if err != nil {
		return err
	}

	userGroupPermissions := []models.UserGroupPermission{}
	err = r.DB.Where("user_group_id = ?", newUserGroupId).Find(&userGroupPermissions).Error
	if err != nil {
		return err
	}

	if len(userGroupPermissions) > 0 {
		for _, userGroupPermission := range userGroupPermissions {
			err = r.DB.Create(&models.Permission{Name: userGroupPermission.Name, UserId: uint(userId)}).Error
			if err != nil {
				return err
			}
		}
	}

	return nil
}
