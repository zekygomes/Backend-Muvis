package br.unifor.muvis.controller

import br.unifor.muvis.database.Directors
import br.unifor.muvis.entity.Director
import com.google.gson.Gson
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response

class DirectorControler{

    companion object {

        val logger = LoggerFactory.getLogger(DirectorControler::class.java)
        val gson = Gson()

        val getAllDirector = { req: Request, res: Response ->

            val directors = ArrayList<Director>()

            transaction {
                Directors.selectAll().forEach{
                    directors.add(Director(it[Directors.id].value, it[Directors.name], it[Directors.age]))
                }
            }

            gson.toJson(directors)

        }

        val getDirector = { req: Request, res: Response ->
            val id = req.params("id").toLong()
            var director:Director? = null

            transaction {
                Directors.select({Directors.id.eq(id)}).forEach { director = Director(it[Directors.id].value, it[Directors.name], it[Directors.age]) }
            }

            if(director == null){
                "{}"
            } else {
                gson.toJson(director)
            }
        }

        val createDirector = { req: Request, res: Response ->

            val json = req.body()

            val director = try {
                gson.fromJson(json, Director::class.java)
            } catch (e: Exception) {
                null
            }


            when {
                director == null || director.name == null -> {
                    logger.error("A requisiçâo ${req.ip()} está incorreta.")
                    """{"status":"ERRO", "descrição":"Os dados recebidos contem erros"}"""
                }
                else -> {

                    transaction {
                        Directors.insert {
                            it[name] = director.name
                            it[age] = director.age
                        }
                    }

                    logger.info("A requisição ${req.ip()} foi inserida na base de dados.")
                    """{"status":"OK", "descrição":"Diretor ${director.name} foi inserido"}"""

                }
            }
        }

        val updateDirector = { req: Request, res: Response ->
            val json = req.body()

            val director = try {
                gson.fromJson(json, Director::class.java)
            } catch (e: Exception) {
                null
            }

            when {
                director == null -> {
                    logger.error("A requisiçâo ${req.ip()} está incorreta")
                    """{"status":"ERRO", "descrição":"Os dados recebidos contem erros"}"""
                }
                else -> {

                    transaction {
                        Directors.update({Directors.id.eq(req.params("id"))}){
                            it[name] = director.name
                            it[age] = director.age
                        }
                    }

                    logger.info("A requisição ${req.ip()} foi inserida na base de dados.")
                    """{"status":"OK", "descrição":"Os dados do Diretor ${director.name} foram alterados"}"""

                }
            }
        }

        val deleteDirector = {req: Request, res: Response ->

        val id = req.params("id").toLong()
            var exist = false

            transaction {
                Directors.select({Directors.id.eq(id)}).forEach { exist = true }
            }

            if(exist){

                transaction {
                    Directors.deleteWhere { Directors.id.eq(id) }
                }

                logger.info("A requisição ${req.ip()} foi inserida na base de dados.")
                """{"status":"OK", "description":"O diretor com o id $id foi deletado"}"""

            } else {

                logger.error("A requisiçâo ${req.ip()} está incorreta")
                """{"status":"ERROR", "descrição":"O diretor com o id $id não foi encontrado"}"""

            }

        }
    }
}