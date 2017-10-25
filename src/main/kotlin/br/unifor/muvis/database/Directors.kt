package br.unifor.muvis.database

import org.jetbrains.exposed.dao.LongIdTable

object Directors: LongIdTable() {

    val name = varchar("name", 100)
    val age = integer("age")

}