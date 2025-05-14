package routes

import (
	"fmt"
	"net/http"

	"github.com/fredeom/erp_server/models"
	"github.com/gofiber/fiber/v2"
)

func (r MyRepository) CreateBook(c *fiber.Ctx) error {
	book := models.Book{}

	err := c.BodyParser(&book)

	if err != nil {
		c.Status(http.StatusUnprocessableEntity).JSON(
			&fiber.Map{"message": "request failed"})
		return err
	}

	err = r.DB.Create(&book).Error
	if err != nil {
		c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not create book"})
		return err
	}

	c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "book has been added"})
	return nil
}

func (r MyRepository) DeleteBook(c *fiber.Ctx) error {
	bookModel := models.Books{}
	id := c.Params("id")
	if id == "" {
		c.Status(http.StatusInternalServerError).JSON(&fiber.Map{
			"message": "id cannot be empty",
		})
		return nil
	}

	err := r.DB.Delete(bookModel, id)

	if err.Error != nil {
		c.Status(http.StatusBadRequest).JSON(&fiber.Map{
			"message": "could not delete book",
		})
		return err.Error
	}
	c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "book delete successfully",
	})
	return nil
}

func (r MyRepository) GetBooks(c *fiber.Ctx) error {
	bookModels := &[]models.Books{}

	err := r.DB.Find(bookModels).Error
	if err != nil {
		return c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get books"})
	}

	return c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "books fetched successfully",
		"data":    bookModels,
	})
}

func (r MyRepository) GetBookByID(c *fiber.Ctx) error {

	id := c.Params("id")
	bookModel := &models.Books{}
	if id == "" {
		c.Status(http.StatusInternalServerError).JSON(&fiber.Map{
			"message": "id cannot be empty",
		})
		return nil
	}

	fmt.Println("the ID is", id)

	err := r.DB.Where("id = ?", id).First(bookModel).Error
	if err != nil {
		c.Status(http.StatusBadRequest).JSON(
			&fiber.Map{"message": "could not get the book"})
		return err
	}
	c.Status(http.StatusOK).JSON(&fiber.Map{
		"message": "book id fetched successfully",
		"data":    bookModel,
	})
	return nil
}
