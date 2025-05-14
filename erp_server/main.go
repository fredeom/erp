package main

import (
	"log"
	"os"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/storage/memcache"
	"github.com/joho/godotenv"

	models "github.com/fredeom/erp_server/models"
	routes "github.com/fredeom/erp_server/routes"
	storage "github.com/fredeom/erp_server/storage"
	tcp_server "github.com/fredeom/erp_server/tcp_server"
)

func main() {
	err := godotenv.Load(".env")
	if err != nil {
		log.Fatal(err)
	}

	config := &storage.Config{
		Host:     os.Getenv("DB_HOST"),
		Port:     os.Getenv("DB_PORT"),
		Password: os.Getenv("DB_PASS"),
		User:     os.Getenv("DB_USER"),
		SSLMode:  os.Getenv("DB_SSLMODE"),
		DBName:   os.Getenv("DB_NAME"),
	}

	db, err := storage.NewConnection(config)

	if err != nil {
		log.Fatal("could not load the database")
	}

	err2 := models.MigrateAll(db)
	if err2 != nil {
		log.Fatal("could not migrate db")
	}

	store := memcache.New()

	r := storage.Repository{
		DB: db,
		MC: store,
	}

	tcpServer := tcp_server.NewTCPServer(&tcp_server.Config{
		Host: os.Getenv("TCP_HOST"),
		Port: os.Getenv("TCP_PORT"),
	}, r)
	go tcpServer.Run()

	app := fiber.New()
	routes.SetupRoutes(app, r)

	log.Fatal(app.Listen(":" + os.Getenv("SERVER_PORT")))
}
