package routes

import (
	"net/http"
	"strconv"

	"github.com/fredeom/erp_server/models"
	"github.com/gofiber/fiber/v2"
)

func (r MyRepository) UserGroupFull(c *fiber.Ctx) error {
	userGroups := &[]models.UserGroup{}
	err := r.DB.Order("posit").Find(&userGroups).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get user_groups full list"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message":     "user_groups full list fetched successfully",
		"user_groups": userGroups,
	})
}

func (r MyRepository) UserGroupsForPermission(c *fiber.Ctx) error {
	permissionName := c.Params("permissionName")
	userGroups := &[]models.UserGroup{}
	err := r.DB.Joins(`join user_group_permissions on user_group_permissions.user_group_id = user_groups.id
					   and user_group_permissions.name = ?`, permissionName).Order("posit").Find(&userGroups).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get user_groups list"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message":     "user_groups for permission list fetched successfully",
		"user_groups": userGroups,
	})
}

func (r MyRepository) UserGroupsPermissionDelete(c *fiber.Ctx) error {
	permissionName := c.Params("permissionName")
	userGroupId := c.Params("userGroupId")

	err := r.DB.Where("name = ? AND user_group_id = ?", permissionName, userGroupId).
		Delete(&models.UserGroupPermission{}).Error

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not delete user_group permission"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "user_group permission deleted successfully",
	})
}

func (r MyRepository) UserGroupsPermissionAdd(c *fiber.Ctx) error {
	permissionName := c.Params("permissionName")
	userGroupId := c.Params("userGroupId")

	ugID, err := strconv.ParseInt(userGroupId, 10, 64)

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not add user_group permission: " + err.Error()})
	}

	userGroupPermission := models.UserGroupPermission{
		Name:        permissionName,
		UserGroupId: ugID,
	}

	err = r.DB.Create(&userGroupPermission).Error

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not create user_group permission record: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{"message": "user group permission has been added"})
}

func (r MyRepository) UserGroupAdd(c *fiber.Ctx) error {
	userGroup := models.UserGroup{}
	err := c.BodyParser(&userGroup)

	if err != nil {
		return c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed: " + err.Error()})
	}

	var count int64
	r.DB.Model(&models.UserGroup{}).Count(&count)

	userGroup.Posit = uint(count)

	err = r.DB.Save(&userGroup).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not add userGroup: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "userGroup has been added"})
}
