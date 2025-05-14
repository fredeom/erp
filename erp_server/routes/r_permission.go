package routes

import (
	"net/http"
	"strconv"
	"strings"

	"github.com/fredeom/erp_server/models"
	"github.com/fredeom/erp_server/services"
	"github.com/fredeom/erp_server/storage"
	"github.com/gofiber/fiber/v2"
)

func (r MyRepository) PermissionDelete(c *fiber.Ctx) error {
	permissionName := c.Params("permissionName")

	err := r.DB.Where("name = ?", permissionName).
		Delete(&models.UserGroupPermission{}).Error

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not delete user_group permission"})
	}

	err = r.DB.Where("name = ?", permissionName).Delete(&models.MenuPermission{}).Error

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not delete menu permission"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "user_groups and menu permission deleted successfully",
	})
}

func (r MyRepository) PermissionAdd(c *fiber.Ctx) error {
	permissionName := c.Params("permissionName")
	menuId := c.Params("menuId")

	menuID, err := strconv.ParseInt(menuId, 10, 64)
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not add menu permission: " + err.Error()})
	}

	menuPermission := &models.MenuPermission{
		Name:       permissionName,
		MenuItemId: uint(menuID),
	}

	err = r.DB.Create(&menuPermission).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not add menu permission: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message":        "menu permission added successfully",
		"menuPermission": menuPermission,
	})
}

func (r MyRepository) PermissionApply(c *fiber.Ctx) error {
	listPermissionGroupId := &[]string{}
	err := c.BodyParser(&listPermissionGroupId)

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not parse permission group list: " + err.Error()})
	}

	for _, permissionGroup := range *listPermissionGroupId {
		s := strings.Split(permissionGroup, "///")
		err = services.ApplyPermissionByAffected(storage.Repository(r), s[0], s[1])
		if err != nil {
			return c.Status(http.StatusBadRequest).JSON(
				&fiber.Map{"message": "could not apply permission to group of users: " + err.Error()})
		}
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "group permissions applied successfully",
	})
}
