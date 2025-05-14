package routes

import (
	"net/http"
	"net/url"
	"os"
	"time"

	"github.com/fredeom/erp_server/models"
	"github.com/gofiber/fiber/v2"

	"github.com/golang-jwt/jwt/v5"
)

func (r MyRepository) Login(c *fiber.Ctx) error {
	m, _ := url.ParseQuery(string(c.Body()))
	login := m["login"]
	pass := m["pass"]

	userModel := &models.User{}
	err := r.DB.Preload("UserGroup").Where("login = ? AND pass = ?", login, pass).First(&userModel).Error

	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(&fiber.Map{"message": "could not get user: " + err.Error()})
	}

	userModel.LastLoginAt = time.Now().Format("2006-01-02 15:04:05")
	err = r.DB.Save(&userModel).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not update user's last_login_at: " + err.Error()})
	}

	auth_id, _ := models.GenerateRandomString(36)

	claims := jwt.MapClaims{
		"user_id": userModel.ID,
		"auth_id": auth_id,
		"exp":     time.Now().Add(time.Hour * 72).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)

	t, err := token.SignedString([]byte(os.Getenv("JWT_SECRET")))
	if err != nil {
		return c.SendStatus(fiber.StatusInternalServerError)
	}

	r.MC.Set(auth_id, []byte("1"), time.Hour)

	return c.JSON(fiber.Map{"token": t, "name": userModel.Name, "group": userModel.UserGroup.Name})
}

func (r MyRepository) Logout(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(jwt.MapClaims)
	auth_id := claims["auth_id"].(string)
	r.MC.Delete(auth_id)
	return c.JSON(&fiber.Map{"message": "logout succeeded"})
}

func (r MyRepository) AuthIdCheckMiddleware() fiber.Handler {
	return func(c *fiber.Ctx) error {
		user := c.Locals("user").(*jwt.Token)
		claims := user.Claims.(jwt.MapClaims)
		auth_id := claims["auth_id"].(string)
		if bc, _ := r.MC.Get(auth_id); string(bc) == "1" {
			return c.Next()
		} else {
			return c.Status(http.StatusBadRequest).JSON(&fiber.Map{"message": "could not get auth_id in memcache"})
		}
	}
}
