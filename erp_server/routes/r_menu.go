package routes

import (
	"fmt"
	"net/http"

	"github.com/fredeom/erp_server/models"
	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
	"gorm.io/gorm"
)

func (r MyRepository) Menu(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(jwt.MapClaims)
	user_id := claims["user_id"].(float64)

	menuItems := &[]models.MenuItems{}
	err := r.DB.Joins("join permissions on permissions.name = ? AND permissions.user_id = ? AND menu_items.active = 1",
		gorm.Expr("CONCAT('menu_', menu_items.id)"), user_id).Order("posit").Find(&menuItems).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get menu_items"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "menu_items fetched successfully",
		"menus":   menuItems,
	})
}

func (r MyRepository) ITGroupCheckMiddleware() fiber.Handler {
	return func(c *fiber.Ctx) error {
		user := c.Locals("user").(*jwt.Token)
		claims := user.Claims.(jwt.MapClaims)
		user_id := claims["user_id"].(float64)

		modelUser := &models.User{}
		err := r.DB.First(&modelUser, user_id).Error
		if err != nil {
			return c.Status(http.StatusBadRequest).JSON(
				&fiber.Map{"message": "could not get user by id: " + fmt.Sprint(user_id)})
		}
		if modelUser.UserGroupId != 1 {
			return c.Status(http.StatusBadRequest).JSON(
				&fiber.Map{"message": "only IT can modify menu items. Given: " + modelUser.UserGroup.Name})
		}

		return c.Next()
	}
}

func (r MyRepository) MenuFull(c *fiber.Ctx) error {
	menuItems := &[]models.MenuItems{}
	err := r.DB.Order("posit").Find(&menuItems).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get menu_items full list"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "menu_items full list fetched successfully",
		"menus":   menuItems,
	})
}

func (r MyRepository) MenuAdd(c *fiber.Ctx) error {
	menuItem := models.MenuItem{}
	err := c.BodyParser(&menuItem)

	if err != nil {
		return c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed: " + err.Error()})
	}

	err = r.DB.Create(&menuItem).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not create menu_item: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "menu_item has been added"})
}

func (r MyRepository) MenuEdit(c *fiber.Ctx) error {
	menuItem := models.MenuItem{}
	err := c.BodyParser(&menuItem)

	if err != nil {
		return c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed: " + err.Error()})
	}

	err = r.DB.Save(&menuItem).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not update menu_item: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "menu_item has been updated"})
}

func (r MyRepository) MenuDelete(c *fiber.Ctx) error {
	menuItem := models.MenuItem{}
	err := c.BodyParser(&menuItem)

	if err != nil {
		return c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed: " + err.Error()})
	}

	err = r.DB.Delete(&menuItem).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not delete menu_item by id: " + fmt.Sprintf("%d", menuItem.ID)})
	}
	return c.Status(http.StatusOK).JSON(&fiber.Map{"message": "menu item has been deleted"})
}

func (r MyRepository) MenuOrder(c *fiber.Ctx) error {
	menuItems := &[]models.MenuItem{}
	err := c.BodyParser(&menuItems)

	if err != nil {
		return c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed: " + err.Error()})
	}

	for _, menuItem := range *menuItems {
		err = r.DB.Model(&menuItem).Where("id = ?", menuItem.ID).Update("posit", menuItem.Posit).Error
		if err != nil {
			return c.Status(http.StatusUnprocessableEntity).JSON(
				&fiber.Map{"message": "can't update: " + err.Error()})
		}
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{"message": "menu items new order established"})
}

func (r MyRepository) MenuPermissionGet(c *fiber.Ctx) error {
	id := c.Params("menu_id")
	menuPermissions := &[]models.MenuPermission{}

	err := r.DB.Where("menu_item_id = ?", id).Find(&menuPermissions).Error

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "can't get permissions for menu item with id " +
				fmt.Sprintf(id) + ": " + err.Error()})
	}
	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message":         "menu_permissions list fetched successfully",
		"menuPermissions": menuPermissions,
	})
}
