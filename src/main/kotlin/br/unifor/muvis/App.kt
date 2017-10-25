package br.unifor.muvis

import br.unifor.muvis.controller.CategoryControler
import br.unifor.muvis.controller.DirectorControler
import br.unifor.muvis.database.Categories
import br.unifor.muvis.database.Directors
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import spark.Spark.*

fun main(args: Array<String>) {

    Database.connect("jdbc:h2:./muvis.db", driver = "org.h2.Driver")
    transaction {
        create(Categories, Directors)
    }

    val logger = LoggerFactory.getLogger("App")

    path("/api", {
        before("/*", {req, res -> logger.info("A requesdt from ${req.host()} was received.") })
        path("/category",{
            get("/", CategoryControler.getAllCaltegory)
            get("/:id", CategoryControler.getCaltegory)
            post("/", CategoryControler.createCategory)
            put("/:id", CategoryControler.updateCategory)
            delete("/:id", CategoryControler.deleteCategory)
        })

        path("/director"){
            get("/", DirectorControler.getAllDirector)
            get("/:id", DirectorControler.getDirector)
            post("/", DirectorControler.createDirector)
            put("/:id", DirectorControler.updateDirector)
            delete("/:id", DirectorControler.deleteDirector)
        }
    })


}