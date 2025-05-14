// NotificationSend
package routes

import (
	"encoding/json"
	"net/http"
	"time"

	"github.com/fredeom/erp_server/models"
	"github.com/gofiber/fiber/v2"
	"github.com/golang-jwt/jwt/v5"
)

type NotificationText struct {
	Text string `json:"text"`
}

func (r MyRepository) NotificationSend(c *fiber.Ctx) error {
	var data NotificationText
	json.Unmarshal(c.Body(), &data)
	notif := models.Notification{Text: data.Text}
	err := r.DB.Save(&notif).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not create notification: " + err.Error()})
	}
	var ids []uint64
	err = r.DB.Model(&models.User{}).Pluck("ID", &ids).Error
	for _, id := range ids {
		notifStatus := models.NotificationStatus{NotificationId: notif.ID, UserId: id}
		err = r.DB.Save(&notifStatus).Error
		if err != nil {
			return c.Status(http.StatusBadRequest).JSON(
				&fiber.Map{"message": "could not create notification status: " + err.Error()})
		}
	}
	r.MC.Set("notification_time", []byte(time.Now().String()), time.Hour)
	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "notification has been created"})
}

func (r MyRepository) NotificationCount(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(jwt.MapClaims)
	user_id := claims["user_id"].(float64)

	var count int64
	err := r.DB.Model(&models.NotificationStatus{}).Where("user_id = ?", user_id).Count(&count).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get notification status count: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "notification has been created", "count": count})
}

func (r MyRepository) NotificationGetAll(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(jwt.MapClaims)
	user_id := claims["user_id"].(float64)

	notifications := []models.Notification{}
	err := r.DB.Joins("join notification_statuses on notification_statuses.user_id = ? and notifications.id = notification_statuses.notification_id", user_id).
		Find(&notifications).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get notification statuses: " + err.Error()})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "notifications have been retrieved", "notifications": notifications})
}

func (r MyRepository) NotificationSeen(c *fiber.Ctx) error {
	user := c.Locals("user").(*jwt.Token)
	claims := user.Claims.(jwt.MapClaims)
	user_id := claims["user_id"].(float64)

	id := c.Params("id")

	// idInt64, _ := strconv.ParseInt(id, 10, 64)
	err := r.DB.Where("user_id = ? and notification_id = ?", user_id, id).Delete(&models.NotificationStatus{}).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not remove notification status: " + err.Error()})
	}

	r.MC.Set("notification_time", []byte(time.Now().String()), time.Hour)

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "notification status has been deleted"})
}
