package br.unifor.muvis.controller

import br.unifor.muvis.database.Categories
import br.unifor.muvis.entity.Category
import com.google.gson.Gson
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response

class CategoryControler {

    companion object {

        val logger = LoggerFactory.getLogger(CategoryControler::class.java)
        val gson = Gson()

        val getAllCaltegory = { req: Request, res: Response ->

            val categories = ArrayList<Category>()

            transaction {
                Categories.selectAll().forEach {
                    categories.add(Category(it[Categories.id].value, it[Categories.name], it[Categories.description]))
                }
            }

            gson.toJson(categories)

        }

        val getCaltegory = { req: Request, res: Response ->

            val id = req.params("id").toLong()
            var category:Category? = null

            transaction {
                Categories.select({Categories.id.eq(id)}).forEach { category = Category(it[Categories.id].value, it[Categories.name], it[Categories.description]) }
            }

            if(category == null){
                "{}"
            } else {
                gson.toJson(category)
            }

        }

        val updateCategory = { req: Request, res: Response ->

            val json = req.body()

            val category = try {
                gson.fromJson(json, Category::class.java)
            } catch (e: Exception) {
                null
            }

            when {
                category == null -> {
                    logger.error("A request from ${req.ip()} was wrong and was discarded.")
                    """{"status":"ERROR", "description":"There is a error on data received"}"""
                }
                else -> {

                    transaction {
                        Categories.update({Categories.id.eq(req.params("id"))}){
                            it[name] = category.name
                            it[description] = category.description
                        }
                    }

                    logger.info("A request from ${req.ip()} inserted into database.")
                    """{"status":"OK", "description":"Category ${category.name} was updated"}"""

                }
            }

        }

        val deleteCategory = { req: Request, res: Response ->

            val id = req.params("id").toLong()
            var exist = false

            transaction {
                Categories.select({Categories.id.eq(id)}).forEach { exist = true }
            }

            if(exist){

                transaction {
                    Categories.deleteWhere { Categories.id.eq(id) }
                }

                logger.info("A request from ${req.ip()} inserted into database.")
                """{"status":"OK", "description":"Category with id $id was deleted"}"""

            } else {

                logger.error("A request from ${req.ip()} was wrong and was discarded.")
                """{"status":"ERROR", "description":"There isn't a category with id $id"}"""

            }


        }

        val createCategory = { req: Request, res: Response ->

            val json = req.body()

            val category = try {
                gson.fromJson(json, Category::class.java)
            } catch (e: Exception) {
                null
            }


            when {
                category == null || category.name == null -> {
                    logger.error("A request from ${req.ip()} was wrong and was discarded.")
                    """{"status":"ERROR", "description":"There is a error on data received"}"""
                }
                else -> {

                    transaction {
                        Categories.insert {
                            it[name] = category.name
                            it[description] = category.description
                        }
                    }

                    logger.info("A request from ${req.ip()} inserted into database.")
                    """{"status":"OK", "description":"Category ${category.name} was inserted"}"""

                }
            }

        }

    }

}