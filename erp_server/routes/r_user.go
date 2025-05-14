package routes

import (
	"net/http"
	"strconv"
	"time"

	"github.com/fredeom/erp_server/models"
	"github.com/fredeom/erp_server/services"
	"github.com/fredeom/erp_server/storage"
	"github.com/gofiber/fiber/v2"
)

func (r MyRepository) UserFull(c *fiber.Ctx) error {
	users := &[]models.UserShort{}
	err := r.DB.Model(&models.User{}).Preload("UserGroup").Order("name").Find(&users).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get user full list"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message":     "user full list fetched successfully",
		"user_shorts": users,
	})
}

func (r MyRepository) UserAdd(c *fiber.Ctx) error {
	user := models.User{}
	err := c.BodyParser(&user)

	if err != nil {
		return c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed: " + err.Error()})
	}

	user.CreatedAt = time.Now().Format(time.DateTime)

	err = r.DB.Save(&user).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not add user: " + err.Error()})
	}

	err = services.ApplyPermissionsByUserGroupChange(storage.Repository(r), int64(user.ID), int64(user.UserGroupId))
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not apply permissions user: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "user has been added"})
}

func (r MyRepository) UserEdit(c *fiber.Ctx) error {
	user := models.User{}
	err := c.BodyParser(&user)

	if err != nil {
		return c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed: " + err.Error()})
	}

	user.UpdatedAt = time.Now().Format(time.DateTime)

	err = r.DB.Save(&user).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not edit user: " + err.Error()})
	}

	err = services.ApplyPermissionsByUserGroupChange(storage.Repository(r), int64(user.ID), int64(user.UserGroupId))
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not apply permissions user: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "user has been edited"})
}

func (r MyRepository) UserDelete(c *fiber.Ctx) error {
	userId := c.Params("id")

	err := r.DB.Where("id = ?", userId).Delete(&models.User{}).Error

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not delete user"})
	}
	userID, err2 := strconv.Atoi(userId)
	if err2 != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not convert user id to int: " + err2.Error()})
	}
	err = services.ApplyPermissionsByUserGroupChange(storage.Repository(r), int64(userID), int64(-1))
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not apply permissions user: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "user deleted successfully",
	})
}

func (r MyRepository) UserDetails(c *fiber.Ctx) error {
	userId := c.Params("id")

	user := &models.User{}
	err := r.DB.Where("id = ?", userId).Find(user).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get user"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "user fetched successfully",
		"user":    user,
	})
}
