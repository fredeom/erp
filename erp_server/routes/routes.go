package routes

import (
	"os"

	"github.com/fredeom/erp_server/storage"
	jwtware "github.com/gofiber/contrib/jwt"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
)

type MyRepository storage.Repository

func SetupRoutes(app *fiber.App, r storage.Repository) {
	app.Use(logger.New())
	app.Post("/login", MyRepository(r).Login)
	app.Use(jwtware.New(jwtware.Config{
		SigningKey: jwtware.SigningKey{Key: []byte(os.Getenv("JWT_SECRET"))},
	}))
	app.Use(MyRepository(r).AuthIdCheckMiddleware())
	app.Post("/logout", MyRepository(r).Logout)
	api := app.Group("/api")
	// api.Post("/create-book", MyRepository(r).CreateBook)
	// api.Delete("delete-book/:id", MyRepository(r).DeleteBook)
	// api.Get("/get-book/:id", MyRepository(r).GetBookByID)
	// api.Get("/books", MyRepository(r).GetBooks)
	api.Get("/menu", MyRepository(r).Menu)
	api.Post("/notification", MyRepository(r).NotificationSend)
	api.Get("/notification", MyRepository(r).NotificationGetAll)
	apiNotification := api.Group("/notification")
	apiNotification.Get("/count", MyRepository(r).NotificationCount)
	apiNotification.Post("/seen/:id", MyRepository(r).NotificationSeen)
	apiMenu := api.Group("/menu")
	apiMenu.Use(MyRepository(r).ITGroupCheckMiddleware())
	apiMenu.Get("/full", MyRepository(r).MenuFull)
	apiMenu.Post("/add", MyRepository(r).MenuAdd)
	apiMenu.Post("/edit", MyRepository(r).MenuEdit)
	apiMenu.Post("/delete", MyRepository(r).MenuDelete)
	apiMenu.Post("/order", MyRepository(r).MenuOrder)
	apiMenuPermission := apiMenu.Group("/permission")
	apiMenuPermission.Get("/:menu_id", MyRepository(r).MenuPermissionGet)
	apiUser := api.Group("/user")
	apiUser.Get("/full", MyRepository(r).UserFull)
	apiUser.Use([]string{"/add", "/edit", "/delete", "/details"}, MyRepository(r).ITGroupCheckMiddleware())
	apiUser.Post("/add", MyRepository(r).UserAdd)
	apiUser.Post("/edit", MyRepository(r).UserEdit)
	apiUser.Post("/delete/:id", MyRepository(r).UserDelete)
	apiUser.Get("/details/:id", MyRepository(r).UserDetails)
	apiUserGroup := apiUser.Group("/group")
	apiUserGroup.Get("/full", MyRepository(r).UserGroupFull)
	apiUserGroup.Post("/add", MyRepository(r).UserGroupAdd)
	apiUserGroupPermission := apiUserGroup.Group("/permission")
	apiUserGroupPermission.Use(MyRepository(r).ITGroupCheckMiddleware())
	apiUserGroupPermission.Get("/:permissionName", MyRepository(r).UserGroupsForPermission)
	apiUserGroupPermission.Post("/delete/:permissionName/:userGroupId", MyRepository(r).UserGroupsPermissionDelete)
	apiUserGroupPermission.Post("/add/:permissionName/:userGroupId", MyRepository(r).UserGroupsPermissionAdd)
	apiPermission := api.Group("/permission")
	apiPermission.Use(MyRepository(r).ITGroupCheckMiddleware())
	apiPermission.Post("/delete/:permissionName", MyRepository(r).PermissionDelete)
	apiPermission.Post("/add/:permissionName/:menuId", MyRepository(r).PermissionAdd)
	apiPermission.Post("/apply", MyRepository(r).PermissionApply)
}
